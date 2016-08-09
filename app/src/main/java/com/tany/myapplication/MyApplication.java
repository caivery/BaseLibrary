package com.tany.myapplication;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

/**
 * Created by tany on 2016/8/8.
 */
public class MyApplication  extends Application {
    private static Context appContext;

    @Override
    public void onCreate() {
            super.onCreate();
        this.appContext = this;//提供一个Application相关的AppContext。

        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)//配置Fresco
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(this, config);

    }

    public static Context getAppContext() {
        return appContext;
    }
}
