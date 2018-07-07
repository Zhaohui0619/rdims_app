package com.ctkj.xj_app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.litepal.LitePal;

/**
 * 一般而言 RequestQueue 是整个APP内使用的全局性对象，所以最好写入 Application 类中
 * 修改清单文件，添加 <application>android:name=".MyApplication"</application>
 */

public class MyApplication extends MultiDexApplication {

    private static RequestQueue requestQueue;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        requestQueue = Volley.newRequestQueue(this);
        context = getApplicationContext();
        //将全局context传递给LitePal
        LitePal.initialize(context);
    }

    public static RequestQueue getHttpQueue(){
        return requestQueue;

    }

    public static Context getContext(){
        return context;
    }
}
