package com.example.jammy.pdf_demo.websocket;

import android.app.Application;
import android.content.Context;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

/**
 * 初始化配置
 */

public class WsApplication extends Application {

    public static WsApplication context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        initLog();
        initAppStatusListener();
    }


    /**
     * 初始化日志配置
     */
    private void initLog() {
        Logger.addLogAdapter(new AndroidLogAdapter());
    }


    /**
     * 初始化应用前后台状态监听
     */
    private void initAppStatusListener() {
        ForegroundCallbacks.init(this).addListener(new ForegroundCallbacks.Listener() {
            @Override
            public void onBecameForeground() {
                Logger.t("WsManager").d("应用回到前台调用重连方法");
                WsManager.getInstance().reconnect();
            }

            @Override
            public void onBecameBackground() {

            }
        });
    }

    public static Context getContext(){
        return context;
    }
}
