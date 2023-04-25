package com.easemob.veckit.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AppStateVecCallback implements Application.ActivityLifecycleCallbacks {
    private volatile static AppStateVecCallback APP_STATE_CALLBACK;
    private Map<String, Integer> mActivityMap = new HashMap<>();
    private List<IAppStateVecCallback> mIAppStateVecCallbacks = new ArrayList<>();
    private volatile boolean mIsBackground;
    private AppStateVecCallback(){}

    public static void init(Application context){
        if (APP_STATE_CALLBACK == null){
            synchronized (AppStateVecCallback.class){
                if (APP_STATE_CALLBACK == null){
                    APP_STATE_CALLBACK = new AppStateVecCallback(context);
                }
            }
        }
    }

    public static AppStateVecCallback getAppStateCallback() {
        return APP_STATE_CALLBACK;
    }

    @SuppressLint("ObsoleteSdkInt")
    private AppStateVecCallback(Application context){
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
            if (mIAppStateVecCallbacks != null){
                for (IAppStateVecCallback iAppStateVecCallback : mIAppStateVecCallbacks){
                    iAppStateVecCallback.onAppForeground();
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

        if (mIAppStateVecCallbacks != null){
            for (IAppStateVecCallback iAppStateVecCallback : mIAppStateVecCallbacks){
                iAppStateVecCallback.onActivityStopped(activity);
            }
        }

        mActivityMap.remove(activity.getClass().getName());
        if (mActivityMap.isEmpty()) {
            mIsBackground = true;
            if (mIAppStateVecCallbacks != null){
                for (IAppStateVecCallback iAppStateVecCallback : mIAppStateVecCallbacks){
                    iAppStateVecCallback.onAppBackground();
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

    public void registerIAppStateVecCallback(IAppStateVecCallback callback){
        synchronized (AppStateVecCallback.class){
            mIAppStateVecCallbacks.add(callback);
        }
    }

    public void unRegisterIAppStateVecCallback(IAppStateVecCallback callback){
        synchronized (AppStateVecCallback.class){
            mIAppStateVecCallbacks.remove(callback);
        }
    }

    // 是否后台运行
    public boolean isBackground() {
        return mIsBackground;
    }

    public interface IAppStateVecCallback {
        void onAppForeground();
        void onAppBackground();
        void onActivityStopped(Activity activity);
    }

}
