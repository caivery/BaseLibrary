package com.tany.myapplication.utils;


import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.tany.myapplication.view.view.MyImageView;

/**
 * Created by tany on 2016/7/27.
 */

//对图片加载库的一个简单封装，方便快速切换其他图片加载库 如Glide
public class ImageLoader {
    //image图片的基地址
    public static String baseUrlImage = "http://api.wusejia.com/image";

    /*Fresco使用说明
    务必设置不要使用wrap_contenter
    使用在对应的Activity 需要进行初始化

    <com.facebook.drawee.view.SimpleDraweeView
        android:id="@+id/main_adv"
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:layout_centerInParent="true"
        fresco:placeholderImage="@mipmap/icon_placeholder"
        fresco:placeholderImageScaleType="fitCenter" />


     */



    //对外接口。加载网络图片资源
    // 如需切换至Glide 。打开glide、注释本块其他代码；MyImageView父类切换至ImageView 即可
    public static void initView(Context context, MyImageView mSimpleDraweeView, String url) { //创建SimpleDraweeView对象

        //Glide.with(context).load(url).into(mSimpleDraweeView);

        //TODO:有部分不是 服务器的图片，需要判断是不是外站地址
        Log.e("imageloader", baseUrlImage + url);
        Uri imageUri = null;
        if (url.startsWith("http")) {
            imageUri = Uri.parse(url);

        } else {
            imageUri = Uri.parse(baseUrlImage + url);
        }

        //一些常规配置，后期会修改到Fresco初始化的时候 配置
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(imageUri)
                .setProgressiveRenderingEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(mSimpleDraweeView.getController())
                .setControllerListener(new BaseControllerListener<ImageInfo>())
                .build();
        mSimpleDraweeView.setController(controller);
    }


    //对外接口。加载网络图片资源
    //加载本地图片（暂时只做了file//，类是assert见官网吧）
    public static void initLocalView(Context context, MyImageView mSimpleDraweeView, String url) { //创建SimpleDraweeView对象

        //创建将要加载的图片的Uri
        Log.e("initLocalView", "file://" + url);
        Uri imageUri = Uri.parse("file://" + url);

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(imageUri)
                .setProgressiveRenderingEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(mSimpleDraweeView.getController())
                .setControllerListener(new BaseControllerListener<ImageInfo>())
                .build();
        mSimpleDraweeView.setController(controller);
    }

}

