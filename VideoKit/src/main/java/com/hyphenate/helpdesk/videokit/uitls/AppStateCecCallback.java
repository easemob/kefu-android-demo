package com.hyphenate.helpdesk.videokit.uitls;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AppStateCecCallback implements Application.ActivityLifecycleCallbacks {
    private volatile static AppStateCecCallback APP_STATE_CALLBACK;
    private Map<String, Integer> mActivityMap = new HashMap<>();
    private List<IAppStateCecCallback> mIAppStateCecCallbacks = new ArrayList<>();
    private volatile boolean mIsBackground;
    public static void init(Application context){
        if (APP_STATE_CALLBACK == null){
            synchronized (AppStateCecCallback.class){
                if (APP_STATE_CALLBACK == null){
                    APP_STATE_CALLBACK = new AppStateCecCallback(context);
                }
            }
        }
    }

    public static AppStateCecCallback getAppStateCallback() {
        return APP_STATE_CALLBACK;
    }

    @SuppressLint("ObsoleteSdkInt")
    private AppStateCecCallback(Application context){
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
            if (mIAppStateCecCallbacks != null){
                for (IAppStateCecCallback iAppStateCecCallback : mIAppStateCecCallbacks){
                    iAppStateCecCallback.onAppForeground();
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

        if (mIAppStateCecCallbacks != null){
            for (IAppStateCecCallback iAppStateVecCallback : mIAppStateCecCallbacks){
                iAppStateVecCallback.onActivityStopped(activity);
            }
        }

        mActivityMap.remove(activity.getClass().getName());
        if (mActivityMap.isEmpty()) {
            mIsBackground = true;
            if (mIAppStateCecCallbacks != null){
                for (IAppStateCecCallback iAppStateCecCallback : mIAppStateCecCallbacks){
                    iAppStateCecCallback.onAppBackground();
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

    public void registerIAppStateCecCallback(IAppStateCecCallback callback){
        synchronized (AppStateCecCallback.class){
            mIAppStateCecCallbacks.add(callback);
        }
    }

    public void unRegisterIAppStateCecCallback(IAppStateCecCallback callback){
        synchronized (AppStateCecCallback.class){
            mIAppStateCecCallbacks.remove(callback);
        }
    }

    // 是否后台运行
    public boolean isBackground() {
        return mIsBackground;
    }

    public interface IAppStateCecCallback {
        void onAppForeground();
        void onAppBackground();
        void onActivityStopped(Activity activity);
    }

}
