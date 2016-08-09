package com.tany.myapplication.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.tany.myapplication.R;
import com.tany.myapplication.model.bean.JsonDiscover;
import com.tany.myapplication.utils.SimpleGetNetData;
import com.tany.myapplication.view.adapter.GvAdapter;

import java.util.ArrayList;
import java.util.List;

//磁盘缓存
public class DiskLruCahceActivity extends AppCompatActivity {
    private Context context;
    private TextView tv_status;
    private TextView tv_souce;
    private TextView tv_time;
    private TextView tv_cache_size;
    private TextView tv_cache_clear;
    private XRecyclerView rv;
    private GvAdapter gvAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disk_lru_cahce);
        this.context = this;
        initView();//初始化view
        initData();//
    }


    //初始化控件和简单的事件处理
    private void initView() {
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_souce = (TextView) findViewById(R.id.tv_souce);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_cache_size = (TextView) findViewById(R.id.tv_cache_size);
        tv_cache_clear = (TextView) findViewById(R.id.tv_cache_clear);

        rv = (XRecyclerView) findViewById(R.id.rv);
        rv.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                refresh();
            }

            @Override
            public void onLoadMore() {
                loadMore();
            }
        });


        //点击清空缓存
        tv_cache_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleGetNetData.getInstance().clearCache();//点击清空缓存
                Toast.makeText(DiskLruCahceActivity.this, "缓存清空完成", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void initData() {
        rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        gvAdapter = new GvAdapter(context, list);
        rv.setAdapter(gvAdapter);

        //首次加载
        status = normal;
        getDataFromNet(false);
    }

    //保存数据 容器（务必先new出来）
    private List<JsonDiscover.DataBean.ItemsBean> list = new ArrayList<>();


    //网络地址
    private String url = "http://api.wusejia.com/mobile/discover/special?limit=16";
    private String urlTemp = url;//动态生成，用于加载更多

    //刷新操作
    private void refresh() {
        status = refresh;
        urlTemp = url + "&offset=" + 0;
        getDataFromNet(true);
    }


    //加载更多操作
    private void loadMore() {
        status = loadMore;
        urlTemp = url + "&offset=" + list.size();
        getDataFromNet(false);
    }

    long startMills;//操作开始mill数
    long endMills;//操作结束mill数

    private void getDataFromNet(boolean isNewFist) {

        Log.e("DiskLruCahceActivity", "getDataFromNet " + urlTemp);
        startMills = SystemClock.uptimeMillis();

        SimpleGetNetData.getInstance().setResultCallBack(urlTemp, isNewFist, JsonDiscover.class, new SimpleGetNetData.ResultCallBack<JsonDiscover>() {

            @Override
            public void onSuccess(final boolean fromNet, String respons, final JsonDiscover jsonDiscoverTemp) {
                Log.e("DiskLruCahceActivity", "getDataFromNet 成功，url:" + url + "获取到缓存：" + respons);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        endMills = SystemClock.uptimeMillis();
                        rv.refreshComplete();
                        rv.loadMoreComplete();

                        List<JsonDiscover.DataBean.ItemsBean> itemsTemp = jsonDiscoverTemp.getData().getItems();
                        showData(fromNet, itemsTemp);//分类型显示数据
                    }
                });
            }

            @Override
            public void onError(String err) {
                Log.e("DiskLruCahceActivity", "getDataFromNet onError " + err);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rv.refreshComplete();
                        rv.loadMoreComplete();
                    }
                });
            }
        });

    }


    private final int normal = 0;
    private final int refresh = 1;
    private final int loadMore = 2;
    private int status;

    //显示底部数据
    private void showData(boolean fromNet, List<JsonDiscover.DataBean.ItemsBean> itemstemp) {
        switch (status) {
            case normal:
            case refresh:

                list.clear();
                list.addAll(itemstemp);
                gvAdapter.notifyDataSetChanged();
                break;

            case loadMore:
                //刷新底部 的gridView样式数据数据
                int preSize = list.size();//之前的大小 这里真的需要么
                list.addAll(itemstemp);
                gvAdapter.notifyItemRangeChanged(preSize, list.size());
                break;
        }

        tv_status.setText("状态：" + status);
        //设置一些状态
        if (fromNet) {
            tv_souce.setText("来源：网络");
        } else {
            tv_souce.setText("来源：缓存");
        }
        tv_time.setText("耗时：" + (endMills - startMills));

        tv_cache_size.setText("大小kb：" + SimpleGetNetData.getInstance().cacheSize());

    }

    @Override
    protected void onStop() {
//        SimpleGetNetData.getInstance().flush();//强制写入日志，避免查不到缓存
//        SimpleGetNetData.getInstance().cancelByTag(this);//取消所有tag标记的联网请求
        super.onStop();
    }
}
