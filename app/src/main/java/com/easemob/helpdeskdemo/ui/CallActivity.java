package com.easemob.helpdeskdemo.ui;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.Toast;

import com.easemob.helpdeskdemo.R;
import com.hyphenate.EMError;
import com.hyphenate.chat.CallStateChangeListener;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.exceptions.EMNoActiveCallException;
import com.hyphenate.exceptions.EMServiceNotReadyException;
import com.hyphenate.media.LocalSurfaceView;
import com.hyphenate.media.OppositeSurfaceView;
import com.hyphenate.util.EMLog;

/**
 * Created by liyuzhao on 11/01/2017.
 */

public class CallActivity extends DemoBaseActivity {

    protected static final String TAG = "callActivity";
    protected final int MSG_CALL_MAKE_VIDEO = 0;
    protected final int MSG_CALL_MAKE_VOICE = 1;
    protected final int MSG_CALL_ANSWER = 2;
    protected final int MSG_CALL_REJECT = 3;
    protected final int MSG_CALL_END = 4;
    protected final int MSG_CALL_RELEASE_HANDLER = 5;
    protected final int MSG_CALL_SWITCH_CAMERA = 6;

    protected boolean isInComingCall;
    protected boolean isRefused = false;
    protected String username;
    protected CallingState callingState = CallingState.CANCELLED;
    protected String callDruationText;
    protected String msgid;
    protected AudioManager audioManager;
    protected SoundPool soundPool;
    protected Ringtone ringtone;
    protected int outgoing;
    protected CallStateChangeListener callStateListener;
//    protected EMCallStateChangeListener imCallStateListener;
    protected LocalSurfaceView localSurface;
    protected OppositeSurfaceView oppositeSurface;
    protected boolean isAnswered = false;
    protected int streamID = -1;


    /**
     * 0：voice call，1：video call
     */
    protected int callType = 0;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    protected void onDestroy() {
        if (soundPool != null){
            soundPool.release();
        }
        if (ringtone != null && ringtone.isPlaying()){
            ringtone.stop();
        }
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setMicrophoneMute(false);

        if (callStateListener != null){
            ChatClient.getInstance().callManager().removeCallStateChangeListener(callStateListener);
        }
//        if (imCallStateListener != null){
//            EMClient.getInstance().callManager().removeCallStateChangeListener(imCallStateListener);
//        }
        releaseHandler();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        handler.sendEmptyMessage(MSG_CALL_END);
        finish();
        super.onBackPressed();
    }


