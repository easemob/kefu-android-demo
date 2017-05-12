package com.easemob.helpdeskdemo.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.helpdeskdemo.DemoHelper;
import com.easemob.helpdeskdemo.R;
import com.hyphenate.chat.CallStateChangeListener;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.media.LocalSurfaceView;
import com.hyphenate.media.OppositeSurfaceView;
import com.hyphenate.util.DensityUtil;
import com.hyphenate.util.EMLog;
import com.superrtc.sdk.VideoView;

import java.util.UUID;

import static com.easemob.helpdeskdemo.R.id.local_surface;

/**
 * Created by liyuzhao on 11/01/2017.
 */

public class VideoCallActivity extends CallActivity implements View.OnClickListener{

    private boolean isMuteState;
    private boolean isHandsfreeState;
    private boolean isLocalVideoOffState;
    private boolean isAnswered;
    private boolean endCallTriggerByMe = false;
    private TextView callStateTextView;
    View leftBottomView;

    private LinearLayout comingBtnContainer;
    private Button refuseBtn;
    private Button answerBtn;
    private Button hangupBtn;

    private ImageView ivSwitchCamera;
    private ImageView muteImage;
    private ImageView handsFreeImage;
    private ImageView ivLocalVideo;
    private TextView nickTextView;
    private Chronometer chronometer;
    private LinearLayout voiceContronlLayout;
    private RelativeLayout rootContainer;
    private LinearLayout topContainer;
    private LinearLayout bottomContainer;
    private TextView netwrokStatusVeiw;

    private Handler uiHandler;

    private boolean isInCalling;

    private void findViews(){
        callStateTextView = (TextView) findViewById(R.id.tv_call_state);
        comingBtnContainer = (LinearLayout) findViewById(R.id.ll_coming_call);
        rootContainer = (RelativeLayout) findViewById(R.id.root_layout);
        refuseBtn = (Button) findViewById(R.id.btn_refuse_call);
        answerBtn = (Button) findViewById(R.id.btn_answer_call);
        hangupBtn = (Button) findViewById(R.id.btn_hangup_call);
        muteImage = (ImageView) findViewById(R.id.iv_mute);

        handsFreeImage = (ImageView) findViewById(R.id.iv_handsfree);
        ivLocalVideo = (ImageView) findViewById(R.id.iv_local_video);
        callStateTextView = (TextView) findViewById(R.id.tv_call_state);
        nickTextView = (TextView) findViewById(R.id.tv_nick);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        voiceContronlLayout = (LinearLayout) findViewById(R.id.ll_voice_control);
        RelativeLayout btnsContainer = (RelativeLayout) findViewById(R.id.ll_btns);
        topContainer = (LinearLayout) findViewById(R.id.ll_top_container);
        bottomContainer = (LinearLayout) findViewById(R.id.ll_bottom_container);
        leftBottomView = findViewById(R.id.left_bottom_view);
        netwrokStatusVeiw = (TextView) findViewById(R.id.tv_network_status);
        ivSwitchCamera = (ImageView) findViewById(R.id.iv_switch_camera);
// local surfaceview
        localSurface = (LocalSurfaceView) findViewById(local_surface);
        localSurface.setZOrderMediaOverlay(true);
        localSurface.setZOrderOnTop(true);

        // remote surfaceview
        oppositeSurface = (OppositeSurfaceView) findViewById(R.id.opposite_surface);
    }

    private void setListeners(){
        refuseBtn.setOnClickListener(this);
        answerBtn.setOnClickListener(this);
        hangupBtn.setOnClickListener(this);
        muteImage.setOnClickListener(this);
        handsFreeImage.setOnClickListener(this);
        ivLocalVideo.setOnClickListener(this);
        rootContainer.setOnClickListener(this);
        ivSwitchCamera.setOnClickListener(this);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            finish();
            return;
        }
        setContentView(R.layout.em_activity_video_call);

        DemoHelper.getInstance().isVideoCalling = true;
        callType = 1;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        uiHandler = new Handler();
        findViews();
        setListeners();

        msgid = UUID.randomUUID().toString();
        isInComingCall = getIntent().getBooleanExtra("isComingCall", false);
        username = getIntent().getStringExtra("username");

