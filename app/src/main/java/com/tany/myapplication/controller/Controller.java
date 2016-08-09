package com.tany.myapplication.controller;

import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import com.tany.myapplication.MyApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by tany on 2016/8/8.
 */
public class Controller {
    private Context mAppContext;
    private LocalBroadcastManager mLBM;//用于发送各种广播
    private static Controller mController;


    private Controller() {
        this.mAppContext = MyApplication.getAppContext();
    }

    public static Controller getInstance() {
        if (mController == null) {
            synchronized (Controller.class) {
                if (mController == null) {
                    mController = new Controller();
                }
            }
        }
        return mController;
    }



    //创建一个全局线程池，来执行model层的操作，避免无限制的创建线程
    private ExecutorService executorService = Executors.newCachedThreadPool();

    //对外接口 获取得到线程池
    public ExecutorService getGlobleThreadPool() {
        return executorService;
    }


}