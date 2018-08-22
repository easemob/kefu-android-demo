package com.easemob.kefu_remote.conference;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.easemob.kefu_remote.RemoteApp;
import com.easemob.kefu_remote.control.CtrlManager;
import com.easemob.kefu_remote.sdk.EMCallbacks;
import com.easemob.kefu_remote.sdk.EMConferenceListener;
import com.easemob.kefu_remote.sdk.EMConferenceManager;
import com.easemob.kefu_remote.sdk.EMConferenceMember;
import com.easemob.kefu_remote.sdk.EMConferenceStream;
import com.easemob.kefu_remote.sdk.EMStreamParam;
import com.easemob.kefu_remote.sdk.EMStreamStatistics;
import com.easemob.kefu_remote.utils.VMSPUtil;
import com.superrtc.sdk.VideoView;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class RemoteManager {
    private final String TAG = this.getClass().getSimpleName();

    private EMConferenceListener conferenceListener;

    private AudioManager audioManager;
    private CallPhoneReceiver phoneReceiver;

    private boolean isAutoSubscribe = true;

    private EMStreamParam normalParam;
    private EMStreamParam desktopParam;
    private EMStreamParam audioMixParam;

    private String ticket = "";
    private String username = "";
    private String publishAudioMixId = "";
    private String publishNormalId = "";
    private String publishDesktopId = "";

    private static RemoteManager instance;
    private Context context;
    private Fragment fragment;

    public static RemoteManager getInstance() {
        if (instance == null) {
            instance = new RemoteManager();
        }

        return instance;
    }

    /**
     * 初始化
     */
    public void init(Context context, Fragment fragment, String ticket) {

        this.context = context;
        this.fragment = fragment;
        bindSRServer();

        try {
            JSONObject object = new JSONObject(ticket);
            username = (String) object.get("memName");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        username = (String) VMSPUtil.get("username", "Godl1");
        this.ticket = ticket.replace("&quot;", "\"");
        Log.i("info", "ticket0:" + ticket);
        Log.i("info", "ticket1:" + testTicket());
        EMConferenceManager.getInstance().setVideoMaxKbps(600);
        EMConferenceManager.getInstance().setVideoMinKbps(150);
        EMConferenceManager.getInstance().setAudioMaxKbps(150);

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        //String recordPath = context.getExternalFilesDir("").getAbsolutePath() + "/";
        //EMConferenceManager.getInstance().setDocDirectory(recordPath);

        // 不同情况下的 config
        audioMixParam = new EMStreamParam();
        audioMixParam.setAudioOff(false);
        audioMixParam.setVideoOff(true);
        audioMixParam.setStreamType(EMConferenceStream.StreamType.AUDIOMIX);

        normalParam = new EMStreamParam();
        normalParam.setAudioOff(false);
        normalParam.setVideoOff(true);
        normalParam.setStreamType(EMConferenceStream.StreamType.NORMAL);

        desktopParam = new EMStreamParam();
        desktopParam.setAudioOff(true);
        desktopParam.setVideoOff(false);
        desktopParam.setStreamType(EMConferenceStream.StreamType.DESKTOP);

        // 默认不打开扬声器
        //openSpeaker();

        startConference();
    }

    public String testTicket() {
        String hostUrl = (String) VMSPUtil.get("host_url", "wss://turn2.easemob.com/ws");
        String conferenceId = (String) VMSPUtil.get("conference_id", "L110");

        VMSPUtil.put("host_url", hostUrl);
        VMSPUtil.put("conference_id", conferenceId);
        VMSPUtil.put("username", username);

        JSONObject testTicketDict = new JSONObject();
        try {
            testTicketDict.put("url", hostUrl);
            testTicketDict.put("tktId", "tkt01");
            testTicketDict.put("type", "confr");
            testTicketDict.put("confrId", conferenceId);
            testTicketDict.put("memName", username);
            testTicketDict.put("hmac", "3Qk2Mfjf4hs+zWlMLtpwTXjijo8=");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return testTicketDict.toString();
    }

    /**
     * 作为创建者创建并加入会议
     */
    private void startConference() {
        EMStreamParam param = new EMStreamParam();
        param.setVideoOff(true);
        EMConferenceManager.getInstance().joinConference(ticket, username, param, new EMCallbacks() {
            @Override public void onDone(Object object) {
                //registerPhoneStateListener();
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override public void run() {
                        Toast.makeText(context, "Join conference success", Toast.LENGTH_SHORT).show();
                        Log.i("info", "joinConference success");
                    }
                });
                shareDesktop();
            }

            @Override public void onError(final int code, final String errorDesc) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override public void run() {
                        Toast.makeText(context, "Join conference failed " + code + ", " + errorDesc, Toast.LENGTH_SHORT).show();
                        Log.i("info", "joinConference failed");
                    }
                });
                if (code == -122) {
                    shareDesktop();
                }
            }
        });
    }

    public void requestCtrDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("申请控制");
        builder.setMessage("对方申请对你进行远程控制，是否同意？");
        builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                CtrlManager.getInstance().agreeRequestCtrl();
                listener.executeMode(1);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                CtrlManager.getInstance().rejectRequestCtrl();
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private RemoteListener listener;

    public void setRemoteListener(RemoteListener listener) {
        this.listener = listener;
    }

    public interface RemoteListener {
        void executeMode(int mode);
    }

    /**
     * 退出会议
     */
    public void exitConference() {
        stopMonitorSpeaker();
        EMConferenceManager.getInstance().exitConference(new EMCallbacks() {
            @Override public void onDone(Object object) {

                shareDesktop();
            }

            @Override public void onError(int code, String errorDesc) {

            }
        });
    }

    /**
     * 开始推自己的数据
     */
    private void publishAudioMix() {
        EMConferenceManager.getInstance().publish(audioMixParam, new EMCallbacks() {
            @Override public void onDone(Object object) {
                publishAudioMixId = (String) object;
            }

            @Override public void onError(final int code, final String msg) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override public void run() {
                        Toast.makeText(context, "publish error " + code + " " + msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void publishVideo() {
        EMConferenceManager.getInstance().publish(normalParam, new EMCallbacks() {
            @Override public void onDone(Object object) {
                publishNormalId = (String) object;
            }

            @Override public void onError(final int code, final String msg) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override public void run() {
                        Toast.makeText(context, "publish error " + code + " " + msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void publishDesktop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            desktopParam.setShareView(null);
        } else {
            desktopParam.setShareView(((Activity) context).getWindow().getDecorView());
        }
        EMConferenceManager.getInstance().publish(desktopParam, new EMCallbacks() {
            @Override public void onDone(Object value) {
                publishDesktopId = (String) value;
                prepareScreenCapture();
                listener.executeMode(0);
                Log.i("info", "publish onDone----" + value);
            }

            @Override public void onError(int error, String errorMsg) {
                Log.i("info", "publish onError");
            }
        });
    }

    /**
     * 停止推自己的数据
     */
    private void unpublish(String id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!TextUtils.isEmpty(publishDesktopId) && publishDesktopId.equals(id)) {
                stopScreenCapture();
            }
        }
        EMConferenceManager.getInstance().unpublish(id, new EMCallbacks() {
            @Override public void onDone(Object object) {
                listener.executeMode(2);
            }

            @Override public void onError(int code, String errorDesc) {

            }
        });
    }

    /**
     * 订阅指定成员 stream
     */
    private void subscribe(EMConferenceStream stream, final VideoView memberView) {
        if (!isAutoSubscribe) {
            return;
        }
        EMConferenceManager.getInstance().subscribe(stream, memberView, new EMCallbacks() {
            @Override public void onDone(Object object) {
                Log.i("info", "subscribe success");
            }

            @Override public void onError(int code, String errorDesc) {
                Log.i("info", "subscribe error:" + errorDesc);
            }
        });
    }

    /**
     * 更新订阅指定成员 stream
     */
    private void updateSubscribe(EMConferenceStream stream, final VideoView memberView) {
        if (!isAutoSubscribe) {
            return;
        }
        EMConferenceManager.getInstance().updateSubscribe(stream, memberView, new EMCallbacks() {
            @Override public void onDone(Object object) {
                Log.i("info", "updateSubscribe success");
            }

            @Override public void onError(int code, String errorDesc) {
                Log.i("info", "updateSubscribe error:" + errorDesc);
            }
        });
    }

    /**
     * 取消订阅指定成员 stream
     */
    private void unsubscribe(EMConferenceStream stream) {
        EMConferenceManager.getInstance().unsubscribe(stream, new EMCallbacks() {
            @Override public void onDone(Object object) {
            }

            @Override public void onError(int code, String errorDesc) {

            }
        });
    }

    private void startMonitorSpeaker() {
        EMConferenceManager.getInstance().startMonitorSpeaker(200);
    }

    private void stopMonitorSpeaker() {
        EMConferenceManager.getInstance().stopMonitorSpeaker();
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

    /**
     * 语音开关
     */
    public void voiceSwitch(TextView view) {
        if (normalParam.isAudioOff()) {
            normalParam.setAudioOff(false);
            EMConferenceManager.getInstance().openVoiceTransfer();
        } else {
            normalParam.setAudioOff(true);
            EMConferenceManager.getInstance().closeVoiceTransfer();
        }
        view.setActivated(!normalParam.isAudioOff());
    }

    /**
     * 切换摄像头
     */
    private void changeCamera() {
        EMConferenceManager.getInstance().switchCamera();
    }

    /**
     * 分享桌面
     */
    private void shareDesktop() {
        if (TextUtils.isEmpty(publishDesktopId)) {
            publishDesktop();
        } else {
            unpublish(publishDesktopId);
            publishDesktopId = "";
        }
    }

    public void addConferenceListener() {
        conferenceListener = new EMConferenceListener() {
            /**
             * --------------------------------------------------------------------
             * 多人音视频会议回调方法
             */
            @Override public void onMemberJoined(final EMConferenceMember member) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override public void run() {
                        Toast.makeText(context, member.getMemberName() + " joined conference!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override public void onMemberExited(final EMConferenceMember member) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override public void run() {
                        Toast.makeText(context, member.getMemberName() + " removed conference!", Toast.LENGTH_SHORT).show();
                        listener.executeMode(2);
                    }
                });
            }

            @Override public void onStreamAdded(final EMConferenceStream stream) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override public void run() {
                        Toast.makeText(context, stream.getUsername() + " stream add!", Toast.LENGTH_SHORT).show();
                        subscribe(stream, null);
                    }
                });
            }

            @Override public void onStreamRemoved(final EMConferenceStream stream) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override public void run() {
                        Toast.makeText(context, stream.getUsername() + " stream removed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override public void onStreamUpdate(final EMConferenceStream stream) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override public void run() {
                        Toast.makeText(context, stream.getUsername() + " stream update!", Toast.LENGTH_SHORT).show();
                        updateSubscribe(stream, null);
                    }
                });
            }

            @Override public void onPassiveLeave(final int error, final String message) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override public void run() {
                        Toast.makeText(context, "Passive exit " + error + ", message" + message, Toast.LENGTH_SHORT).show();
                        listener.executeMode(2);
                    }
                });
            }

            @Override public void onConferenceState(final EMConferenceListener.ConferenceState state, Object object) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override public void run() {
                        Toast.makeText(context, "State=" + state, Toast.LENGTH_SHORT).show();
                    }
                });
                if (state == EMConferenceListener.ConferenceState.STATE_TAKE_CAMERA_PICTURE) {
                    //tackCameraPictureNotice((String) object);
                }
                if (state == EMConferenceListener.ConferenceState.STATE_CUSTOM_MSG) {
                }
            }

            @Override public void onStreamStatistics(EMStreamStatistics statistics) {
                Log.i(TAG, statistics.toString());
            }

            @Override public void onCtrlMessage(EMConferenceListener.ConferenceState state, final String arg1, final String arg2, final Object arg3) {
            }

            @Override public void onSpeakers(final List<String> speakers) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override public void run() {
                        //currTalking(speakers);
                    }
                });
            }

            @Override public void onStreamSetup(final String streamId) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override public void run() {
                        Toast.makeText(context, "streamId = " + streamId, Toast.LENGTH_SHORT).show();
                        Log.i("info", "stream id---:" + streamId);
                    }
                });
            }
        };
        EMConferenceManager.getInstance().addConferenceListener(conferenceListener);
    }

    public void removeConferenceListener() {
        EMConferenceManager.getInstance().removeConferenceListener(conferenceListener);
    }

    public void unbindSR(Context context) {
        //unregisterPhoneStateListener();
        unbindSRServer(context);
    }

    /**
     * --------------------------------------------------------------------
     * 屏幕分享捕获屏幕部分
     */
    private SRService srService;

    /**
     * 绑定捕获屏幕服务
     */
    public void bindSRServer() {
        if (context != null) {
            Intent intent = new Intent(context, SRService.class);
            context.bindService(intent, serviceConnection, context.BIND_AUTO_CREATE);
            RemoteApp.getInstance().setServiceStatus(false);
        }
    }

    /**
     * 解除服务绑定
     */
    private void unbindSRServer(Context context) {
        if (!RemoteApp.getInstance().getServiceStatus()) {
            context.unbindService(serviceConnection);
            RemoteApp.getInstance().setServiceStatus(true);
        }
    }

    /**
     * 准备屏幕捕获
     */
    private void prepareScreenCapture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!SRManager.getInstance().isRunning()) {
                SRManager.getInstance().init((Activity) context, fragment);
                SRManager.getInstance().setScreenShortCallback(new SRManager.ScreenShortCallback() {
                    @Override public void onBitmap(Bitmap bitmap) {
                        EMConferenceManager.getInstance().inputExternalVideoData(bitmap);
                    }
                });
            }
        }
    }

    /**
     * 开始捕获屏幕
     */
    public void startScreenCapture() {
        srService.startScreenShort();
    }

    /**
     * 停止
     */
    private void stopScreenCapture() {
        srService.stop();
    }

    /**
     * 自定义实现服务连接
     */
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
     * 注册手机通话监听
     */
    public void registerPhoneStateListener() {
        if (phoneReceiver == null) {
            phoneReceiver = new CallPhoneReceiver();
            IntentFilter phoneFilter = new IntentFilter();
            phoneFilter.addAction("android.intent.action.PHONE_STATE");
            context.registerReceiver(phoneReceiver, phoneFilter);
        }
    }

    /**
     * 取消注册手机通话监听
     */
    public void unregisterPhoneStateListener() {
        if (phoneReceiver != null) {
            context.unregisterReceiver(phoneReceiver);
            phoneReceiver = null;
        }
    }
}