        nickTextView.setText(username);


        // set call state listener
        addCallStateListener();
        if (!isInComingCall){ // outgoing call
            soundPool = new SoundPool(1, android.media.AudioManager.STREAM_RING, 0);
            outgoing = soundPool.load(this, R.raw.em_outgoing, 1);

            comingBtnContainer.setVisibility(View.INVISIBLE);
            hangupBtn.setVisibility(View.VISIBLE);
            String st = getResources().getString(R.string.Are_connected_to_each_other);
            callStateTextView.setText(st);
            ChatClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
            handler.sendEmptyMessage(MSG_CALL_MAKE_VIDEO);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    streamID = playMakeCallSounds();
                }
            }, 300);
        } else { // incoming call
            if (ChatClient.getInstance().callManager().getCallState() == CallStateChangeListener.CallState.IDLE
                    || ChatClient.getInstance().callManager().getCallState() == CallStateChangeListener.CallState.DISCONNECTED){
                // the call has ended
                finish();
                return;
            }
            voiceContronlLayout.setVisibility(View.INVISIBLE);
            localSurface.setVisibility(View.INVISIBLE);
            Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            audioManager.setMode(AudioManager.MODE_RINGTONE);
            audioManager.setSpeakerphoneOn(true);
            ringtone = RingtoneManager.getRingtone(this, ringUri);
            ringtone.play();
            ChatClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
        }

        final int MAKE_CALL_TIMEOUT = 50 * 1000;
        handler.removeCallbacks(timeoutHangup);
        handler.postDelayed(timeoutHangup, MAKE_CALL_TIMEOUT);
    }

    /**
     * set call state listener
     */
    void addCallStateListener(){

        callStateListener = new CallStateChangeListener() {
            @Override
            public void onCallStateChanged(CallState callState, final CallError error) {
                switch (callState){
                    case CONNECTING: // is connecting
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callStateTextView.setText(R.string.Are_connected_to_each_other);
                            }
                        });
                        break;
                    case CONNECTED: // connected
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callStateTextView.setText(R.string.have_connected_with);
                            }
                        });
                        break;
                    case ACCEPTED: // call is accepted
                        handler.removeCallbacks(timeoutHangup);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (soundPool != null){
                                        soundPool.stop(streamID);
                                    }
                                } catch (Exception e) {
                                }
                                openSpeakerOn();
                                handsFreeImage.setImageResource(R.drawable.em_icon_speaker_on);
                                isHandsfreeState = true;
                                isInCalling = true;
                                chronometer.setVisibility(View.VISIBLE);
                                chronometer.setBase(SystemClock.elapsedRealtime());
                                // call durations start
                                chronometer.start();
                                nickTextView.setVisibility(View.INVISIBLE);
                                callStateTextView.setText(R.string.In_the_call);
                                callingState = CallingState.NORMAL;
                            }
                        });
                        break;
                    case NETWORK_DISCONNECTED:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                netwrokStatusVeiw.setVisibility(View.VISIBLE);
                                netwrokStatusVeiw.setText(R.string.network_unavailable);
                            }
                        });
                        break;
                    case NETWORK_UNSTABLE:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                netwrokStatusVeiw.setVisibility(View.VISIBLE);
                                if (error == CallError.ERROR_NO_DATA){
                                    netwrokStatusVeiw.setText(R.string.no_call_data);
                                }else{
                                    netwrokStatusVeiw.setText(R.string.network_unstable);
                                }
                            }
                        });
                        break;
                    case NETWORK_NORMAL:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                netwrokStatusVeiw.setVisibility(View.INVISIBLE);
                            }
                        });
                        break;
                    case VIDEO_PAUSE:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "VIDEO_PAUSE", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case VIDEO_RESUME:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "VIDEO_RESUME", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case VOICE_PAUSE:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "VOICE_PAUSE", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case VOICE_RESUME:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "VOICE_RESUME", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case DISCONNECTED: // call is disconnected
                        handler.removeCallbacks(timeoutHangup);
                        final CallError fError = error;
                        runOnUiThread(new Runnable() {
                            private void postDelayedCloseMsg(){
                                uiHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        removeCallStateListener();
                                        Animation animation = new AlphaAnimation(1.0f, 0.0f);
                                        animation.setDuration(1200);
                                        rootContainer.startAnimation(animation);
                                        finish();
                                    }
                                }, 200);
                            }

                            @Override
                            public void run() {
                                chronometer.stop();
                                callDruationText = chronometer.getText().toString();
                                String s1 = getResources().getString(R.string.The_other_party_refused_to_accept);
                                String s2 = getResources().getString(R.string.Connection_failure);
                                String s3 = getResources().getString(R.string.The_other_party_is_not_online);
                                String s4 = getResources().getString(R.string.The_other_is_on_the_phone_please);
                                String s5 = getResources().getString(R.string.The_other_party_did_not_answer);

                                String s6 = getResources().getString(R.string.hang_up);
                                String s7 = getResources().getString(R.string.The_other_is_hang_up);
                                String s8 = getResources().getString(R.string.did_not_answer);
                                String s9 = getResources().getString(R.string.Has_been_cancelled);
                                String s10 = getResources().getString(R.string.Refused);

                                if (fError == CallError.REJECTED){
                                    callingState = CallingState.BEREFUSED;
                                    callStateTextView.setText(s1);
                                }else if (fError == CallError.ERROR_TRANSPORT){
                                    callStateTextView.setText(s2);
                                }else if (fError == CallError.ERROR_UNAVAILABLE){
                                    callingState = CallingState.OFFLINE;
                                    callStateTextView.setText(s3);
                                }else if (fError == CallError.ERROR_BUSY){
                                    callingState = CallingState.BUSY;
                                    callStateTextView.setText(s4);
                                }else if (fError == CallError.ERROR_NORESPONSE){
                                    callingState = CallingState.NO_RESPONSE;
                                    callStateTextView.setText(s5);
                                }else if (fError == CallError.ERROR_LOCAL_SDK_VERSION_OUTDATED
                                        || fError == CallError.ERROR_REMOTE_SDK_VERSION_OUTDATED){
                                    callingState = CallingState.VERSION_NOT_SAME;
                                    callStateTextView.setText(R.string.call_version_inconsistent);
                                }else{
                                    if (isRefused){
                                        callingState = CallingState.REFUSED;
                                        callStateTextView.setText(s10);
                                    }else if (isAnswered){
                                        callingState = CallingState.NORMAL;
                                        if (endCallTriggerByMe){
//                                            callStateTextView.setText(s6);
                                        }else{
                                            callStateTextView.setText(s7);
                                        }
                                    }else{
                                        if (isInComingCall){
                                            callingState = CallingState.UNANSWERED;
                                            callStateTextView.setText(s8);
                                        }else{
                                            if (callingState != CallingState.NORMAL){
                                                callingState = CallingState.CANCELLED;
                                                callStateTextView.setText(s9);
                                            }else{
                                                callStateTextView.setText(s6);
                                            }
                                        }
                                    }
                                }
                                postDelayedCloseMsg();
                            }
                        });
                        break;
                    default:
                        break;
                }
            }
        };

        ChatClient.getInstance().callManager().addCallStateChangeListener(callStateListener);
    }


    void removeCallStateListener(){
        ChatClient.getInstance().callManager().removeCallStateChangeListener(callStateListener);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_refuse_call: // decline the call
                isRefused = true;
                refuseBtn.setEnabled(false);
                handler.sendEmptyMessage(MSG_CALL_REJECT);
                break;
            case R.id.btn_answer_call: // answer the call
                EMLog.d(TAG, "btn_answer_call clicked");
                answerBtn.setEnabled(false);
                openSpeakerOn();
                if (ringtone != null){
                    ringtone.stop();
                }
                callStateTextView.setText(getResources().getString(R.string.answering));
                handler.sendEmptyMessage(MSG_CALL_ANSWER);
                handsFreeImage.setImageResource(R.drawable.em_icon_speaker_on);
                isAnswered = true;
                isHandsfreeState = true;
                comingBtnContainer.setVisibility(View.INVISIBLE);
                hangupBtn.setVisibility(View.VISIBLE);
                voiceContronlLayout.setVisibility(View.VISIBLE);
                localSurface.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_hangup_call: // hangup
                hangupBtn.setEnabled(false);
                chronometer.stop();
                endCallTriggerByMe = true;
                callStateTextView.setText(getResources().getString(R.string.hanging_up));
                handler.sendEmptyMessage(MSG_CALL_END);
                break;
            case R.id.iv_mute: // mute
                if (isMuteState){
                    // resume voice transfer
                    muteImage.setImageResource(R.drawable.em_icon_mute_normal);
                    try {
                        ChatClient.getInstance().callManager().resumeVoiceTransfer();
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                    isMuteState = false;
                }else{
                    // pause voice transfer
                    muteImage.setImageResource(R.drawable.em_icon_mute_on);
                    try {
                        ChatClient.getInstance().callManager().pauseVoiceTransfer();
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                    isMuteState = true;
                }
                break;
            case R.id.iv_local_video: //local video
                if (isLocalVideoOffState){
                    ivLocalVideo.setImageResource(R.drawable.em_icon_local_video_on);
                    try {
                        ChatClient.getInstance().callManager().resumeVideoTransfer();
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                    isLocalVideoOffState = false;
                }else{
                    ivLocalVideo.setImageResource(R.drawable.em_icon_local_video_off);
                    try {
                        ChatClient.getInstance().callManager().pauseVideoTransfer();
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                    isLocalVideoOffState = true;
                }
                break;
            case R.id.iv_handsfree: // handsfree
                if (isHandsfreeState){
                    // turn off speaker
                    handsFreeImage.setImageResource(R.drawable.em_icon_speaker_off);
                    closeSpeakerOn();
                    isHandsfreeState = false;
                }else{
                    handsFreeImage.setImageResource(R.drawable.em_icon_speaker_on);
                    openSpeakerOn();
                    isHandsfreeState = true;
                }
                break;
            case R.id.root_layout:
                if (callingState == CallingState.NORMAL){
                    if (bottomContainer.getVisibility() == View.VISIBLE){
                        bottomContainer.setVisibility(View.GONE);
                        topContainer.setVisibility(View.GONE);
                        oppositeSurface.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);
                    } else {
                        bottomContainer.setVisibility(View.VISIBLE);
                        topContainer.setVisibility(View.VISIBLE);
                        oppositeSurface.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFit);
                    }
                }
                break;
            case R.id.iv_switch_camera: // switch_camera
                handler.sendEmptyMessage(MSG_CALL_SWITCH_CAMERA);
                break;
            default:
                break;
        }

    }

    @Override
    protected void onDestroy() {
        DemoHelper.getInstance().isVideoCalling = false;
        localSurface.getRenderer().dispose();
        localSurface = null;
        oppositeSurface.getRenderer().dispose();
        oppositeSurface = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        callDruationText = chronometer.getText().toString();
        super.onBackPressed();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (isInCalling){
            try {
                ChatClient.getInstance().callManager().pauseVideoTransfer();
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isInCalling){
            try {
                ChatClient.getInstance().callManager().resumeVideoTransfer();
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) leftBottomView.getLayoutParams();
            layoutParams.weight = 1;
            leftBottomView.setLayoutParams(layoutParams);
            RelativeLayout.LayoutParams localSurfaceParams = (RelativeLayout.LayoutParams) localSurface.getLayoutParams();
            localSurfaceParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            localSurfaceParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            localSurfaceParams.width = DensityUtil.dip2px(this, 120);
            localSurfaceParams.height = DensityUtil.dip2px(this, 100);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                localSurfaceParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                localSurfaceParams.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
            }
            localSurface.setLayoutParams(localSurfaceParams);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) leftBottomView.getLayoutParams();
            layoutParams.weight = 0;
            leftBottomView.setLayoutParams(layoutParams);
            RelativeLayout.LayoutParams localSurfaceParams = (RelativeLayout.LayoutParams) localSurface.getLayoutParams();
            localSurfaceParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            localSurfaceParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            localSurfaceParams.width = DensityUtil.dip2px(this, 100);
            localSurfaceParams.height = DensityUtil.dip2px(this, 120);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                localSurfaceParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
                localSurfaceParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            }
            localSurface.setLayoutParams(localSurfaceParams);
        }
    }
}
