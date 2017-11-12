package com.security.weather.util;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import org.litepal.LitePal;

/**
 * 全局的Application
 */
public class MyApplication extends Application {

    private static Context context;
    private static long mainTreadId;
    private static Handler handler;

    /**
     * 程序的入口,初始化一些常用的参数;
     */
    @Override
    public void onCreate() {
        super.onCreate();

        //全局的上下文
        context = getApplicationContext();
        // 主线程Id
        mainTreadId = android.os.Process.myTid();
        // 定义一个handler
        handler = new Handler();

        //初始化Litepal
        LitePal.initialize(this);
    }

    public static Handler getHandler() {
        return handler;
    }

    public static Context getContext() {
        return context;
    }

    public static long getMainTreadId() {
        return mainTreadId;
    }
}
