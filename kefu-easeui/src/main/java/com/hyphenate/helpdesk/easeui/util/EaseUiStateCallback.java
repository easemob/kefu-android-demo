package com.hyphenate.helpdesk.easeui.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EaseUiStateCallback implements Application.ActivityLifecycleCallbacks {
    private volatile static EaseUiStateCallback APP_STATE_CALLBACK;
    private final Map<String, Integer> mActivityMap = new HashMap<>();
    private final List<IEaseUiStateCallback> mIEaseUiStateCallbacks = new ArrayList<>();
    private volatile boolean mIsBackground;
    private EaseUiStateCallback(){}


    public static void init(Application context){
        if (APP_STATE_CALLBACK == null){
            synchronized (EaseUiStateCallback.class){
                if (APP_STATE_CALLBACK == null){
                    APP_STATE_CALLBACK = new EaseUiStateCallback(context);
                }
            }
        }
    }

    public static EaseUiStateCallback getEaseUiStateCallback() {
        return APP_STATE_CALLBACK;
    }

    @SuppressLint("ObsoleteSdkInt")
    private EaseUiStateCallback(Application context){
        // isSdk14
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            context.registerActivityLifecycleCallbacks(this);
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        mActivityMap.put(activity.getClass().getName(), 1);
        if(mActivityMap.size() == 1) {
            mIsBackground = false;
            if (mIEaseUiStateCallbacks != null){
                for (IEaseUiStateCallback iEaseUiStateCallback : mIEaseUiStateCallbacks){
                    iEaseUiStateCallback.onAppForeground();
                }
            }
        }

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        mActivityMap.remove(activity.getClass().getName());
        if (mActivityMap.isEmpty()) {
            mIsBackground = true;
            if (mIEaseUiStateCallbacks != null){
                for (IEaseUiStateCallback iEaseUiStateCallback : mIEaseUiStateCallbacks){
                    iEaseUiStateCallback.onAppBackground();
                }
            }

        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public void registerIAppStateEaseUiCallback(IEaseUiStateCallback callback){
        synchronized (EaseUiStateCallback.class){
            mIEaseUiStateCallbacks.add(callback);
        }
    }

    public void unRegisterIAppStateEaseUiCallback(IEaseUiStateCallback callback){
        synchronized (EaseUiStateCallback.class){
            mIEaseUiStateCallbacks.remove(callback);
        }
    }

    // 是否后台运行
    public boolean isBackground() {
        return mIsBackground;
    }

    public interface IEaseUiStateCallback {
        void onAppForeground();
        void onAppBackground();
    }

}
