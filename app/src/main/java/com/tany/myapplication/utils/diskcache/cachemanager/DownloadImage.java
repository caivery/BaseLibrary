package com.tany.myapplication.utils.diskcache.cachemanager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by sujizhong on 16/5/16.
 */
public class DownloadImage {

    public static boolean downloadImageforString(String imageUrlm, OutputStream outputStream) {
        HttpURLConnection connection = null;
        BufferedOutputStream bufferedOutputStream = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            URL url = new URL(imageUrlm);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(1000 * 5);
            bufferedInputStream = new BufferedInputStream(connection.getInputStream(), 8 * 1024);
            bufferedOutputStream = new BufferedOutputStream(outputStream, 8 * 1024);
            int length = 0;
            while ((length = bufferedInputStream.read()) != -1) {
                bufferedOutputStream.write(length);
            }
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (bufferedOutputStream != null) {
                    bufferedOutputStream.close();
                }
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
