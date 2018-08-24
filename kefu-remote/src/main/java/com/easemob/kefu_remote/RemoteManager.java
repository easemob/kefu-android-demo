package com.easemob.kefu_remote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.DisplayMetrics;
import com.easemob.kefu_remote.control.CtrlManager;
import com.easemob.kefu_remote.utils.VMLog;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.MediaStream;
import com.hyphenate.helpdesk.callback.Callback;
import com.superrtc.mediamanager.EMediaManager;

public class RemoteManager {

    private static RemoteManager instance;
    private AudioManager audioManager;
    private Context context;

    public static RemoteManager getInstance() {
        if (instance == null) {
            instance = new RemoteManager();
        }

        return instance;
    }

    public void initRemoteOption(Context context) {
        this.context = context;
        EMediaManager.getInstance().setAudioMaxKbps(150);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(false);
        openMic();
        bindSRServer();
        ChatClient.getInstance().callManager().publishWindow((Activity) context, new Callback() {
            @Override public void onSuccess() {
                listener.executeMode(0);
            }

            @Override public void onError(int code, String error) {

            }

            @Override public void onProgress(int progress, String status) {

            }
        });
        CtrlManager.getInstance(context).setStopCtrModeListener(new CtrlManager.StopCtrModeListener() {
            @Override public void stopCtr() {
                listener.stopCtr();
            }
        });
    }

    //开始远程桌面共享
    public void startShareDeskTop(int requestCode, int resultCode, Intent data) {
        srService.startc(requestCode, resultCode, data);
    }

    /**
     * 自定义实现服务连接
     */
    private SRService srService;
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override public void onServiceConnected(ComponentName name, IBinder service) {
            DisplayMetrics metrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
            SRService.SRBinder binder = (SRService.SRBinder) service;
            srService = binder.getSRService();
        }

        @Override public void onServiceDisconnected(ComponentName name) {
        }
    };

    /**
     * 绑定捕获屏幕服务
     */
    public void bindSRServer() {
        if (context != null) {
            Intent intent = new Intent(context, SRService.class);
            context.bindService(intent, serviceConnection, context.BIND_AUTO_CREATE);
        }
    }

    /**
     * 解除服务绑定
     */
    public void unbindSRServer(Context context) {
        context.unbindService(serviceConnection);
    }

    //退出远程所有操作
    public void exitRemote() {
        stopShareDeskTop();
        ChatClient.getInstance().callManager().endCall();
    }

    //停止远程桌面共享
    public void stopShareDeskTop() {
        ChatClient.getInstance().callManager().unPublishWindow(new Callback() {
            @Override public void onSuccess() {
                listener.executeMode(2);
            }

            @Override public void onError(int code, String error) {

            }

            @Override public void onProgress(int progress, String status) {

            }
        });
    }

    //停止坐席端对手机端ui远程控制
    public void stopControl() {
        CtrlManager.getInstance(context).stopCtrlMode();
    }

    //坐席端主动关闭远程操作
    public void stoppedRemote() {
        listener.executeMode(2);
    }

    public void requestCtrDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("申请控制");
        builder.setMessage("对方申请对你进行远程控制，是否同意？");
        builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                CtrlManager.getInstance(context).agreeRequestCtrl();
                listener.executeMode(1);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                CtrlManager.getInstance(context).rejectRequestCtrl();
                dialog.dismiss();
            }
        });
        builder.show();
    }

    //解析接收的控制指令
    public void parseCtrlMsg(String arg1, String arg2, Object arg3) {
        CtrlManager.getInstance(context).parseCtrlMsg(arg1, arg2, arg3);
    }

    /**
     * 打开扬声器
     * 主要是通过扬声器的开关以及设置音频播放模式来实现
     * 1、MODE_NORMAL：是正常模式，一般用于外放音频
     * 2、MODE_IN_CALL：
     * 3、MODE_IN_COMMUNICATION：这个和 CALL 都表示通讯模式，不过 CALL 在华为上不好使，故使用 COMMUNICATION
     * 4、MODE_RINGTONE：铃声模式
     */
    public void openSpeaker() {
        // 检查是否已经开启扬声器
        if (!audioManager.isSpeakerphoneOn()) {
            // 打开扬声器
            audioManager.setSpeakerphoneOn(true);
        }
        // 开启了扬声器之后，因为是进行通话，声音的模式也要设置成通讯模式
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    }

    /**
     * 关闭扬声器，即开启听筒播放模式
     * 更多内容看{@link #openSpeaker()}
     */
    public void closeSpeaker() {
        // 检查是否已经开启扬声器
        if (audioManager.isSpeakerphoneOn()) {
            // 关闭扬声器
            audioManager.setSpeakerphoneOn(false);
        }
        // 设置声音模式为通讯模式
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    }

    private RemoteListener listener;

    public void setRemoteListener(RemoteListener listener) {
        this.listener = listener;
    }

    public interface RemoteListener {
        void executeMode(int mode);

        void stopCtr();
    }

    public void openMic() {
        ChatClient.getInstance().callManager().resumeVoice();
    }

    public void closeMic() {
        ChatClient.getInstance().callManager().pauseVoice();
    }

    public void subscribe(MediaStream stream) {
        ChatClient.getInstance().callManager().subscribe(stream, null, new Callback() {
            @Override public void onSuccess() {
                VMLog.d("subscribe onSuccess");
            }

            @Override public void onError(int code, String error) {
                VMLog.d("subscribe onError");
            }

            @Override public void onProgress(int progress, String status) {

            }
        });
    }

    public void stopSubscribe(MediaStream stream) {
        ChatClient.getInstance().callManager().unSubscribe(stream, new Callback() {
            @Override public void onSuccess() {

            }

            @Override public void onError(int code, String error) {

            }

            @Override public void onProgress(int progress, String status) {

            }
        });
    }

    public void updateSubscribe(String streamId) {
        ChatClient.getInstance().callManager().updateSubscribe(streamId, null, new Callback() {
            @Override public void onSuccess() {

            }

            @Override public void onError(int code, String error) {

            }

            @Override public void onProgress(int progress, String status) {

            }
        });
    }
}
