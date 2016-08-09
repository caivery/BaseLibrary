package com.tany.myapplication.utils.diskcache.cachemanager;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.tany.myapplication.utils.diskcache.io.DiskLruCache;
import com.tany.myapplication.utils.diskcache.utils.BitmapUtil;
import com.tany.myapplication.utils.diskcache.utils.StreamUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by sujizhong on 16/5/16.
 */
public class DiskCacheManager extends BaseCacheManager {

    private static final String TAG = DiskCacheManager.class.getSimpleName();

    private static final int DEFAULT_DISK_CACHE_SIZE = 10 * 1024 * 1024;
    private static final int VALUE = 1;
    private final String mPackage_Name = "bitmap";

    private static DiskCacheManager mDiskCache = null;
    private DiskLruCache mDiskLruCache = null;

    public static DiskCacheManager getInstance() {
        if (mDiskCache == null) {
            synchronized (DiskCacheManager.class) {
                if (mDiskCache == null) {
                    mDiskCache = new DiskCacheManager();
                }
            }
        }
        return mDiskCache;
    }

    public int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    public String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private class DownLoadRunnable implements Runnable {

        private String mImageUrl;

        public DownLoadRunnable(String imageUrl) {
            this.mImageUrl = imageUrl;
        }

        @Override
        public void run() {
            if (!TextUtils.isEmpty(mImageUrl)) {
                String md5 = hashKeyForDisk(mImageUrl);
                DiskLruCache.Editor editor;
                if (mDiskLruCache != null) {
                    try {
                        editor = mDiskLruCache.edit(md5);
                        if (editor != null) {
                            OutputStream outputStream = editor.newOutputStream(0);

                            if (DownloadImage.downloadImageforString(mImageUrl, outputStream)) {
                                editor.commit();
                            } else {
                                editor.abort();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * @param context 默认为文件名为 bitmap图像
     *                说明 当版本发生变化后 会自动删除之前的数据
     */
    public void open(Context context) {
        if (context == null) {
            return;
        }
        try {
            File file = getDiskCacheDir(context, mPackage_Name);
            if (!file.isFile()) {
                file.mkdir();
            }
            mDiskLruCache = DiskLruCache.open(file, getAppVersion(context), VALUE, DEFAULT_DISK_CACHE_SIZE);
        } catch (Exception e) {
            Log.e(TAG, "mDiskLruCache is open faile");
            e.printStackTrace();
        }
    }

    /**
     * @param context      上下文
     * @param saveFileName 创建文件名
     */
    public void open(Context context, String saveFileName) {
        if (context == null || TextUtils.isEmpty(saveFileName)) return;
        try {
            File file = getDiskCacheDir(context, saveFileName);
            if (!file.isFile()) {
                file.mkdir();
            }
            mDiskLruCache = DiskLruCache.open(file, getAppVersion(context), VALUE, DEFAULT_DISK_CACHE_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context      上下文
     * @param maxCacheSize 创建大小
     *                     用户自己定义相关创建时的信息 如果cacheSize <= 0 则默认为10M
     */
    public void open(Context context, int maxCacheSize){
        if(context == null)return;
        if(maxCacheSize <= 0){
            maxCacheSize = DEFAULT_DISK_CACHE_SIZE;
        }
        try {
            File file = getDiskCacheDir(context, mPackage_Name);
            if(!file.isFile()){
                file.mkdir();
            }
            mDiskLruCache = DiskLruCache.open(file, getAppVersion(context), VALUE, maxCacheSize);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @param context      上下文
     * @param saveFileName 保存文件名
     * @param maxCacheSize 创建大小
     *                     用户自己定义相关创建时的信息 如果cacheSize <= 0 则默认为10M
     */
    public void open(Context context, String saveFileName, int maxCacheSize) {
        if (context == null || TextUtils.isEmpty(saveFileName)) {
            return;
        }
        if (maxCacheSize <= 0) {
            maxCacheSize = DEFAULT_DISK_CACHE_SIZE;
        }
        try {
            File file = getDiskCacheDir(context, saveFileName);
            if (!file.isFile()) {
                file.mkdir();
            }
            mDiskLruCache = DiskLruCache.open(file, getAppVersion(context), VALUE, maxCacheSize);
        } catch (Exception e) {
            Log.e(TAG, "mDiskLruCache is open faile");
            e.printStackTrace();
        }
    }

    /**
     * @param context      上下文
     * @param dir          cache路径
     * @param maxCacheSize 创建大小
     *                     用户自己定义相关创建时的信息 如果cacheSize <= 0 则默认为10M
     */
    public void open(Context context, File dir, int maxCacheSize) {
        if (context == null || dir == null) return;
        if (maxCacheSize <= 0) {
            maxCacheSize = DEFAULT_DISK_CACHE_SIZE;
        }
        try {
            if (!dir.isFile()) {
                dir.mkdir();
            }
            mDiskLruCache = DiskLruCache.open(dir, getAppVersion(context), VALUE, maxCacheSize);
        } catch (IOException e) {
            Log.e(TAG, "mDiskLruCache is open faile");
            e.printStackTrace();
        }
    }

    /**
     * @param key 直接从网络下载
     *            说明：根据地址下载并存储到disk中
     */
    public void download(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        new Thread(new DownLoadRunnable(key)).start();
    }

    /**
     * @param key 通用获取方式
     * @return InputStream
     */
    public InputStream get(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        try {
            String md5 = hashKeyForDisk(key);
            if (mDiskLruCache != null) {
                DiskLruCache.Snapshot snapShot = mDiskLruCache.get(md5);
                if (snapShot != null) {
                    InputStream is = snapShot.getInputStream(0);
                    return is;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param key
     * @param value 说明 保存字符串到diskCache
     */
    public void put(String key, String value) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) return;
        DiskLruCache.Editor editor = null;
        BufferedWriter bw = null;
        String md5 = hashKeyForDisk(key);
        try {
            editor = mDiskLruCache.edit(md5);
            if (editor == null) return;
            OutputStream outputStream = editor.newOutputStream(0);
            bw = new BufferedWriter(new OutputStreamWriter(outputStream));
            bw.write(value);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                editor.abort();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            IOclose(bw);
        }
    }

    /**
     * @param key 说明：获取String
     */
    public String getAsString(String key) {
        InputStream inputStream = null;
        inputStream = get(key);
        if (inputStream != null) {
            String result = null;
            try {
                result = StreamUtil.readFully(new InputStreamReader(inputStream, StreamUtil.UTF_8));
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOclose(inputStream);
            }
        }
        return null;
    }

    /**
     * @param key
     * @param jsonObject 说明：保存json对象到diskCache
     */
    public void put(String key, JSONObject jsonObject) {
        if (TextUtils.isEmpty(key) || jsonObject == null) {
            return;
        }
        put(key, jsonObject.toString());
    }

    /**
     * @param key 说明：获取json对象
     */
    public JSONObject getAsJson(String key) {
        if (TextUtils.isEmpty(key)) return null;
        String reuslt = getAsString(key);
        if (!TextUtils.isEmpty(reuslt)) {
            try {
                return new JSONObject(reuslt);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @param key
     * @param jsonArray 说明：保存json数组到diskCache
     */
    public void put(String key, JSONArray jsonArray) {
        if (TextUtils.isEmpty(key) || jsonArray == null) return;
        put(key, jsonArray.toString());
    }

    /**
     * @param key 说明：保存json数组到diskCache
     * @return JsonArray
     */
    public JSONArray getAsJsonArray(String key) {
        if (TextUtils.isEmpty(key)) return null;
        String result = getAsString(key);
        if (!TextUtils.isEmpty(result)) {
            try {
                return new JSONArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @param key
     * @param value 说明：保存byte[]数组到diskCache
     */
    public void put(String key, byte[] value) {
        if (TextUtils.isEmpty(key) || value == null) return;
        OutputStream out = null;
        DiskLruCache.Editor editor = null;
        try {
            String md5 = hashKeyForDisk(key);
            editor = mDiskLruCache.edit(md5);
            if (editor == null) {
                return;
            }
            out = editor.newOutputStream(0);
            out.write(value);
            out.flush();
            editor.commit();//write CLEAN
        } catch (Exception e) {
            e.printStackTrace();
            try {
                editor.abort();//write REMOVE
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        } finally {
            IOclose(out);
        }
    }

    /**
     * @param key 说明：获取byte[]
     */
    public byte[] getAsBytes(String key) {
        if (TextUtils.isEmpty(key)) return null;
        byte[] res = null;
        InputStream is = get(key);
        if (is == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] buf = new byte[256];
            int len = 0;
            while ((len = is.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            res = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOclose(baos);
        }
        return res;
    }

    /**
     * @param key
     * @param bitmap 说明:保存Bitmap对象到diskCache
     */
    public void put(String key, Bitmap bitmap) {
        if (TextUtils.isEmpty(key) || bitmap == null) return;
        put(key, BitmapUtil.bitmap2Bytes(bitmap));
    }

    /**
     * @param key 说明：获取Bitmap对象
     */
    public Bitmap getAsBitmap(String key) {
        if (TextUtils.isEmpty(key)) return null;
        byte[] bytes = getAsBytes(key);
        if (bytes == null) return null;
        return BitmapUtil.bytes2Bitmap(bytes);
    }

    /**
     * @param key
     * @param value 说明：保存Drawable到diskCache
     */
    public void put(String key, Drawable value) {
        if (TextUtils.isEmpty(key) || value == null) return;
        put(key, BitmapUtil.drawable2Bitmap(value));
    }

    /**
     * @param key 说明：获取Drawable对象
     */
    public Drawable getAsDrawable(String key) {
        if (TextUtils.isEmpty(key)) return null;
        byte[] bytes = getAsBytes(key);
        if (bytes == null) {
            return null;
        }
        return BitmapUtil.bitmap2Drawable(BitmapUtil.bytes2Bitmap(bytes));
    }

    /**
     * @param key
     * @param value
     */
    public void put(String key, Serializable value) {
        if (TextUtils.isEmpty(key) || value == null) return;
        DiskLruCache.Editor editor = null;
        ObjectOutputStream oos = null;
        try {
            if (mDiskLruCache != null) {
                String md5 = hashKeyForDisk(key);
                editor = mDiskLruCache.edit(md5);
                OutputStream os = editor.newOutputStream(0);
                oos = new ObjectOutputStream(os);
                oos.writeObject(value);
                oos.flush();
                editor.commit();
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                editor.abort();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            IOclose(oos);
        }
    }

    /**
     * @param key
     */
    public <T> T getAsSerializable(String key) {
        if (TextUtils.isEmpty(key)) return null;
        T t = null;
        InputStream is = get(key);
        ObjectInputStream ois = null;
        if (is == null) return null;
        try {
            ois = new ObjectInputStream(is);
            t = (T) ois.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOclose(ois);
        }
        return t;
    }

    /**
     * @param imageUrl 说明：根据key移除该资源
     */
    public boolean remove(String imageUrl) {
        if (TextUtils.isEmpty(imageUrl)) {
            return false;
        }
        try {
            String md5 = hashKeyForDisk(imageUrl);
            if (mDiskLruCache != null) {
                return mDiskLruCache.remove(md5);
            } else {
                Log.e(TAG, "mDiskLruCache is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 一般在onPause中调用
     */
    public void flush() {
        if (mDiskLruCache != null) {
            try {
                mDiskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 清空cache 调用该方法之后DiskLruCache将关闭
     */
    public void clearCache() {
        if (mDiskLruCache != null) {
            try {
                mDiskLruCache.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭cache 一般在onDestory中调用
     */
    public void close() {
        if (mDiskLruCache != null) {
            try {
                mDiskLruCache.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断是否已经关闭
     */
    public boolean isClosed() {
        if (mDiskLruCache != null) {
            return mDiskLruCache.isClosed();
        }
        return true;
    }

    /**
     * 得到缓存大小
     */
    public long size() {
        if (mDiskLruCache != null) {
            return mDiskLruCache.size();
        }
        return 0;
    }

    /**
     * 设置max cache 大小
     */
    public void setMaxSize(long maxSize) {
        if (mDiskLruCache != null) {
            mDiskLruCache.setMaxSize(maxSize);
        }
    }

    /**
     * 获取最大缓存
     */
    public long getMaxSize() {
        if (mDiskLruCache != null) {
            return mDiskLruCache.maxSize();
        }
        return 0;
    }

    /**
     * 获取cache路径
     */
    public File getDirectory() {
        if (mDiskLruCache != null) {
            return mDiskLruCache.getDirectory();
        }
        return null;
    }
}
