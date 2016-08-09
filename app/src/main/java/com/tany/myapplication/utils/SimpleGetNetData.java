package com.tany.myapplication.utils;

/**
 * Created by tany on 2016/7/27
 */


import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.tany.myapplication.MyApplication;
import com.tany.myapplication.controller.Controller;
import com.tany.myapplication.utils.diskcache.cachemanager.DiskCacheManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * 一个简单的网络数据获取器，将缓存、网络请求、Json数据解析封装在一起的
 * 各部分都可自由切换你喜欢的
 * <p/>
 * 一般来说；
 * 缓存工具：DiskLruCache、AsimpleCache都是不错的选择
 * 网络：用okhttp吧
 * Json：用fastJson吧，其他的都没他快，我试过，真的
 * <p/>
 * 整体功能描述：
 * 传入（tag）、一个url，一个需求，一个泛型，重写的回调，
 * get(Object tag,String url,boolea isNewFirst, Class clazz)
 * 提供一个回调，成功或失败的回调(异步，回调需要使用runOnUIThread切换)；
 * <p/>
 * 附注：
 * diskLruCache的一些进一步讲解，和具体配置可以参考这里：
 * 1.http://blog.csdn.net/guolin_blog/article/details/28863651
 * 2.http://www.cnblogs.com/tianzhijiexian/p/4252664.html
 * <p/>
 * DiskLruCache几个暂时没有用到的
 * 计算缓存大小和删除缓存空间
 * size()：这个方法会返回当前缓存路径下所有缓存数据的总字节数，以byte为单位
 * delete()：这个方法用于将所有的缓存数据全部删除
 * <p/>
 * OkHttp更多可用的方法：
 * cancel：用于根据tag进行批量取消
 */

public class SimpleGetNetData {

    private static SimpleGetNetData mInstance;

    private SimpleGetNetData() {
    }


    //单例
    public static SimpleGetNetData getInstance() {

        if (mInstance == null) {
            synchronized (SimpleGetNetData.class) {
                if (mInstance == null) {
                    mInstance = new SimpleGetNetData();
                }
            }
        }
        return mInstance;
    }


    //每一次请求都会开启一个线程，为避免线程过多，最好使用全局线程池
    public void setResultCallBack(final String url, final boolean isNewFirst, final Class clazz, final ResultCallBack callBack) {

        Controller.getInstance().getGlobleThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                setResultCallBack(null, url, isNewFirst, clazz, callBack);
            }
        });
    }

    //重载，对外提供提供一个额外的tag，用于在对应的onStop、onDestroy中进行okhttp的cancel操作
    public void setResultCallBack(final Object tag, final String url, final boolean isNewFirst, final Class clazz, final ResultCallBack callBack) {

        Controller.getInstance().getGlobleThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                setResultCallBack1(tag, url, isNewFirst, clazz, callBack);
            }
        });
    }


    //*************************************内部实现

    //为OkHttp提供一个Tag，用于在页面关闭时取消不再需要的 联网需求
    private void setResultCallBack1(Object tag, String url, boolean isNewFirst, Class clazz, ResultCallBack callBack) {
        //使用DiskLruCache做缓存用到，路径在对应包名下（如挂载sd，在Android/data/包名/cache下），默认缓存10m，这里配置5M
        DiskCacheManager.getInstance().open(MyApplication.getAppContext(), 50000000);
        getDataFromNet(tag, url, isNewFirst, clazz, callBack);
    }


    //根据需求，来决定数据的获取源：缓存、网络。
    private void getDataFromNet(Object tag, String url, boolean isNewFirst, Class clazz, ResultCallBack callBack) {

        //判断是否需要
        if (isNewFirst) {//强制联网获取最新数据
            getDataByOk(tag, url, clazz, callBack);//通过ok获取数据

        } else {//优先获取缓存数据，只有当缓存为空时，才联网加载数据
            String result = DiskCacheManager.getInstance().getAsString(url);
            Log.e("SimpleGetNetData", "result" + result);
            if (result == null) {
                getDataByOk(tag, url, clazz, callBack);//通过ok获取数据
            } else {
                parsonJson1(false, result, clazz, url, callBack);//解析缓存数据
            }
        }
    }


    //网络获取数据
    private void getDataByOk(Object tagobj, final String url, final Class clazz, final ResultCallBack callBack) {

        OkHttpUtils.get().url(url).tag(tagobj).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                callBack.onError(e.getMessage());
                Log.e("SimpleGetNetData", "getDataByOk 错误原因" + e.getMessage());
            }

            @Override
            public void onResponse(final String json, int id) {
                //涉及IO操作（开分线程）
                Controller.getInstance().getGlobleThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        parsonJson1(true, json, clazz, url, callBack);
                    }
                });
            }
        });

    }


    //解析Json数据，并做磁盘缓存（整体都在分线程）
    private <T> void parsonJson1(boolean fromNet, final String json, final Class<T> clazz, final String url, final ResultCallBack callBack) {
        try {
            T t = JSON.parseObject(json, clazz);//这里可以轻松切换至你先喜欢的Json解析，比如Gson、Jackson、FastJson

            callBack.onSuccess(fromNet, json, t);//解析成功，进行回调（需在对应位置切换至UI线程）

            DiskCacheManager.getInstance().put(url, json);
            DiskCacheManager.getInstance().flush();//会讲操作写入日志这个一定要写，不然不会有缓存，但可以写在其他地方，比如Activity的onStop
            Log.e("SimpleGetNetData", "parsonJson1 解析并成功保存缓存");

        } catch (Exception e) {
            callBack.onError(e.getMessage());
            Log.e("SimpleGetNetData", clazz.getSimpleName() + "  parsonJson1出错，原因：" + e.getMessage() + " url:" + url);
        }
    }


    //回调接口
    public interface ResultCallBack<T> {
        //成功返回一个指定对象;参数status 0:来源于缓存；1
        /**
         * @param fromNet  内容是否来自网络还是缓存。
         * @param response 请求到的字符串
         * @param t        泛型。用于返回指定类型的解析对象
         */
        void onSuccess(boolean fromNet, String response, T t);

        //异常返回一个原因结果用于说明
        void onError(String err);
    }


    //额外的方法

    //强制写入缓存日志
    public void flush(){
        DiskCacheManager.getInstance().flush();
    }

    //对外接口。功能：清除缓存
    public void clearCache(){
        DiskCacheManager.getInstance().clearCache();
    }

    //对外接口。 功能：查询缓存文件大小。
    public long cacheSize(){
       return DiskCacheManager.getInstance().size() / 1024;
    }



    //对外接口 。根据tag 取消队列中的请求
    public void cancelByTag(Object tag){
        OkHttpUtils.getInstance().cancelTag(tag);
    }

}
