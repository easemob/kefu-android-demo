package com.easemob.kefu_remote.conference;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.easemob.kefu_remote.R;

public class SRService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        Notification notification = new Notification(R.drawable.ic_cursor_default,
                "wf update service is running",
                System.currentTimeMillis());
        PendingIntent pintent= PendingIntent.getService(this, 0, intent, 0);
        //notification.setLatestEventInfo(this, "WF Update Service",
        //        "wf update service is running！", pintent);

        //让该service前台运行，避免手机休眠时系统自动杀掉该服务
        //如果 id 为 0 ，那么状态栏的 notification 将不会显示。
        startForeground(10, notification);
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
