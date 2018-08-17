package com.easemob.kefu_remote.conference;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class SRService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return new SRBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * 开始截图
     */
    public boolean startScreenShort() {
        return SRManager.getInstance().startScreenShort();
    }

    /**
     * 开始录屏
     */
    public boolean startScreenRecord() {
        return SRManager.getInstance().startScreenRecord();
    }

    /**
     * 停止
     */
    public boolean stop() {
        return SRManager.getInstance().stop();
    }

    /**
     * 是否在运行中
     */
    public boolean isRunning() {
        return SRManager.getInstance().isRunning();
    }


    public class SRBinder extends Binder {
        public SRService getSRService() {
            return SRService.this;
        }
    }
}