    Runnable timeoutHangup = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(MSG_CALL_END);
        }
    };

    HandlerThread callHandlerThread = new HandlerThread("callHandlerThread");
    { callHandlerThread.start(); }

    protected Handler handler = new Handler(callHandlerThread.getLooper()){
        @Override
        public void handleMessage(android.os.Message msg) {
            EMLog.d(TAG, "handleMessage -- enter block -- msg.what: " + msg.what);
            switch (msg.what){
                case MSG_CALL_MAKE_VIDEO:
                case MSG_CALL_MAKE_VOICE:
                    try{
                        if (msg.what == MSG_CALL_MAKE_VIDEO){
                            ChatClient.getInstance().callManager().makeVideoCall(username);
                        }else{
                            ChatClient.getInstance().callManager().makeVoiceCall(username);
                        }
                    }catch (final EMServiceNotReadyException e){
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String st2 = e.getMessage();
                                if (e.getErrorCode() == EMError.CALL_REMOTE_OFFLINE) {
                                    st2 = getResources().getString(R.string.The_other_is_not_online);
                                } else if (e.getErrorCode() == EMError.USER_NOT_LOGIN) {
                                    st2 = getResources().getString(R.string.Is_not_yet_connected_to_the_server);
                                } else if (e.getErrorCode() == EMError.INVALID_USER_NAME) {
                                    st2 = getResources().getString(R.string.illegal_user_name);
                                } else if (e.getErrorCode() == EMError.CALL_BUSY) {
                                    st2 = getResources().getString(R.string.The_other_is_on_the_phone);
                                } else if (e.getErrorCode() == EMError.NETWORK_ERROR) {
                                    st2 = getResources().getString(R.string.can_not_connect_chat_server_connection);
                                }
                                Toast.makeText(CallActivity.this, st2, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                    break;
                case MSG_CALL_ANSWER:
                    EMLog.d(TAG, "MSG_CALL_ANSWER");
                    if (ringtone != null){
                        ringtone.stop();
                    }
                    if (isInComingCall){
                        try{
                            ChatClient.getInstance().callManager().answerCall();
                            isAnswered = true;
                            // meizu MX5 4G, hasDataConnection(context) return status is incorrect
                            // MX5 con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected() return false in 4G
                            // so we will not judge it, App can decide whether judge the network status

//                        if (NetUtils.hasDataConnection(CallActivity.this)) {
//                            ChatClient.getInstance().callManager().answerCall();
//                            isAnswered = true;
//                        } else {
//                            runOnUiThread(new Runnable() {
//                                public void run() {
//                                    final String st2 = getResources().getString(R.string.Is_not_yet_connected_to_the_server);
//                                    Toast.makeText(CallActivity.this, st2, Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                            throw new Exception();
//                        }
                        }catch (Exception e){
                            e.printStackTrace();
                            finish();
                            return;
                        }
                    }else{
                        EMLog.d(TAG, "answer call isInComingCall:false");
                    }
                    break;
                case MSG_CALL_REJECT:
                    if (ringtone != null){
                        ringtone.stop();
                    }
                    try {
                        ChatClient.getInstance().callManager().rejectCall();
                    } catch (EMNoActiveCallException e) {
                        e.printStackTrace();
                        finish();
                    }
                    callingState = CallingState.REFUSED;
                    break;
                case MSG_CALL_END:
                    if (soundPool != null){
                        soundPool.stop(streamID);
                    }
                    try {
                        ChatClient.getInstance().callManager().endCall();
                    } catch (EMNoActiveCallException e) {
                        finish();
                    }
                    break;
                case MSG_CALL_RELEASE_HANDLER:
                    try {
                        ChatClient.getInstance().callManager().endCall();
                    } catch (EMNoActiveCallException e) {
                    }
                    handler.removeCallbacks(timeoutHangup);
                    handler.removeMessages(MSG_CALL_MAKE_VIDEO);
                    handler.removeMessages(MSG_CALL_ANSWER);
                    handler.removeMessages(MSG_CALL_REJECT);
                    handler.removeMessages(MSG_CALL_END);
                    callHandlerThread.quit();
                    break;
                case MSG_CALL_SWITCH_CAMERA:
                    ChatClient.getInstance().callManager().switchCamera();
                    break;
                default:
                    break;
            }
            EMLog.d("CallActivity", "handleMessage ---exit block--- msg.what:" + msg.what);
        }
    };


    void releaseHandler(){
        handler.sendEmptyMessage(MSG_CALL_RELEASE_HANDLER);
    }


    /**
     * play the incoming call ringtone
     */
    protected int playMakeCallSounds() {
        try {
            audioManager.setMode(AudioManager.MODE_RINGTONE);
            audioManager.setSpeakerphoneOn(false);

            // play
            int id = soundPool.play(outgoing, // sound resource
                    0.3f, // left volume
                    0.3f, // right volume
                    1, // priority
                    -1, // loop, 0 is no loop, -1 is loop forever
                    1); // playback rate (1.0 = normal playback, range 0.5 to 2.0)
            return id;
        } catch (Exception e) {
            return -1;
        }
    }

    protected void openSpeakerOn(){
        try {
            if (!audioManager.isSpeakerphoneOn()){
                audioManager.setSpeakerphoneOn(true);
            }
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void closeSpeakerOn(){
        try {
            if (audioManager != null){
                // int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
                if (audioManager.isSpeakerphoneOn()){
                    audioManager.setSpeakerphoneOn(false);
                }
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                // audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, curVolume, AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    enum CallingState {
        CANCELLED, NORMAL, REFUSED, BEREFUSED, UNANSWERED, OFFLINE, NO_RESPONSE, BUSY, VERSION_NOT_SAME
    }

}
