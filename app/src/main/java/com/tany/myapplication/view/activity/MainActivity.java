package com.tany.myapplication.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.tany.myapplication.R;


//简单演示 greenDao 替代sql语句 crud

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    //跳转greenDemo
    public void greenDao(View v) {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }


    //跳转DiskLruCache
    public void DiskLruCache(View v) {
        Intent intent = new Intent(MainActivity.this, DiskLruCahceActivity.class);
        startActivity(intent);
    }


}
