package com.easemob.kefu_remote;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

/**
 * 用于其他进程来唤醒UI进程用的 Service
 *
 * Created by lzan13 on 2017/3/9.
 */

public class CoreService extends Service {

    private final static String TAG = CoreService.class.getSimpleName();
    // 核心进程 Service ID
    private final static int CORE_SERVICE_ID = -1001;

    @Override
    public void onCreate() {
        Log.i(TAG, "VMCoreService -> onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "VMCoreService -> onStartCommand");
        // 利用 Android 漏洞提高进程优先级，
        startForeground(CORE_SERVICE_ID, new Notification());
        // 当 SDk 版本大于18时，需要通过内部 Service 类启动同样 id 的 Service
        if (Build.VERSION.SDK_INT >= 18) {
            Intent innerIntent = new Intent(this, CoreInnerService.class);
            startService(innerIntent);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "VMCoreService -> onDestroy");
        super.onDestroy();
    }

    /**
     * 实现一个内部的 Service，实现让后台服务的优先级提高到前台服务，这里利用了 android 系统的漏洞，
     * 不保证所有系统可用，测试在7.1.1 之前大部分系统都是可以的，不排除个别厂商优化限制
     */
    public static class CoreInnerService extends Service {

        @Override
        public void onCreate() {
            Log.i(TAG, "CoreInnerService -> onCreate");
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.i(TAG, "CoreInnerService -> onStartCommand");
            startForeground(CORE_SERVICE_ID, new Notification());
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent intent) {
            // TODO: Return the communication channel to the service.
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public void onDestroy() {
            Log.i(TAG, "CoreInnerService -> onDestroy");
            super.onDestroy();
        }
    }
}
