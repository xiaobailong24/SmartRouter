package com.xiaobailong24.smartrouter;

import android.app.Application;

import me.xiaobailong24.library.Crash.CrashHandler;

/**
 * Created by LiuYinlong on 2016/9/13.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler handler = CrashHandler.getInstance();
        handler.init(getApplicationContext());
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }
}