package com.hyphenate.helpdesk.videokit.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.herewhite.sdk.RoomParams;
import com.herewhite.sdk.domain.WindowAppParam;
import com.herewhite.sdk.domain.WindowParams;
import com.hyphenate.agora.AgoraStreamItem;
import com.hyphenate.agora.FunctionIconItem;
import com.hyphenate.agora.IAgoraMessageNotify;
import com.hyphenate.agora.ZuoXiSendRequestObj;
import com.hyphenate.chat.AgoraMessage;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.VecConfig;
import com.hyphenate.helpdesk.callback.ValueCallBack;
import com.hyphenate.helpdesk.easeui.util.FlatFunctionUtils;
import com.hyphenate.helpdesk.videokit.R;
import com.hyphenate.helpdesk.videokit.agora.AgoraRtcEngine;
import com.hyphenate.helpdesk.videokit.board.CloudFile;
import com.hyphenate.helpdesk.videokit.board.ConversionInfo;
import com.hyphenate.helpdesk.videokit.board.ConvertException;
import com.hyphenate.helpdesk.videokit.board.ConvertedFiles;
import com.hyphenate.helpdesk.videokit.board.ConverterCallbacks;
import com.hyphenate.helpdesk.videokit.board.FileConverter;
import com.hyphenate.helpdesk.videokit.board.PptPage;
import com.hyphenate.helpdesk.videokit.board.Scene;
import com.hyphenate.helpdesk.videokit.board.CloudActivity;
import com.hyphenate.helpdesk.videokit.ui.views.BottomContainerView;
import com.hyphenate.helpdesk.videokit.ui.views.ChangeHeightFrameLayout;
import com.hyphenate.helpdesk.videokit.ui.views.FixHeightFrameLayout;
import com.hyphenate.helpdesk.videokit.ui.views.IconTextView;
import com.hyphenate.helpdesk.videokit.ui.views.MyChronometer;
import com.hyphenate.helpdesk.videokit.ui.views.VideoItemContainerView;
import com.hyphenate.helpdesk.videokit.uitls.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import io.agora.board.fast.FastRoom;
import io.agora.board.fast.Fastboard;
import io.agora.board.fast.FastboardView;
import io.agora.board.fast.extension.FastResult;
import io.agora.board.fast.internal.PromiseResultAdapter;
import io.agora.board.fast.model.FastInsertDocParams;
import io.agora.board.fast.model.FastRegion;
import io.agora.board.fast.model.FastRoomOptions;
import io.agora.board.fast.model.FastStyle;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.video.VideoEncoderConfiguration;


/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 04/05/2018
 */

public class CallActivity extends FragmentActivity implements IAgoraMessageNotify, VideoItemContainerView.OnVideoIconViewClickListener,
        FixHeightFrameLayout.ICloseFlatCallback {

    private static final String TAG = CallActivity.class.getSimpleName();
    private final int MSG_CALL_ANSWER = 2;
    private final int MSG_CALL_END = 3;
    private final int MSG_CALL_RELEASE_HANDLER = 4;
    private final int MSG_CLEAR = 5;
    private final int MSG_UPLOAD_FLAT_FILE_SUCCESS = 6;
    private final int MSG_UPLOAD_FLAT_FILE_FAIL = 7;
    private final int MSG_DELAYED_CLOSE_DIALOG = 8;
    private final int MSG_DELAYED_FILE_CONVERTED = 9;
    private final int DELAYED_CLOSE_DIALOG = 15 * 1000;

    public static final String CURRENT_CHAT_USER_NAME = "CURRENT_CHAT_USER_NAME";

    // key
    public static final String INTENT_CALLING_TAG = "INTENT_CALLING_TAG";
    // 主动呼叫
    public static final int INTENT_CALLING_TAG_ACTIVE_VALUE = 100;
    // 被动呼叫
    public static final int INTENT_CALLING_TAG_PASSIVE_VALUE = 101;

    private final int MAKE_CALL_TIMEOUT = 60 * 1000;// 未接听，1分钟后超时关闭


    private AudioManager mAudioManager;
    private Ringtone mRingtone;
    private HeadsetReceiver mHeadsetReceiver = new HeadsetReceiver();
    private TextView mTvTitleTips;
    private VideoItemContainerView mMembersContainer;
    private View mBottomContainer;
    private LayoutInflater mInflater;
    private MyChronometer mChronometer;
    private MyChronometer mNoAcceptView;

    private FrameLayout fl_local;
    private View fl_local_camera_view;
    private ZuoXiSendRequestObj mZuoXiSendRequestObj;
    private AgoraRtcEngine mAgoraRtcEngine;

    private boolean isSharing = false;
    // private ScreenSharingClient mSSClient;
    private FixHeightFrameLayout mFixHeightFrameLayout;
    private BottomContainerView mBottomContainerView;
    private LinearLayout mLocalNameC;
    private TextView mLocalNameTv;
    private TextView mIconTextView;
    private PopupWindow mPopupWindow;
    // top 上方的容器（正在通话 + 视频）
    private View mTopView;
    // top 上方的容器（正在通话 + 视频）
    private int mTopViewHeight;
    private View mFlatRoomItem;

    private ProgressDialog mProgressDialog;
    // private View mFlatIconView;
    private ChangeHeightFrameLayout mChangHeightView;

    private String mCurrentChatUserName;
    private boolean mIsCreate;

    // 默认是被动呼叫
    public static void show(Context context, Intent i){
        Parcelable zuoXiSendRequestObj = i.getParcelableExtra("zuoXiSendRequestObj");
        Intent intent = new Intent(context.getApplicationContext(), CallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(INTENT_CALLING_TAG, INTENT_CALLING_TAG_PASSIVE_VALUE);
        intent.putExtra("zuoXiSendRequestObj", zuoXiSendRequestObj);
        context.startActivity(intent);
    }

    // 主动发起呼叫
    public static void show(Context context, String toChatUserName){
        VecConfig.newVecConfig().setVecVideo(false);
        Intent intent = new Intent(context.getApplicationContext(), CallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(INTENT_CALLING_TAG, INTENT_CALLING_TAG_ACTIVE_VALUE);
        intent.putExtra(CURRENT_CHAT_USER_NAME, toChatUserName);
        context.startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mIsActive){
            ZuoXiSendRequestObj zuoXiSendRequestObj = intent.getParcelableExtra("zuoXiSendRequestObj");
            if (!TextUtils.isEmpty(mCurrentChatUserName) && zuoXiSendRequestObj != null
                    && mCurrentChatUserName.equals(zuoXiSendRequestObj.getFrom())){
                if (!mIsCreate){
                    mIsCreate = true;
                    // 初始化
                    mZuoXiSendRequestObj = intent.getParcelableExtra("zuoXiSendRequestObj");
                    if (mZuoXiSendRequestObj != null) {
                        mZuoXis.put(mZuoXiSendRequestObj.getThreeUid(), mZuoXiSendRequestObj);
                    }
                    initVideo();
                    showAndHidden(mActiveBottomContainer, false);
                    showAndHidden(mBottomContainer, false);
                    mIsClick = true;
                    sendIsOnLineState(true);
                    mHandler.sendEmptyMessage(MSG_CALL_ANSWER);
                }
            }
        }
    }

    // 是否主动呼叫
    private boolean mIsActive;
    private View mActiveBottomContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TOTO 注意：此代码仅为演示作用，详细逻辑根据实际情况自己实现
        Log.e("hhhhhhhhh","onCreate");
        if (savedInstanceState != null) {
            finish();
            return;
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        Intent intent = getIntent();

        // 默认被动呼叫
        int active = intent.getIntExtra(INTENT_CALLING_TAG, INTENT_CALLING_TAG_PASSIVE_VALUE);
        mIsActive = active == INTENT_CALLING_TAG_ACTIVE_VALUE;
        setContentView(R.layout.activity_chat);
        showAndHidden(findViewById(R.id.statusView), false);
        if (!mIsActive){
            mZuoXiSendRequestObj = intent.getParcelableExtra("zuoXiSendRequestObj");
            if (mZuoXiSendRequestObj != null) {
                mZuoXis.put(mZuoXiSendRequestObj.getThreeUid(), mZuoXiSendRequestObj);
            }
        }else {
            mCurrentChatUserName = intent.getStringExtra(CURRENT_CHAT_USER_NAME);
        }

        if (mActiveBottomContainer == null){
            mActiveBottomContainer = findViewById(R.id.active_bottom_container);
        }

        if (mBottomContainer == null){
            mBottomContainer = findViewById(R.id.bottom_container);
        }

        showAndHidden(mBottomContainer, !mIsActive);
        showAndHidden(mActiveBottomContainer, mIsActive);

        AgoraMessage.newAgoraMessage().registerAgoraMessageNotify(getClass().getSimpleName(), this);
        if (mIsActive){
            findViewById(R.id.active_call_hangup).setOnClickListener(v -> {
                Log.e("yyyyyyyyy","onClick");
                // 主动视频，挂断
                mIsClick = true;
                mHandler.sendEmptyMessage(MSG_CALL_END);
            });

            // 超时设置
            mHandler.removeCallbacks(timeoutHangup);
            mHandler.postDelayed(timeoutHangup, MAKE_CALL_TIMEOUT);
        }else {
            initVideo();
        }
    }

    private void initVideo() {
        mChangHeightView = findViewById(R.id.changHeightView);
        mChangHeightView.setHeightChangeCallback(new ChangeHeightFrameLayout.IHeightChangeCallback() {
            @Override
            public void onHeightChanged(boolean isFullScreen) {

                if (mIsStartBoard){
                    if (isFullScreen){
                        // 判断电子白板是否全屏
                        if (mFixHeightFrameLayout.isFullScreen()){
                            showAndHidden(mBottomContainerView, false);
                            showAndHidden(mMembersContainer, false);
                        }else {
                            showAndHidden(mBottomContainerView, true);
                            showAndHidden(mMembersContainer, true);
                        }
                    }

                }else {
                    if (isFullScreen){
                        showAndHidden(mBottomContainerView, true);
                        showAndHidden(mMembersContainer, true);
                    }
                }
            }
        });

        View view = findViewById(R.id.drawAndDrawIcon);
        view.setVisibility(View.GONE);


        mTopView = findViewById(R.id.top_panel);
        mFixHeightFrameLayout = findViewById(R.id.fixFrameLayout);
        mFixHeightFrameLayout.setCloseFlatCallback(this);
        mFixHeightFrameLayout.setDefaultShowHeight();
        fl_local = findViewById(R.id.fl_local);
        fl_local_camera_view = findViewById(R.id.fl_local_camera_view);
        mLocalNameTv = findViewById(R.id.localNameTv);
        mIconTextView = findViewById(R.id.iconTextView);
        mBottomContainerView = findViewById(R.id.bcv);
        clipToOutline(mBottomContainerView);
        mChronometer = findViewById(R.id.chronometer);
        mNoAcceptView = findViewById(R.id.noAcceptView);
        mNoAcceptView.setBase(SystemClock.elapsedRealtime());
        mNoAcceptView.start();
        mMembersContainer = findViewById(R.id.rlg_container);
        mMembersContainer.setOnVideoIconViewClickListener(this);

        mBottomContainerView.setVisibility(mIsActive ? View.VISIBLE : View.GONE);

        ImageView ivAccept = findViewById(R.id.iv_accept);
        ImageView ivHangup = findViewById(R.id.iv_hangup);
        mTvTitleTips = findViewById(R.id.tv_title_tip);

        mLocalNameC = findViewById(R.id.localNameC);

        // TODO 此demo只为显示功能，具体功能逻辑根据自己项目实现
        // 动态显示底部导航条目
        switchBottomItem();

        // 接通
        ivAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomContainer.setVisibility(View.GONE);
                mBottomContainerView.setVisibility(View.VISIBLE);
                mIsClick = true;
                sendIsOnLineState(true);
                mHandler.sendEmptyMessage(MSG_CALL_ANSWER);
            }
        });

        // 挂断
        ivHangup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsClick = true;
                mHandler.sendEmptyMessage(MSG_CALL_END);
            }
        });


        mInflater = LayoutInflater.from(this);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setMode(AudioManager.MODE_RINGTONE);
        // 开启扬声器
        mAudioManager.setSpeakerphoneOn(true);

        Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mRingtone = RingtoneManager.getRingtone(this, ringUri);
        if (mRingtone != null) {
            mRingtone.play();
        }
        mHandler.removeCallbacks(timeoutHangup);
        mHandler.postDelayed(timeoutHangup, MAKE_CALL_TIMEOUT);
        registerReceiver(mHeadsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
        mStreams.clear();


        AgoraMessage.newAgoraMessage().registerAgoraMessageNotify(getClass().getSimpleName(), this);

        mAgoraRtcEngine = AgoraRtcEngine.builder()
                .build(getApplicationContext(), mZuoXiSendRequestObj.getAppId(), new IRtcEngineEventHandler() {

                    @Override
                    public void onUserJoined(int uid, int elapsed) {
                        if (uid == mMyUid) {
                            return;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e(TAG, "onUserJoined uid = " + uid);
                                ZuoXiSendRequestObj obj = mZuoXis.get(uid);
                                mUids.put(uid, uid);
                                createZuoXiSurfaceView(uid);
                                if (obj != null) {
                                    if (!obj.isAddThreeUser()) {
                                        // TextUtils.isEmpty(obj.getThreeTrueName()) || "null".equals(obj.getTrueName()) ? obj.getThreeNiceName() : obj.getThreeTrueName(), threeUid
                                        // addAgoraRadioButton(obj.getNickName(), uid);
                                        addAgoraRadioButton(obj.getNickName(), uid);
                                    } else {
                                        addAgoraRadioButton(getThreeName(obj), uid);
                                    }
                                } else {
                                    addAgoraRadioButton(mZuoXiSendRequestObj.getThreeNiceName(), uid);
                                }
                            }
                        });
                    }

                    @Override
                    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e(TAG, "onJoinChannelSuccess uid = " + uid);
                                int i = mAgoraRtcEngine.muteLocalAudioStream(!mCurrentLocalVoiceIsOpen);
                                if (i == 0) {
                                    mBottomContainerView.setCustomItemState(getIconIndex(BottomContainerView.ViewIconData.TYPE_ITEM_VOICE), mCurrentLocalVoiceIsOpen);
                                    mLocalViewItem.updateName();
                                    updateVoiceIcon(mMyUid, mCurrentLocalVoiceIsOpen);
                                }
                                int i1 = mAgoraRtcEngine.muteLocalVideoStream(!mCurrentLocalCameraIsOpen);
                                if (i1 == 0) {
                                    mBottomContainerView.setCustomItemState(getIconIndex(BottomContainerView.ViewIconData.TYPE_ITEM_CAME), mCurrentLocalCameraIsOpen);
                                    mLocalViewItem.updateCameraView();
                                    updateCamera(mMyUid, mCurrentLocalCameraIsOpen);
                                }

                                mMyUid = uid;
                                if (mTopView != null && mTopViewHeight != 0) {
                                    mTopViewHeight = mTopView.getHeight();
                                }

                            }
                        });

                    }

                    @Override
                    public void onLeaveChannel(RtcStats stats) {
                        Log.e("sssssssss", "onLeaveChannel");
                    }

                    @Override
                    public void onUserOffline(int uid, int reason) {
                        Log.e("sssssssss", "onUserOffline uid = " + uid);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 第三方离开通道
                                removeView(uid);
                            }
                        });
                    }

                    @Override
                    public void onRemoteAudioStateChanged(int uid, int state, int reason, int elapsed) {
                        super.onRemoteAudioStateChanged(uid, state, reason, elapsed);
                        // reason：5 --> 远端用户禁用 6 --> 远端用户恢复
                        // REMOTE_AUDIO_REASON_REMOTE_MUTED(5)：远端用户停止发送音频流或远端用户禁用音频模块。
                        //REMOTE_AUDIO_REASON_REMOTE_UNMUTED(6)：远端用户恢复发送音频流或远端用户启用音频模块。
                        if (reason == Constants.REMOTE_AUDIO_REASON_REMOTE_MUTED) {
                            // 远端用户停止发送音频流或远端用户禁用音频模块。
                            changeRemoteAudioState(uid, false);
                        } else if (reason == Constants.REMOTE_AUDIO_REASON_REMOTE_UNMUTED) {
                            // 远端用户恢复发送音频流或远端用户启用音频模块。
                            changeRemoteAudioState(uid, true);
                        }

                    }

                    @Override
                    public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
                        super.onRemoteVideoStateChanged(uid, state, reason, elapsed);

                        if (reason == Constants.REMOTE_VIDEO_STATE_REASON_REMOTE_MUTED) {
                            // 远端用户停止发送视频流或远端用户禁用视频模块。
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateCamera(uid, false);
                                }
                            });

                        } else if (reason == Constants.REMOTE_VIDEO_STATE_REASON_REMOTE_UNMUTED) {
                            // 远端用户恢复发送视频流或远端用户启用视频模块。
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateCamera(uid, true);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(int err) {
                        super.onError(err);
                        Log.e(TAG,"err = "+err);
                    }

                });

        // 初始化屏幕共享进程
        // mSSClient = ScreenSharingClient.getInstance();

        // mSSClient.setListener(mListener);
    }

    // 动态显示底部导航条目
    private void switchBottomItem() {
        List<FunctionIconItem> functionIconItems = FlatFunctionUtils.get().getIconItems();
        /*List<FunctionIconItem> functionIconItems = new ArrayList<>();
        FunctionIconItem iconItem1 = new FunctionIconItem("shareDesktop");
        iconItem1.setStatus("enable");
        FunctionIconItem iconItem2 = new FunctionIconItem("whiteBoard");
        iconItem2.setStatus("enable");
        functionIconItems.add(iconItem1);
        functionIconItems.add(iconItem2);*/
        if (functionIconItems.size() == 0) {
            // 默认显示3个图标 + 0 = 显示3个图标
            addIcon_0();
        } else if (functionIconItems.size() == 1) {
            // 默认显示3个图标 + 1 = 显示4个图标
            addIcon_1(functionIconItems);
        } else if (functionIconItems.size() == 2) {
            // 默认显示3个图标 + 2 = 显示5个图标
            // 电子白板 and 屏幕分享
            addIcon_2(functionIconItems);
        }
    }


    private void changeRemoteAudioState(int uid, boolean isOpen) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AgoraStreamItem agoraStreamItem = getRealViewItem(uid);
                if (agoraStreamItem != null) {
                    agoraStreamItem.setOpenVoice(isOpen);
                }
            }
        });

    }


    private void removeView(int uid) {

        // 判断本地视图是否就是退出的用户，如果是，现将自己视图替换回来
        if (mLocalViewItem != null && mLocalViewItem.getRealUid() == uid) {
            // 取出自己视图
            AgoraStreamItem realViewItem = getExcludeLocalRealViewItem(mMyUid);
            if (realViewItem == null) {
                return;
            }

            FrameLayout remoteView = realViewItem.getRemoteView();
            // 替换
            String name = realViewItem.getName();
            int realUid = realViewItem.getRealUid();
            TextureView surfaceView = realViewItem.getSurfaceView();
            boolean openVoice = realViewItem.isOpenVoice();

            realViewItem.setName(mLocalViewItem.getName());
            realViewItem.setRealUid(mLocalViewItem.getRealUid());
            realViewItem.setSurfaceView(mLocalViewItem.getSurfaceView());
            realViewItem.setOpenVoice(mLocalViewItem.isOpenVoice());


            TextureView remoteSurfaceView = mLocalViewItem.getSurfaceView();
            ViewParent remoteParent = remoteSurfaceView.getParent();
            if (remoteParent instanceof ViewGroup) {
                ((ViewGroup) remoteParent).removeView(remoteSurfaceView);
            }
            remoteView.removeAllViews();
            // 远程视图显示
            remoteView.addView(remoteSurfaceView);
            realViewItem.updateName();

            mLocalViewItem.setName(name);
            mLocalViewItem.setRealUid(realUid);
            mLocalViewItem.setSurfaceView(surfaceView);
            mLocalViewItem.setOpenVoice(openVoice);

            ViewParent parent = surfaceView.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(surfaceView);
            }
            // 本地视图显示
            fl_local.addView(surfaceView);
            mLocalViewItem.updateName();

            // 此uid并不是要被删除的uid，需要更换
            int listUid = realViewItem.getUid();
            mMembersContainer.removeVideoIconView(realViewItem.getUid(), uid);


            // 找出集合里删除的uid
            AgoraStreamItem deleteItem = getAgoraStreamItem(uid);
            if (deleteItem != null) {
                deleteItem.setUid(listUid);
            }
            AgoraStreamItem remove = mStreams.remove(uid);
            if (remove != null) {
                mStreams.put(listUid, remove);
            }


        } else {
            // 获取uid对应的真实uid
            AgoraStreamItem realViewItem = getExcludeLocalRealViewItem(uid);
            if (realViewItem != null) {
                // 此uid并不是要被删除的uid，需要更换
                int listUid = realViewItem.getUid();
                mMembersContainer.removeVideoIconView(realViewItem.getUid(), listUid);

                // 找出集合里删除的uid
                AgoraStreamItem deleteItem = getAgoraStreamItem(uid);
                if (deleteItem != null) {
                    deleteItem.setUid(listUid);
                }
                AgoraStreamItem remove = mStreams.remove(uid);
                if (remove != null) {
                    mStreams.put(listUid, remove);
                }
            }
        }

        // 第三方离开通道
        mUids.remove(uid);
        mZuoXis.remove(uid);


        notifyTitleTips();
        if (mUids.size() < 1) {
            // 关闭页面
            mHandler.sendEmptyMessage(MSG_CALL_END);
        }
    }

    private void removeFlatView(int uid) {

        // 判断本地视图是否就是退出的用户，如果是，现将自己视图替换回来
        if (mLocalViewItem != null && mLocalViewItem.getRealUid() == uid) {
            // 取出自己视图
            AgoraStreamItem realViewItem = getAgoraStreamItem(sFlatUuid);
            if (realViewItem == null) {
                return;
            }

            FrameLayout remoteView = realViewItem.getRemoteView();
            // 替换
            String name = realViewItem.getName();
            int realUid = realViewItem.getRealUid();
            TextureView surfaceView = realViewItem.getSurfaceView();
            boolean openVoice = realViewItem.isOpenVoice();
            View flatView = realViewItem.getFlatView();

            realViewItem.setName(mLocalViewItem.getName());
            realViewItem.setRealUid(mLocalViewItem.getRealUid());
            realViewItem.setSurfaceView(mLocalViewItem.getSurfaceView());
            realViewItem.setOpenVoice(mLocalViewItem.isOpenVoice());
            realViewItem.setFlatView(mLocalViewItem.getFlatView());

            if (realViewItem.getRealUid() == sFlatUuid) {
                View remoteSurfaceView = realViewItem.getFlatView();
                ViewParent remoteParent = remoteSurfaceView.getParent();
                if (remoteParent instanceof ViewGroup) {
                    ((ViewGroup) remoteParent).removeView(remoteSurfaceView);
                }
                remoteView.removeAllViews();
                // 远程视图显示
                remoteView.addView(remoteSurfaceView);
            } else {
                TextureView remoteSurfaceView = realViewItem.getSurfaceView();
                ViewParent remoteParent = remoteSurfaceView.getParent();
                if (remoteParent instanceof ViewGroup) {
                    ((ViewGroup) remoteParent).removeView(remoteSurfaceView);
                }
                remoteView.removeAllViews();
                // 远程视图显示
                remoteView.addView(remoteSurfaceView);
            }


            realViewItem.updateName();

            mLocalViewItem.setName(name);
            mLocalViewItem.setRealUid(realUid);
            mLocalViewItem.setSurfaceView(surfaceView);
            mLocalViewItem.setOpenVoice(openVoice);
            mLocalViewItem.setFlatView(flatView);

            if (mLocalViewItem.getRealUid() == sFlatUuid) {
                View fv = mLocalViewItem.getFlatView();
                ViewParent parent = fv.getParent();
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(fv);
                }
                // 本地视图显示
                fl_local.addView(fv);
            } else {
                TextureView fv = mLocalViewItem.getSurfaceView();
                ViewParent parent = fv.getParent();
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(fv);
                }
                // 本地视图显示
                fl_local.addView(fv);
            }

            mLocalViewItem.updateName();
            mMembersContainer.removeVideoIconView(uid);
            mStreams.remove(uid);
        }

        notifyTitleTips();
    }

    // 电子白板相关
    private FastboardView fastboardView;
    private FastRoom fastRoom;

    private void loadFlatData(View view, ZuoXiSendRequestObj obj) {
        fastboardView = view.findViewById(R.id.fastboard_view);
        IconTextView cloudView = view.findViewById(R.id.cloud);
        cloudView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CallActivity.this, CloudActivity.class);
                intent.putExtra(CloudActivity.KEY_TYPE, false);
                startActivityForResult(intent, CloudActivity.UPLOAD_REQUEST);
            }
        });
        String uid = String.valueOf(obj.getUid());
        Fastboard fastboard = fastboardView.getFastboard();
        FastRoomOptions roomOptions = new FastRoomOptions(
                obj.getAppIdentifier(),
                obj.getRoomUUID(),
                obj.getRoomToken(),
                uid,
                FastRegion.CN_HZ
        );

        float ratio = 1f;
        RoomParams params = new RoomParams(obj.getRoomUUID(), obj.getRoomToken(), uid);
        WindowParams windowParams = new WindowParams();
        windowParams.setContainerSizeRatio(ratio);
        windowParams.setChessboard(false);
        params.setWindowParams(windowParams);
        roomOptions.setRoomParams(params);
        fastboard.setWhiteboardRatio(ratio);

        fastRoom = fastboard.createFastRoom(roomOptions);
        fastRoom.join();
        updateFastStyle();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration config) {
        super.onConfigurationChanged(config);
        updateFastStyle();
    }

    private void updateFastStyle() {
        // global style change
        FastStyle fastStyle = fastRoom.getFastStyle();
        fastStyle.setDarkMode(Utils.isDarkMode(this));
        fastStyle.setMainColor(Utils.getThemePrimaryColor(this));
        fastRoom.setFastStyle(fastStyle);
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void closeFlat(boolean isFullScreen) {
        mIsRunAnim = false;
        // 关闭电子白板
        mIsStartBoard = false;
        mIsSend = false;
        mFlatRoomItem = null;
        removeFlatView(sFlatUuid);
        mBottomContainerView.setCustomItemState(getIconIndex(BottomContainerView.ViewIconData.TYPE_ITEM_FLAT), true);
        showAndHidden(mMembersContainer, true);
        showAndHidden(mBottomContainerView, true);
        if (!isFullScreen){
            mFixHeightFrameLayout.showFullHeight();
        }

        try {
            if (fastRoom != null){
                fastRoom.destroy();
                fastRoom = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // 当全屏或者恢复正在执行动画，这时时不允许点击顶部的视图
    private boolean mIsRunAnim;

    @Override
    public void onFullScreenCompleted(boolean isFullScreen) {
        mIsRunAnim = false;
    }

    private void showAndHidden(View view, boolean isShow) {
        if (isShow && view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
            return;
        }

        if (!isShow && view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        }
    }

    private boolean mIsStartBoard;

    // 默认
    private boolean mCurrentLocalVoiceIsOpen = true;
    private boolean mCurrentLocalCameraIsOpen = true;

    private void updateVoiceIcon(int uid, boolean openVoice) {
        if (mLocalViewItem != null) {
            if (mLocalViewItem.getRealUid() == uid) {
                mLocalViewItem.setOpenVoice(openVoice);
                mLocalViewItem.updateVoiceIcon();
                return;
            }
        }

        AgoraStreamItem realViewItem = getExcludeLocalRealViewItem(uid);
        if (realViewItem != null) {
            realViewItem.setOpenVoice(openVoice);
            realViewItem.updateVoiceIcon();
        }

    }

    private void updateCamera(int uid, boolean openCamera) {
        if (mLocalViewItem != null) {
            if (mLocalViewItem.getRealUid() == uid) {
                mLocalViewItem.setOpenCamera(openCamera);
                mLocalViewItem.updateCameraView();
                return;
            }
        }

        AgoraStreamItem realViewItem = getExcludeLocalRealViewItem(uid);
        if (realViewItem != null) {
            realViewItem.setOpenCamera(openCamera);
            realViewItem.updateCameraView();
        }

    }

    private AgoraStreamItem getExcludeLocalRealViewItem(int uid) {
        for (AgoraStreamItem key : mStreams.values()) {
            if (uid == key.getRealUid()) {
                return key;
            }
        }
        return null;
    }

    private AgoraStreamItem getRealViewItem(int uid) {
        if (mLocalViewItem != null) {
            if (mLocalViewItem.getRealUid() == uid) {
                return mLocalViewItem;
            }
        }
        for (AgoraStreamItem key : mStreams.values()) {
            if (uid == key.getRealUid()) {
                return key;
            }
        }
        return null;
    }

    // * 2 * 12
    /*private final ScreenSharingClient.IStateListener mListener = new ScreenSharingClient.IStateListener() {
        @Override
        public void onError(int error) {
            Log.e(TAG, "Screen share service error happened: " + error);
        }

        @Override
        public void onTokenWillExpire() {
            //mSSClient.renewToken(null);
            mSSClient.renewToken(mZuoXiSendRequestObj.getToken()); // Replace the token with your valid token
        }

        // 分享桌面，弹出对话框点击确认回调
        @Override
        public void onDialogStart() {
            // 权限点击确认
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBottomContainerView.setCustomItemState(getIconIndex(BottomContainerView.ViewIconData.TYPE_ITEM_SHARE), true);
                    // 恢复初始化对象，加入缓存
                    // 记录位置
                    *//*initAgoraStreamItem();
                    mAgoraRtcEngine.leaveChannel();
                    mBottomContainerView.setCustomItemState(getIconIndex(BottomContainerView.ViewIconData.TYPE_ITEM_SHARE), true);
                    new Handler(getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            joinChannel(mZuoXiSendRequestObj);
                        }
                    }, 200);*//*
                }
            });

        }

        @Override
        public void onDialogDeniedError(int error) {
            // 权限拒绝
            isSharing = false;
        }
    };*/

    private void initAgoraStreamItem() {
        mCacheLocalViewShowPositionUid = -1;
        mCacheLocalViewRealUid = mLocalViewItem.getRealUid();
        AgoraStreamItem realViewItem = getRealViewItem(mMyUid);
        if (realViewItem != null) {
            mCacheLocalViewShowPositionUid = realViewItem.getUid();
        }

        for (AgoraStreamItem item : mStreams.values()) {
            item.init();
        }
    }

    private void shareWindow() {
        // 执行屏幕共享进程，将 App ID，channel ID 等信息发送给屏幕共享进程
        if (!isSharing) {
            isSharing = true;
            /*ScreenCaptureParameters screenCaptureParameters = new ScreenCaptureParameters();
            //screenCaptureParameters.captureAudio = true;
            screenCaptureParameters.captureVideo = true;
            ScreenCaptureParameters.VideoCaptureParameters videoCaptureParameters = new ScreenCaptureParameters.VideoCaptureParameters();
            screenCaptureParameters.videoCaptureParameters = videoCaptureParameters;*/
            /*mSSClient.startLocalScreenSharing(this, mAgoraRtcEngine.getEngine(), screenCaptureParameters, new ExternalScreenSharingCallback() {
                @Override
                public void onExtenalScreenCaptureEvent(int event, int error) {

                }
            });*/

            // mSSClient.updateScreenCaptureParametersInternal(true, false, videoCaptureParameters);

            // String appId, String token, String channelName, int uid, VideoEncoderConfiguration vec
            /*mSSClient.startExternalScreenSharing(this, mZuoXiSendRequestObj.getAppId(), mZuoXiSendRequestObj.getToken(),
                    mZuoXiSendRequestObj.getChannel(), mZuoXiSendRequestObj.getUid(), new VideoEncoderConfiguration(
                            getScreenDimensions(),
                            VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
                            VideoEncoderConfiguration.STANDARD_BITRATE,
                            VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE));*/


            // Context context, ScreenCaptureParameters screenCaptureParameters, ExternalScreenSharingCallback callback

            mAgoraRtcEngine.startScreenCapture();

            mBottomContainerView.setCustomItemState(getIconIndex(BottomContainerView.ViewIconData.TYPE_ITEM_SHARE), false);

            /*mSSClient.start(getApplication(), "74855635d3a64920b0c7ee3684f68a9f", "00674855635d3a64920b0c7ee3684f68a9fIAAXCVKXKtDOGEC4VAK93Q1ykf5PGtEY6NC9lbICNW6SKho6pkUAAAAAEACr2kwLZ5B7YgEAAQBmkHti",
                    "huanxin", 0, new VideoEncoderConfiguration(
                            getScreenDimensions(),
                            VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
                            VideoEncoderConfiguration.STANDARD_BITRATE,
                            VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE));*/

            // mAgoraRtcEngine.aa();

        } else {
            // mSSClient.stop(this);
            mAgoraRtcEngine.stopScreenCapture();
            //mSSClient.stop();
            isSharing = false;
            mBottomContainerView.setCustomItemState(getIconIndex(BottomContainerView.ViewIconData.TYPE_ITEM_SHARE), true);
            //joinChannel(mZuoXiSendRequestObj);
        }
    }

    /*private void shareWindow() {
        // 执行屏幕共享进程，将 App ID，channel ID 等信息发送给屏幕共享进程
        if (!isSharing) {
            mSSClient.start(getApplication(), mZuoXiSendRequestObj.getAppId(), mZuoXiSendRequestObj.getToken(),
                    mZuoXiSendRequestObj.getChannel(), mZuoXiSendRequestObj.getUid(), new VideoEncoderConfiguration(
                            getScreenDimensions(),
                            VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
                            VideoEncoderConfiguration.STANDARD_BITRATE,
                            VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE));

            *//*mSSClient.start(getApplication(), "74855635d3a64920b0c7ee3684f68a9f", "00674855635d3a64920b0c7ee3684f68a9fIAAXCVKXKtDOGEC4VAK93Q1ykf5PGtEY6NC9lbICNW6SKho6pkUAAAAAEACr2kwLZ5B7YgEAAQBmkHti",
                    "huanxin", 0, new VideoEncoderConfiguration(
                            getScreenDimensions(),
                            VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
                            VideoEncoderConfiguration.STANDARD_BITRATE,
                            VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE));*//*
            isSharing = true;
        } else {
            mSSClient.stop(getApplication());
            isSharing = false;
            mBottomContainerView.setCustomItemState(getIconIndex(BottomContainerView.ViewIconData.TYPE_ITEM_SHARE), false);
            //joinChannel(mZuoXiSendRequestObj);
        }
    }*/


    @Override
    public void iconViewClick(int index, View view, Object tag) {
        // 当正在运行动画时，不允许执行点击
        if (mIsRunAnim) {
            return;
        }
        // 切换
        if (fl_local.getChildCount() > 0) {
            fl_local.removeAllViews();
        }

        if (tag instanceof Integer) {
            AgoraStreamItem agoraStreamItem = getAgoraStreamItem((Integer) tag);
            if (agoraStreamItem == null) {
                return;
            }
            FrameLayout remoteView = agoraStreamItem.getRemoteView();
            View flatView = agoraStreamItem.getFlatView();
            TextureView surfaceView = agoraStreamItem.getSurfaceView();
            String name = agoraStreamItem.getName();
            boolean openVoice = agoraStreamItem.isOpenVoice();
            boolean openCamera = agoraStreamItem.isOpenCamera();
            int realUid = agoraStreamItem.getRealUid();

            agoraStreamItem.setName(mLocalViewItem.getName());
            agoraStreamItem.setFlatView(mLocalViewItem.getFlatView());
            agoraStreamItem.setSurfaceView(mLocalViewItem.getSurfaceView());
            agoraStreamItem.setOpenVoice(mLocalViewItem.isOpenVoice());
            agoraStreamItem.setOpenCamera(mLocalViewItem.isOpenCamera());
            agoraStreamItem.setRealUid(mLocalViewItem.getRealUid());
            //mMembersContainer.set((Integer) tag, mLocalViewItem.getRealUid());
            remoteView.removeAllViews();
            if (mLocalViewItem.getRealUid() == sFlatUuid) {
                View remoteSurfaceView = mLocalViewItem.getFlatView();
                removeViewFromParent(remoteSurfaceView);
                // 远程视图显示
                remoteView.addView(remoteSurfaceView);
            } else {
                TextureView remoteSurfaceView = mLocalViewItem.getSurfaceView();
                removeViewFromParent(remoteSurfaceView);
                // 远程视图显示
                remoteView.addView(remoteSurfaceView);
                // remoteSurfaceView.setZOrderOnTop(true);
                // remoteSurfaceView.setZOrderMediaOverlay(true);
            }
			/*SurfaceView remoteSurfaceView = mLocalViewItem.getSurfaceView();
			removeViewFromParent(remoteSurfaceView);
			// 远程视图显示
			remoteView.addView(remoteSurfaceView);*/
            // 更新名称
            agoraStreamItem.updateName();

            // 记录本地视图
            mLocalViewItem.setSurfaceView(surfaceView);
            mLocalViewItem.setFlatView(flatView);
            mLocalViewItem.setName(name);
            mLocalViewItem.setOpenVoice(openVoice);
            mLocalViewItem.setOpenCamera(openCamera);
            mLocalViewItem.setRealUid(realUid);
            if (realUid == sFlatUuid) {
                removeViewFromParent(flatView);
                // 本地视图显示
                fl_local.addView(flatView);
            } else {
                removeViewFromParent(surfaceView);
                // 本地视图显示
                fl_local.addView(surfaceView);
                // surfaceView.setZOrderOnTop(false);
                // surfaceView.setZOrderMediaOverlay(false);
            }

            // 更新名称
            mLocalViewItem.updateName();

        }

    }


    private Map<Integer, Integer> mUids = new HashMap<>();

    private AgoraStreamItem getAgoraStreamItem(Integer uid) {
        return mStreams.get(uid);
    }

    private int mMyUid;
    // 记录本地视图上显示的真实uid
    private int mCacheLocalViewRealUid = mMyUid;
    // 记录本地视图在上方显示的位置对应的uid
    private int mCacheLocalViewShowPositionUid;
    // 记录本地视图显示的临时位置
    private int mCurrentLocalViewShowPositionUid = -1;
    private AgoraStreamItem mLocalViewItem;

    private void joinChannel(ZuoXiSendRequestObj obj) {
        if (fl_local.getChildCount() > 0) {
            fl_local.removeAllViews();
        }
        // 自己uid getUid()
        mMyUid = obj.getUid();
        TextureView surfaceView;
        // 记录本地View
        if (mLocalViewItem == null) {
            surfaceView = mAgoraRtcEngine.createTextureView();
            mLocalViewItem = new AgoraStreamItem();
            mLocalViewItem.setSurfaceView(surfaceView);
        } else {
            surfaceView = mLocalViewItem.getSurfaceView();
            removeViewFromParent(surfaceView);
        }

        mLocalViewItem.setUid(obj.getUid());
        mLocalViewItem.setRealUid(obj.getUid());
        mLocalViewItem.setOpenVoice(mCurrentLocalVoiceIsOpen);
        mLocalViewItem.setOpenCamera(mCurrentLocalCameraIsOpen);
        mLocalViewItem.setRemoteView(fl_local);
        mLocalViewItem.setCameraView(fl_local_camera_view);
        mLocalViewItem.setName(getName(obj));
        mLocalViewItem.setTextViewName(mLocalNameTv);
        mLocalViewItem.setIconTextView(mIconTextView);
        mLocalViewItem.updateVoiceIcon();
        mLocalViewItem.updateCameraView();

        fl_local.addView(surfaceView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mAgoraRtcEngine.setupLocalVideo(surfaceView, AgoraRtcEngine.RENDER_MODE_HIDDEN, obj.getUid());
        mAgoraRtcEngine.joinChannel(obj.getToken(), obj.getChannel(), obj.getUid());

        Log.e("CallActivity", "uid = "+obj.getUid());
        Log.e("CallActivity", "channel = "+obj.getChannel());

    }

    private void clipToOutline(View surfaceView){
        surfaceView.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                Rect rect = new Rect();
                view.getGlobalVisibleRect(rect);
                int leftMargin = 0;
                int topMargin = 0;
                Rect selfRect = new Rect(leftMargin, topMargin,
                        rect.right - rect.left - leftMargin,
                        rect.bottom - rect.top - topMargin);
                outline.setRoundRect(selfRect, dp2px(10));
            }
        });
        surfaceView.setClipToOutline(true);

    }

    private VideoEncoderConfiguration.VideoDimensions getScreenDimensions() {
        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return new VideoEncoderConfiguration.VideoDimensions(outMetrics.widthPixels / 2, outMetrics.heightPixels / 2);
    }


    private View getVideoItemLayoutView(int uid) {
        if (mMembersContainer != null) {
            for (int i = 0; i < mMembersContainer.getVideoIconViewCount(); i++) {
                View childAt = mMembersContainer.getVideoIconView(i);
                Object tag = childAt.getTag();
                if (tag instanceof Integer) {
                    Integer u = (Integer) tag;
                    if (u == uid) {
                        return childAt;
                    }
                }
            }
        }
        return null;
    }

    private void addAgoraRadioButton(String niceName, int uid) {
        niceName = TextUtils.isEmpty(niceName) ? "" : niceName;

        View videoItemLayoutView = getVideoItemLayoutView(uid);
        TextView ivNick;
        TextView iconTextView;
        View cameraView;
        View whiteboardDef;
        if (videoItemLayoutView == null) {
            videoItemLayoutView = mInflater.inflate(R.layout.layout_call_head_item_new, null);
            clipToOutline(videoItemLayoutView);
            videoItemLayoutView.setTag(uid);
            // radioLayoutView.setId(getViewIdByMemberName(String.valueOf(uid)));
            ivNick = videoItemLayoutView.findViewById(R.id.tv_nick);
            iconTextView = videoItemLayoutView.findViewById(R.id.remoteIconTextView);
            cameraView = videoItemLayoutView.findViewById(R.id.cameraView);
            ivNick.setText(niceName);
            mMembersContainer.addVideoIconView(uid, videoItemLayoutView);

        } else {
            ivNick = videoItemLayoutView.findViewById(R.id.tv_nick);
            ivNick.setText(niceName);
            iconTextView = videoItemLayoutView.findViewById(R.id.remoteIconTextView);
            cameraView = videoItemLayoutView.findViewById(R.id.cameraView);
        }
        notifyTitleTips();
        whiteboardDef = videoItemLayoutView.findViewById(R.id.whiteboardDef);
        // TODO 保存remoteView
        FrameLayout remoteView = videoItemLayoutView.findViewById(R.id.remoteView);
        AgoraStreamItem ziJiSurfaceView = getAgoraStreamItem(uid);
        TextureView zuoXiSurfaceView = ziJiSurfaceView.getSurfaceView();
        removeViewFromParent(zuoXiSurfaceView);
        ziJiSurfaceView.setName(niceName);
        ziJiSurfaceView.setRemoteView(remoteView);
        ziJiSurfaceView.setCameraView(cameraView);
        ziJiSurfaceView.setTextViewName(ivNick);
        ziJiSurfaceView.setIconTextView(iconTextView);
        ziJiSurfaceView.setWhiteboardDef(whiteboardDef);
        // 远端音频状态，默认是开启
        ziJiSurfaceView.setOpenVoice(true);
        // 远端相机视图是否开启
        ziJiSurfaceView.setOpenCamera(true);
        // 显示
        remoteView.addView(zuoXiSurfaceView);
        mAgoraRtcEngine.setupRemoteVideo(zuoXiSurfaceView, uid);
        // 更新名称
        ziJiSurfaceView.updateName();
        mLocalViewItem.updateName();

        // 屏幕共享，还原视图
        if (mCacheLocalViewRealUid != mMyUid) {
            if (uid == mCacheLocalViewRealUid) {
                // 切换
                switchView(uid);
                if (mCacheLocalViewShowPositionUid != uid) {
                    // 记录本地视图临时显示的位置，并不是最终位置
                    mCurrentLocalViewShowPositionUid = uid;
                }

            } else {
                if (mCacheLocalViewShowPositionUid == uid) {
                    // 替换
                    switchViewAtTopPosition(mCurrentLocalViewShowPositionUid, uid);
                }
            }
        }
    }

    private final static int sFlatUuid = Integer.MAX_VALUE;

    // 是否开启电子白板
    // 显示电子白板
    private void showFlat(View view, int uid) {
        String niceName = "白板";
        AgoraStreamItem flatItem = mStreams.get(uid);
        if (flatItem == null) {
            flatItem = new AgoraStreamItem();
            flatItem.setFlatView(view);
            flatItem.setUid(uid);
            flatItem.setRealUid(uid);
            flatItem.setName(niceName);
            mStreams.put(uid, flatItem);
        } else {
            flatItem.setFlatView(view);
            flatItem.setUid(uid);
            flatItem.setRealUid(uid);
            flatItem.setName(niceName);
        }


        View videoItemLayoutView = getVideoItemLayoutView(uid);
        TextView ivNick;
        TextView iconTextView;
        View cameraView;
        View whiteboardDef;
        if (videoItemLayoutView == null) {
            videoItemLayoutView = mInflater.inflate(R.layout.layout_call_head_item_new, null);
            clipToOutline(videoItemLayoutView);
            videoItemLayoutView.setTag(uid);
            ivNick = videoItemLayoutView.findViewById(R.id.tv_nick);
            iconTextView = videoItemLayoutView.findViewById(R.id.remoteIconTextView);
            ivNick.setText(niceName);
            cameraView = videoItemLayoutView.findViewById(R.id.cameraView);
            mMembersContainer.addVideoIconView(0, uid, videoItemLayoutView);

        } else {
            ivNick = videoItemLayoutView.findViewById(R.id.tv_nick);
            cameraView = videoItemLayoutView.findViewById(R.id.cameraView);
            ivNick.setText(niceName);
            iconTextView = videoItemLayoutView.findViewById(R.id.remoteIconTextView);
        }
        whiteboardDef = videoItemLayoutView.findViewById(R.id.whiteboardDef);
        // TODO 保存remoteView
        FrameLayout remoteView = videoItemLayoutView.findViewById(R.id.remoteView);
        flatItem.setRemoteView(remoteView);
        flatItem.setCameraView(cameraView);
        flatItem.setTextViewName(ivNick);
        flatItem.setIconTextView(iconTextView);
        flatItem.setFlatView(view);
        flatItem.setWhiteboardDef(whiteboardDef);
        // 远端音频状态，默认是开启
        flatItem.setOpenVoice(true);
        // flatItem.setOpenCamera(mCurrentLocalCameraIsOpen);
        flatItem.setOpenCamera(true);
        // 显示
        removeViewFromParent(view);
        remoteView.addView(view);

        // 更新名称
        flatItem.updateName();


        // 替换
        FrameLayout flt = flatItem.getRemoteView();
        View surfaceView = flatItem.getFlatView();

        String name = flatItem.getName();
        boolean openVoice = flatItem.isOpenVoice();
        boolean openCamera = flatItem.isOpenCamera();
        int realUid = flatItem.getRealUid();


        flatItem.setName(mLocalViewItem.getName());
        flatItem.setSurfaceView(mLocalViewItem.getSurfaceView());
        flatItem.setOpenVoice(mLocalViewItem.isOpenVoice());
        flatItem.setOpenCamera(mLocalViewItem.isOpenCamera());
        flatItem.setRealUid(mLocalViewItem.getRealUid());
        flatItem.setFlatView(mLocalViewItem.getFlatView());


        flt.removeAllViews();
        TextureView remoteSurfaceView = mLocalViewItem.getSurfaceView();
        removeViewFromParent(remoteSurfaceView);
        // 远程视图显示
        flt.addView(remoteSurfaceView);
        // 更新名称
        flatItem.updateName();

        // 记录本地视图
        mLocalViewItem.setFlatView(surfaceView);
        mLocalViewItem.setName(name);
        mLocalViewItem.setOpenVoice(openVoice);
        mLocalViewItem.setOpenCamera(openCamera);
        mLocalViewItem.setRealUid(realUid);

        removeViewFromParent(surfaceView);
        // 本地视图显示
        fl_local.addView(surfaceView);
        // 更新名称
        mLocalViewItem.updateName();
        mStreams.get(sFlatUuid);
    }

    private void switchViewAtTopPosition(int localUid, int uid) {
        AgoraStreamItem localStreamItem = getAgoraStreamItem(localUid);
        if (localStreamItem == null) {
            return;
        }
        FrameLayout fl_local = localStreamItem.getRemoteView();
        fl_local.removeAllViews();


        AgoraStreamItem agoraStreamItem = getAgoraStreamItem(uid);
        FrameLayout remoteView = agoraStreamItem.getRemoteView();
        TextureView surfaceView = agoraStreamItem.getSurfaceView();
        String name = agoraStreamItem.getName();
        boolean openVoice = agoraStreamItem.isOpenVoice();
        boolean openCamera = agoraStreamItem.isOpenCamera();
        int realUid = agoraStreamItem.getRealUid();


        agoraStreamItem.setName(localStreamItem.getName());
        agoraStreamItem.setSurfaceView(localStreamItem.getSurfaceView());
        agoraStreamItem.setOpenVoice(localStreamItem.isOpenVoice());
        agoraStreamItem.setOpenCamera(localStreamItem.isOpenCamera());
        agoraStreamItem.setRealUid(localStreamItem.getRealUid());

        remoteView.removeAllViews();
        TextureView remoteSurfaceView = localStreamItem.getSurfaceView();
        removeViewFromParent(remoteSurfaceView);
        // 远程视图显示
        remoteView.addView(remoteSurfaceView);
        // 更新名称
        agoraStreamItem.updateName();

        // 记录本地视图
        localStreamItem.setSurfaceView(surfaceView);
        localStreamItem.setName(name);
        localStreamItem.setOpenVoice(openVoice);
        localStreamItem.setOpenCamera(openCamera);
        localStreamItem.setRealUid(realUid);

        removeViewFromParent(surfaceView);
        // 本地视图显示
        fl_local.addView(surfaceView);
        // 更新名称
        localStreamItem.updateName();

    }

    private void switchView(int uid) {
        if (fl_local.getChildCount() > 0) {
            fl_local.removeAllViews();
        }

        AgoraStreamItem agoraStreamItem = getAgoraStreamItem(uid);
        FrameLayout remoteView = agoraStreamItem.getRemoteView();
        TextureView surfaceView = agoraStreamItem.getSurfaceView();
        String name = agoraStreamItem.getName();
        boolean openVoice = agoraStreamItem.isOpenVoice();
        boolean openCamera = agoraStreamItem.isOpenCamera();
        int realUid = agoraStreamItem.getRealUid();

        agoraStreamItem.setName(mLocalViewItem.getName());
        agoraStreamItem.setSurfaceView(mLocalViewItem.getSurfaceView());
        agoraStreamItem.setOpenVoice(mLocalViewItem.isOpenVoice());
        agoraStreamItem.setOpenCamera(mLocalViewItem.isOpenCamera());
        agoraStreamItem.setRealUid(mLocalViewItem.getRealUid());
        //mMembersContainer.set(uid, mLocalViewItem.getRealUid());
        remoteView.removeAllViews();
        TextureView remoteSurfaceView = mLocalViewItem.getSurfaceView();
        removeViewFromParent(remoteSurfaceView);
        // 远程视图显示
        remoteView.addView(remoteSurfaceView);
        // 更新名称
        agoraStreamItem.updateName();

        // 记录本地视图
        mLocalViewItem.setSurfaceView(surfaceView);
        mLocalViewItem.setName(name);
        mLocalViewItem.setOpenVoice(openVoice);
        mLocalViewItem.setOpenCamera(openCamera);
        mLocalViewItem.setRealUid(realUid);

        removeViewFromParent(surfaceView);
        // 本地视图显示
        fl_local.addView(surfaceView);
        // 更新名称
        mLocalViewItem.updateName();
    }

    private int dp2px(float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }


    private void notifyTitleTips() {
        // mTvTitleTips.setText(String.format(getString(R.string.tip_video_calling), mStreams.size() + 1));
        mTvTitleTips.setText(getString(R.string.tip_video_calling));
    }

    private final Map<Integer, AgoraStreamItem> mStreams = new ConcurrentHashMap<>();

    private void createZuoXiSurfaceView(Integer uid) {
        if (!mStreams.containsKey(uid)) {
            TextureView surfaceView = mAgoraRtcEngine.createTextureView();
            //surfaceView.setZOrderMediaOverlay(true);
            AgoraStreamItem item = new AgoraStreamItem();
            item.setSurfaceView(surfaceView);
            item.setUid(uid);
            item.setRealUid(uid);
            mStreams.put(uid, item);
        } else {
            AgoraStreamItem item = mStreams.get(uid);
            item.setUid(uid);
            item.setRealUid(uid);
        }
    }

    private void removeViewFromParent(View view) {
        if (view == null) {
            return;
        }
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(view);
        }
    }

    class HeadsetReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 插入和拔出耳机会触发广播
            if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
                // 1 if headset has a microphone, 0 otherwise
                int state = intent.getIntExtra("state", 0);
                if (state == 1) {
                    // 耳机已插入
                    closeSpeakerOn();
                } else if (state == 0) {
                    // 耳机已拔出
                    openSpeakerOn();
                }
            }
        }
    }

    HandlerThread callHandlerThread = new HandlerThread("callHandlerThread");

    {
        callHandlerThread.start();
    }

    Handler mHandler = new Handler(callHandlerThread.getLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CALL_ANSWER:
                    mHandler.removeCallbacks(timeoutHangup);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mRingtone != null) {
                                mRingtone.stop();
                            }
                            openSpeakerOn();
                            mNoAcceptView.setVisibility(View.GONE);
                            mNoAcceptView.stop();

                            mChronometer.setVisibility(View.VISIBLE);
                            mChronometer.setBase(SystemClock.elapsedRealtime());
                            mChronometer.start();

                            // mFixHeightFrameLayout.setDefaultShowHeight(mLltTopView.getHeight() + dp2px(90) + getResources().getDimensionPixelSize(com.hyphenate.helpdesk.R.dimen.bottom_navi_height_62) + dp2px(6));
                            //mFixHeightFrameLayout.setDefaultShowHeight();
                            //mFixHeightFrameLayout.showHalfHeight();
                            mLocalNameC.setVisibility(View.VISIBLE);
                            //notifyAcceptedStateUI();
                            //addAgoraSelfRadioButton(getName(mZuoXiSendRequestObj), 0);
                            joinChannel(mZuoXiSendRequestObj);
                            notifyTitleTips();
                        }
                    });
                    break;
                case MSG_CALL_END:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeDialog();
                            if (mRingtone != null) {
                                mRingtone.stop();
                            }

                            /*if (mSSClient != null) {
                                // mSSClient.stop(getApplication());
                                mSSClient.stop();
                            }*/

                            if (mAgoraRtcEngine != null){
                                mAgoraRtcEngine.leaveChannel();
                            }

                            if (mChronometer != null){
                                mChronometer.stop();
                            }

                            if (mZuoXiSendRequestObj != null){
                                ChatClient.getInstance().callManager().endCall(mZuoXiSendRequestObj.getCallId(), isOnLine);
                            }else {
                                ChatClient.getInstance().callManager().endCall(0, true);
                            }

                            // 挂断
                            finish();
                            mHandler.sendEmptyMessage(MSG_CLEAR);

                        }
                    });
                    break;
                case MSG_CALL_RELEASE_HANDLER:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 拒接
                            if (!mIsActive){
                                ChatClient.getInstance().callManager().endCall(mZuoXiSendRequestObj.getCallId(), false);
                            }else {
                                ChatClient.getInstance().callManager().endCall(0, true);
                            }
                            //stopForegroundService();
                            finish();
                            mHandler.sendEmptyMessage(MSG_CLEAR);
                        }
                    });
                    break;
                case MSG_CLEAR:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            clear();
                        }
                    });
                    break;
                case MSG_UPLOAD_FLAT_FILE_FAIL:
                    // 电子白板，上传文件失败
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeDialog();
                            // 10M
                            showToast("上传文件失败，请查看网络！");
                        }
                    });
                    break;
                case MSG_UPLOAD_FLAT_FILE_SUCCESS:
                    // 电子白板，上传文件成功
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            insertFlatFile(mCloudFile);
                        }
                    });
                    break;
                case MSG_DELAYED_CLOSE_DIALOG:
                    // 获取电子白板roomId，超时时会触发
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mIsStartBoard = false;
                            mIsSend = false;
                            closeDialog();
                            mBottomContainerView.setCustomItemState(getIconIndex(BottomContainerView.ViewIconData.TYPE_ITEM_FLAT), true);
                            mFixHeightFrameLayout.showFullHeight();
                            showToast("创建白板房间失败！请重试。");
                        }
                    });
                    break;
                case MSG_DELAYED_FILE_CONVERTED:
                    // 文档转换
                    String type = mCloudFile.getConvertedTpe();
                    mCloudFile.url = mUrl;
                    AgoraMessage.asyncFileConverted(mUrl, AgoraMessage.getToken(), mZuoXiSendRequestObj.getCallId(), ChatClient.getInstance().tenantId(), type, new ValueCallBack<String>() {
                        @Override
                        public void onSuccess(String value) {
                            Log.e("ttttttttt", "asyncFileConverted = " + value);
                            if (!isOnLine) {
                                return;
                            }

                            /*{
                                "status": "OK",
                                    "entity": {
                                "uuid": "9ed47370c13a11ec94c0312d35e72631",
                                        "token": "NETLESSTASK_YWs9ZUktd2RsQmk3eENrU3M3VCZleHBpcmVBdD0xNjUwNTI1NDA2MDQ0Jm5vbmNlPTEzY2Y5M2JlLTRlMTctNGFmMy1iYjU2LWRkYTY3YzhmZmRiMCZyb2xlPTImc2lnPTA4OTI4MThlYTI1N2NkZjRlZDM1ZWVkMmNiZjI4OWNkY2RiZmQ2MDFkY2FlMzBjNDdjNTNlYTA2ZWU0YmVjMzUmdXVpZD05ZWQ0NzM3MGMxM2ExMWVjOTRjMDMxMmQzNWU3MjYzMQ",
                                        "type": "static",
                                        "status": "Waiting"
                            }
                            }*/

                            // 设置url
                            try {
                                JSONObject object = new JSONObject(value);
                                if (object.isNull("status")) {
                                    return;
                                }
                                if (!"ok".equalsIgnoreCase(object.getString("status"))) {
                                    return;
                                }

                                if (!object.isNull("entity")) {
                                    JSONObject entity = object.getJSONObject("entity");
                                    if (!entity.isNull("uuid")) {
                                        mCloudFile.taskUUID = entity.getString("uuid");
                                    }

                                    if (!entity.isNull("token")) {
                                        mCloudFile.taskToken = entity.getString("token");
                                    }

                                    if (!entity.isNull("type")) {
                                        mCloudFile.typeString = entity.getString("type");
                                    }

                                    /*if (!entity.isNull("status")){
                                        String status = entity.getString("status");
                                    }*/
                                }
                                //test(mCloudFile.url);
                                Message obtain = Message.obtain();
                                obtain.what = MSG_UPLOAD_FLAT_FILE_SUCCESS;
                                mHandler.sendMessage(obtain);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(int error, String errorMsg) {
                            Log.e("ttttttttt", "asyncFileConverted error = " + error);
                            Log.e("ttttttttt", "asyncFileConverted errorMsg = " + errorMsg);
                            mHandler.sendEmptyMessage(MSG_UPLOAD_FLAT_FILE_FAIL);
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    };

    // 获取自己名字
    private String getName(ZuoXiSendRequestObj obj) {
        return TextUtils.isEmpty(obj.getTrueName()) || "null".equals(obj.getTrueName()) ? obj.getNiceName() : obj.getTrueName();
    }

    // 获取三方名字
    private String getThreeName(ZuoXiSendRequestObj obj) {
        return TextUtils.isEmpty(obj.getThreeTrueName()) || "null".equals(obj.getThreeTrueName()) ? obj.getThreeNiceName() : obj.getThreeTrueName();
    }


    Runnable timeoutHangup = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(MSG_CALL_RELEASE_HANDLER);
        }
    };

    Runnable timeoutDialog = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(MSG_DELAYED_CLOSE_DIALOG);
        }
    };

    private boolean mIsClick;

    @Override
    public void onBackPressed() {
        if (!isOnLine) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "正在通话中...", Toast.LENGTH_LONG).show();
        }
    }


    private void openSpeakerOn() {
        try {
            if (!mAudioManager.isSpeakerphoneOn()) {
                mAudioManager.setSpeakerphoneOn(true);
            }
            mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeSpeakerOn() {
        try {
            if (mAudioManager != null) {
                if (mAudioManager.isSpeakerphoneOn()) {
                    mAudioManager.setSpeakerphoneOn(false);
                }
                mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 座席端主动挂断视频回调函数
    @Override
    public void zuoXiToBreakOff() {
        // 先检测房间里是否还有人，如果没有人直接退出
        if (mUids.size() >= 1) {
            return;
        }
        Log.e("rrrrrrrr", "zuoXiToBreakOff");
        mHandler.sendEmptyMessage(MSG_CALL_END);
    }


    private final Map<Integer, ZuoXiSendRequestObj> mZuoXis = new ConcurrentHashMap<>();

    // 座席端邀请三方人员加入房间回调函数
    @Override
    public void zuoXiSendThreeUserRequest(com.hyphenate.chat.Message message, ZuoXiSendRequestObj obj) {
        // 访客端添加第三方人员进入视频

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 检测三方视频是否已经加入
                int threeUid = obj.getThreeUid();
                mZuoXis.put(threeUid, obj);
                if (mUids.containsKey(threeUid)) {
                    // 执行加入
                    createZuoXiSurfaceView(threeUid);
                    addAgoraRadioButton(TextUtils.isEmpty(obj.getThreeTrueName()) || "null".equals(obj.getTrueName()) ? obj.getThreeNiceName() : obj.getThreeTrueName(), threeUid);
                } else {
                    mUids.put(threeUid, threeUid);
                }

            }
        });

    }

    // 创建白板roomId回调函数
    private boolean mIsSend;

    @Override
    public void createFlatRoom(com.hyphenate.chat.Message message, ZuoXiSendRequestObj obj) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mIsSend) {
                    return;
                }
                mIsSend = true;
                mIsStartBoard = true;
                createFlatRoom();

                mHandler.removeCallbacks(timeoutDialog);
                closeDialog();
                // 显示电子白板
                showFlat(mFlatRoomItem, sFlatUuid);
                loadFlatData(mFlatRoomItem, obj);
            }
        });
    }

    private void closePopupWindow() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    private void clear() {
        // 释放屏幕分享
        /*if (mSSClient != null) {
            // mSSClient.stop(getApplication());
            mSSClient.stop();
        }*/
        mIsCreate = false;
        mIsStartBoard = false;
        mIsSend = false;
        isSharing = false;
        closePopupWindow();

        mPopupWindow = null;
        mUids.clear();
        AgoraMessage.newAgoraMessage().unRegisterAgoraMessageNotify(getClass().getSimpleName());
        mIsClick = false;
        sendIsOnLineState(false);
        if (mRingtone != null && mRingtone.isPlaying()) {
            mRingtone.stop();
        }
        if (mHandler != null) {
            mHandler.removeCallbacks(timeoutDialog);
            mHandler.removeCallbacks(timeoutHangup);
            mHandler.removeCallbacksAndMessages(null);
            mHandler.removeMessages(MSG_CALL_END);
        }

        if (callHandlerThread != null) {
            callHandlerThread.quit();
        }


        try {
            if (mHeadsetReceiver != null) {
                unregisterReceiver(mHeadsetReceiver);
                mHeadsetReceiver = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        if (mAudioManager != null) {
            mAudioManager.setMode(AudioManager.MODE_NORMAL);
            mAudioManager.setMicrophoneMute(false);
            mAudioManager = null;
        }

        for (AgoraStreamItem item : mStreams.values()) {
            item.onDestroy();
        }

        try {
            if (fastRoom != null){
                fastRoom.destroy();
                fastRoom = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if (mAgoraRtcEngine != null) {
            mAgoraRtcEngine.onDestroy();
        }


        mStreams.clear();
        mZuoXis.clear();
        if (mLocalViewItem != null) {
            mLocalViewItem.onDestroy();
            mLocalViewItem = null;
        }

        if (mIconDatas != null) {
            for (BottomContainerView.ViewIconData data : mIconDatas) {
                data.onDestroy();
            }
            mIconDatas.clear();
            mIconDatas = null;
        }
    }

    volatile boolean isOnLine;

    private void sendIsOnLineState(boolean isOnLine) {
        this.isOnLine = isOnLine;
        Intent intent = new Intent(ChatClient.getInstance().callManager().getIncomingCallBroadcastAction());
        intent.setAction("calling.state");
        intent.putExtra("state", isOnLine);
        sendBroadcast(intent);

    }

    private List<BottomContainerView.ViewIconData> mIconDatas = new ArrayList<>();
    private FunctionIconItem getFunctionIconItem(List<FunctionIconItem> functionIconItems, String name) {
        for (FunctionIconItem iconItem : functionIconItems) {
            if (name.equalsIgnoreCase(iconItem.getGrayName())) {
                return iconItem;
            }
        }

        return null;
    }

    private void addIcon_0() {
        mIconDatas.clear();
        BottomContainerView.ViewIconData voiceData = new BottomContainerView.ViewIconData(
                "\ue6ef", "#3B84F7",
                "\ue6a7", "#ff4400",
                BottomContainerView.ViewIconData.TYPE_ITEM_VOICE);
        voiceData.setState(true);
        mIconDatas.add(voiceData);

        mIconDatas.add(new BottomContainerView.ViewIconData("\ue605", "#ff4400", "\ue605", "#3B84F7", 56, true, BottomContainerView.ViewIconData.TYPE_ITEM_PHONE));

        BottomContainerView.ViewIconData cameData = new BottomContainerView.ViewIconData(
                true,
                "\ue76c", "#3B84F7",
                "\ue640", "#ff4400",
                true,
                BottomContainerView.ViewIconData.TYPE_ITEM_CAME);
        cameData.setState(true);
        mIconDatas.add(cameData);

        mBottomContainerView.addIcons(mIconDatas);
        addBottomContainerViewPressStateListener();
    }

    private void addIcon_1(List<FunctionIconItem> functionIconItems) {
        if (functionIconItems == null) {
            return;
        }
        FunctionIconItem iconItem = functionIconItems.get(0);
        String name = iconItem.getGrayName();
        boolean state = iconItem.isEnable();
        mIconDatas.clear();
        BottomContainerView.ViewIconData voiceData = new BottomContainerView.ViewIconData(
                "\ue6ef", "#3B84F7",
                "\ue6a7", "#ff4400",
                BottomContainerView.ViewIconData.TYPE_ITEM_VOICE);
        voiceData.setState(true);
        mIconDatas.add(voiceData);

        BottomContainerView.ViewIconData cameData = new BottomContainerView.ViewIconData(
                true,
                "\ue76c", "#3B84F7",
                "\ue640", "#ff4400",
                true,
                BottomContainerView.ViewIconData.TYPE_ITEM_CAME);
        cameData.setState(true);
        mIconDatas.add(cameData);

        if ("whiteBoard".equalsIgnoreCase(name)) {
            if (state) {
                // 电子白板
                mIconDatas.add(new BottomContainerView.ViewIconData("\ue6a5", "#3B84F7", "\ue6a5", "#ff4400", true, BottomContainerView.ViewIconData.TYPE_ITEM_FLAT));
            }
        } else {
            if (state) {
                // shareDesktop，屏幕分享
                mIconDatas.add(new BottomContainerView.ViewIconData(true, "\ue6ff", "#3B84F7", "\ue6ff", "#ff4400", false, BottomContainerView.ViewIconData.TYPE_ITEM_SHARE));
            }
        }
        // 控制挂断视频按钮大小
        int size = functionIconItems.size() == 1 ? 46 : 56;
        mIconDatas.add(new BottomContainerView.ViewIconData("\ue605", "#ff4400", "\ue605", "#3B84F7", size, true, BottomContainerView.ViewIconData.TYPE_ITEM_PHONE));
        mBottomContainerView.addIcons(mIconDatas);
        addBottomContainerViewPressStateListener();
    }

    private void addIcon_2(List<FunctionIconItem> functionIconItems) {
        if (functionIconItems == null) {
            return;
        }

        mIconDatas.clear();
        BottomContainerView.ViewIconData voiceData = new BottomContainerView.ViewIconData(
                "\ue6ef", "#3B84F7",
                "\ue6a7", "#ff4400",
                BottomContainerView.ViewIconData.TYPE_ITEM_VOICE);
        voiceData.setState(true);
        mIconDatas.add(voiceData);

        BottomContainerView.ViewIconData cameData = new BottomContainerView.ViewIconData(
                true,
                "\ue76c", "#3B84F7",
                "\ue640", "#ff4400",
                true,
                BottomContainerView.ViewIconData.TYPE_ITEM_CAME);
        cameData.setState(true);
        mIconDatas.add(cameData);

        mIconDatas.add(new BottomContainerView.ViewIconData("\ue605", "#ff4400", "\ue605", "#3B84F7", 56, true, BottomContainerView.ViewIconData.TYPE_ITEM_PHONE));
        FunctionIconItem shareDesktop = getFunctionIconItem(functionIconItems, "shareDesktop");
        if (shareDesktop != null && shareDesktop.isEnable()) {
            // 分享
            mIconDatas.add(new BottomContainerView.ViewIconData(true, "\ue6ff", "#3B84F7", "\ue6ff", "#ff4400", false, BottomContainerView.ViewIconData.TYPE_ITEM_SHARE));
        }
        FunctionIconItem whiteBoard = getFunctionIconItem(functionIconItems, "whiteBoard");
        if (whiteBoard != null && whiteBoard.isEnable()) {
            // 白板
            mIconDatas.add(new BottomContainerView.ViewIconData("\ue6a5", "#3B84F7", "\ue6a5", "#ff4400", true, BottomContainerView.ViewIconData.TYPE_ITEM_FLAT));
        }
        mBottomContainerView.addIcons(mIconDatas);
        addBottomContainerViewPressStateListener();
    }

    private void addBottomContainerViewPressStateListener() {

        mBottomContainerView.setOnBottomContainerViewPressStateListener(new BottomContainerView.OnViewPressStateListener() {

            @Override
            public boolean onPressStateChange(int index, boolean isClick, boolean isCustomState) {
                // 默认 true 开启
                if (BottomContainerView.ViewIconData.TYPE_ITEM_VOICE.equalsIgnoreCase(mIconDatas.get(index).getName())) {
                    // 声音
                    int i = mAgoraRtcEngine.muteLocalAudioStream(!isClick);
                    if (i == 0){
                        mCurrentLocalVoiceIsOpen = isClick;
                        // 找到本地视图
                        updateVoiceIcon(mMyUid, isClick);
                    }

                } else if (BottomContainerView.ViewIconData.TYPE_ITEM_CAME.equalsIgnoreCase(mIconDatas.get(index).getName())) {
                    // 相机
                    // 弹出popup
                    if (mPopupWindow == null) {
                        View popupItem = View.inflate(getApplication(), R.layout.popup_item, null);
                        popupItem.measure(0, 0);
                        // TODO 动态修改状态
                        TextView cameraTextView = popupItem.findViewById(R.id.cameraTextView);
                        IconTextView cameraIcon = popupItem.findViewById(R.id.cameraIcon);

                        cameraTextView.setText(mCurrentLocalCameraIsOpen ? "关闭摄像头" : "开启摄像头");
                        // 默认状态相反
                        BottomContainerView.ViewIconData data = mIconDatas.get(getIconIndex(BottomContainerView.ViewIconData.TYPE_ITEM_CAME));
                        cameraIcon.setText(mCurrentLocalCameraIsOpen ? data.getPressIcon() : data.getDefaultIcon());
                        cameraIcon.setTextColor(mCurrentLocalCameraIsOpen ? Color.parseColor(data.getPressIconColor()) : Color.parseColor(data.getDefaultIconColor()));


                        popupItem.findViewById(R.id.oneFlt).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                closePopupWindow();
                                // 开关摄像头
                                String text = cameraTextView.getText().toString();
                                if ("关闭摄像头".equals(text)) {
                                    int i = mAgoraRtcEngine.muteLocalVideoStream(true);
                                    if (i == 0) {
                                        mCurrentLocalCameraIsOpen = false;
                                        // 找到本地视图
                                        updateCamera(mMyUid, mCurrentLocalCameraIsOpen);
                                        cameraTextView.setText("开启摄像头");
                                        cameraIcon.setText(mIconDatas.get(getIconIndex(BottomContainerView.ViewIconData.TYPE_ITEM_CAME)).getDefaultIcon());
                                        cameraIcon.setTextColor(Color.parseColor(mIconDatas.get(getIconIndex(BottomContainerView.ViewIconData.TYPE_ITEM_CAME)).getDefaultIconColor()));


                                        mBottomContainerView.setCustomItemState(getIconIndex(BottomContainerView.ViewIconData.TYPE_ITEM_CAME), mCurrentLocalCameraIsOpen);
                                        Toast.makeText(getApplicationContext(), "关闭摄像头成功！", Toast.LENGTH_LONG).show();
                                    } else {
                                        mCurrentLocalCameraIsOpen = true;
                                        Toast.makeText(getApplicationContext(), "关闭摄像头失败，请检查网络！", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    int i = mAgoraRtcEngine.muteLocalVideoStream(false);
                                    if (i == 0) {
                                        mCurrentLocalCameraIsOpen = true;
                                        // 找到本地视图
                                        updateCamera(mMyUid, mCurrentLocalCameraIsOpen);
                                        cameraTextView.setText("关闭摄像头");
                                        cameraIcon.setText(mIconDatas.get(getIconIndex(BottomContainerView.ViewIconData.TYPE_ITEM_CAME)).getPressIcon());
                                        cameraIcon.setTextColor(Color.parseColor(mIconDatas.get(getIconIndex(BottomContainerView.ViewIconData.TYPE_ITEM_CAME)).getPressIconColor()));
                                        mBottomContainerView.setCustomItemState(getIconIndex(BottomContainerView.ViewIconData.TYPE_ITEM_CAME), mCurrentLocalCameraIsOpen);
                                        Toast.makeText(getApplicationContext(), "开启摄像头成功！", Toast.LENGTH_LONG).show();
                                    } else {
                                        mCurrentLocalCameraIsOpen = false;
                                        Toast.makeText(getApplicationContext(), "开启摄像头失败，请检查网络！", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
                        popupItem.findViewById(R.id.twoFlt).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                closePopupWindow();
                                // 切换摄像头
                                mAgoraRtcEngine.switchCamera();
                            }
                        });
                        mPopupWindow = new PopupWindow(popupItem, popupItem.getMeasuredWidth(), popupItem.getMeasuredHeight());
                        mPopupWindow.setFocusable(true);
                        mPopupWindow.setOutsideTouchable(true);
                    }
                    if (mIconDatas.size() == 3) {
                        mPopupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.BOTTOM | Gravity.RIGHT, dp2px(6), getResources().getDimensionPixelSize(R.dimen.bottom_navi_height_62) + dp2px(13));
                    } else {
                        mPopupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.BOTTOM | Gravity.LEFT, dp2px(42), getResources().getDimensionPixelSize(R.dimen.bottom_navi_height_62) + dp2px(13));
                    }

                } else if (BottomContainerView.ViewIconData.TYPE_ITEM_PHONE.equalsIgnoreCase(mIconDatas.get(index).getName())) {
                    // 挂断视频
                    mIsClick = true;
                    mHandler.sendEmptyMessage(MSG_CALL_END);
                } else if (BottomContainerView.ViewIconData.TYPE_ITEM_SHARE.equalsIgnoreCase(mIconDatas.get(index).getName())) {
                    // 桌面分享
                    if (mIsStartBoard) {
                        showToast("请先关闭白板！");
                    } else {
                        shareWindow();
                    }

                } else if (BottomContainerView.ViewIconData.TYPE_ITEM_FLAT.equalsIgnoreCase(mIconDatas.get(index).getName())) {
                    if (isSharing) {
                        showToast("请先关闭桌面分享！");
                        return true;
                    }

                    // 主动创建电子白板
                    if (!mIsStartBoard) {
                        mIsStartBoard = true;
                        if (mFixHeightFrameLayout.isFullScreen()){
                            mFixHeightFrameLayout.showHalfHeight();
                        }
                        // 显示加载对话框
                        showDialog(getResources().getString(R.string.flat_loading));
                        mHandler.removeCallbacks(timeoutDialog);
                        mHandler.postDelayed(timeoutDialog, DELAYED_CLOSE_DIALOG);
                        initFlatIconView();

                        mFlatRoomItem.findViewById(R.id.full).setTag("0");
                        mBottomContainerView.setCustomItemState(getIconIndex(BottomContainerView.ViewIconData.TYPE_ITEM_FLAT), false);
                        // 发送请求到服务器，创建电子白板roomId，结果会回调createFlatRoom方法
                        ChatClient.getInstance().callManager().createFlatRoom(mZuoXiSendRequestObj.getCallId());
                    } else {
                        showToast("不能重复开启电子白板！");
                    }
                }
                return true;
            }
        });
    }

    private void initFlatIconView(){
        if (mFlatRoomItem == null) {
            mFlatRoomItem = View.inflate(CallActivity.this, R.layout.flat_room_item, null);
            // 当画中画时，需要隐藏
            // mFlatIconView = mFlatRoomItem.findViewById(R.id.flatIconView);
            // 全屏显示电子白板
            View fullView = mFlatRoomItem.findViewById(R.id.full);
            fullView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIsRunAnim = true;
                    String tag = (String) v.getTag();
                    if ("0".equals(tag)) {
                        // 全屏
                        v.setTag("1");
                        mFixHeightFrameLayout.showFullHeight();
                        showAndHidden(mMembersContainer, false);
                        showAndHidden(mBottomContainerView, false);
                    } else {
                        v.setTag("0");
                        mFixHeightFrameLayout.showHalfHeight();
                        showAndHidden(mMembersContainer, true);
                        showAndHidden(mBottomContainerView, true);
                    }
                }
            });

            // 关闭电子白板
            View exitView = mFlatRoomItem.findViewById(R.id.exit);
            exitView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFixHeightFrameLayout.closeFlat();
                }
            });
        }
    }


    // 被动创建电子白板
    private void createFlatRoom(){
        if (mFixHeightFrameLayout.isFullScreen()){
            mFixHeightFrameLayout.showHalfHeight();
        }
        // 显示加载对话框
        showDialog(getResources().getString(R.string.flat_loading));
        initFlatIconView();
        mFlatRoomItem.findViewById(R.id.full).setTag("0");
        mBottomContainerView.setCustomItemState(getIconIndex(BottomContainerView.ViewIconData.TYPE_ITEM_FLAT), false);
    }

    // 根据导航获数动态获取索引
    private int getIconIndex(String name) {
        for (int i = 0; i < mIconDatas.size(); i++) {
            BottomContainerView.ViewIconData data = mIconDatas.get(i);
            if (data.getName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return 0;
    }

    private void showDialog(String msg) {
        if (mProgressDialog == null) {
            //getResources().getString(R.string.flat_loading)
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();

    }

    private void closeDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    public void getFileTypeByFilePath(CloudFile cloudFile, String path) {
        File file = new File(path);
        String fileName = file.getName();
        String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
        cloudFile.name = fileName;
        if ("png".equals(prefix)) {
            cloudFile.type = "png";
        } else if ("jpg".equals(prefix)) {
            cloudFile.type = "jpg";
        } else if ("mp4".equals(prefix)) {
            cloudFile.type = "mp4";
        } else if ("mp3".equals(prefix)) {
            cloudFile.type = "mp3";
        } else if ("pptx".equals(prefix)) {
            cloudFile.type = "pptx";
        } else if ("ppt".equals(prefix)) {
            cloudFile.type = "ppt";
        } else if ("pdf".equals(prefix)) {
            cloudFile.type = "pdf";
        } else if ("doc".equals(prefix)) {
            cloudFile.type = "doc";
        } else if ("docx".equals(prefix)) {
            cloudFile.type = "docx";
        } else if ("xlsx".equals(prefix)) {
            cloudFile.type = "xlsx";
        } else if ("txt".equals(prefix)) {
            cloudFile.type = "txt";
        } else if ("webp".equals(prefix)) {
            cloudFile.type = "webp";
        } else {
            cloudFile.type = "";
        }
    }

    // https://img2.baidu.com/it/u=1814268193,3619863984&fm=253&fmt=auto&app=138&f=JPEG?w=632&h=500
    // 插入图片
    private void insertImage(CloudFile cloudFile) {
        fastRoom.insertImage(cloudFile.url, cloudFile.width, cloudFile.height);
    }

    // 插入视频
    private void insertVideo(CloudFile cloudFile) {
        fastRoom.insertVideo(cloudFile.url, cloudFile.name);
    }

    // 插入文件
    /*private void insertDocs(CloudFile file) {
        FastInsertDocParams params = new FastInsertDocParams(file.taskUUID, file.taskToken, file.type, file.name);

        fastRoom.insertDocs(params, new FastResult<String>() {
            @Override
            public void onSuccess(String value) {
                Log.e("CloudFilesController", "insert Docs success");
            }

            @Override
            public void onError(Exception exception) {
                showToast("插入文档失败，请查看网络！");
            }
        });
    }*/

    private void insertDocss(CloudFile file) {
        FastInsertDocParams params = new FastInsertDocParams(file.taskUUID, file.taskToken, file.type, file.name);

        fastRoom.insertDocs(params, new FastResult<String>() {
            @Override
            public void onSuccess(String value) {
                Log.e("CloudFilesController", "insert Docs success");
            }

            @Override
            public void onError(Exception exception) {
                showToast("插入文档失败，请查看网络！");
            }
        });
    }

    private boolean isDynamicDoc(String fileType) {
        return "pptx".equals(fileType);
    }

    // 插入文件
    private void insertDocs(CloudFile obj, FastResult<String> result) {
        FileConverter convert = new FileConverter.Builder()
                .setResource("")
                .setTypeString(obj.typeString)
                .setTaskUuid(obj.getTaskUUID())
                .setTaskToken(obj.getTaskToken())
                .setCallId(mZuoXiSendRequestObj.getCallId())
                .setCallback(new ConverterCallbacks() {
                    @Override
                    public void onProgress(Double progress, ConversionInfo convertInfo) {

                    }

                    @Override
                    public void onFinish(ConvertedFiles converted, ConversionInfo convertInfo, FileConverter.QueryInfo queryInfo) {
                        Scene[] scenes = converted.getScenes();
                        com.herewhite.sdk.domain.Scene[] s = new com.herewhite.sdk.domain.Scene[scenes.length];
                        for (int i = 0; i < scenes.length; i++) {
                            Scene sc = scenes[i];
                            com.herewhite.sdk.domain.Scene scene = new com.herewhite.sdk.domain.Scene();
                            scene.setName(sc.getName());
                            PptPage ppt = sc.getPpt();
                            com.herewhite.sdk.domain.PptPage pptPage = new com.herewhite.sdk.domain.PptPage(ppt.getSrc(), ppt.getWidth(), ppt.getHeight(), ppt.getPreview());
                            scene.setPpt(pptPage);
                            s[i] = scene;
                        }
                        WindowAppParam param = WindowAppParam.createSlideApp(generateUniqueDir(converted.getTaskId()), s, obj.name);
                        fastRoom.getRoom().addApp(param, new PromiseResultAdapter<>(result));
                        closeDialog();
                        Log.e("tttttttttttt","成功");
                    }

                    private String generateUniqueDir(String taskUUID) {
                        String uuid = UUID.randomUUID().toString();
                        return String.format("/%s/%s", taskUUID, uuid);
                    }

                    @Override
                    public void onFailure(ConvertException e) {
                        closeDialog();

                    }

                    @Override
                    public void onFailure(int code, String error) {
                        closeDialog();
                    }
                }).build();
        convert.startConvertTask();
    }


    private CloudFile mCloudFile;
    private String mUrl;
    boolean mIsConvertedType;

    private void loadDataToService(CloudFile file) {
        mCloudFile = file;
        AgoraMessage.asyncUploadFile(file.url, AgoraMessage.getToken(), mZuoXiSendRequestObj.getCallId(), ChatClient.getInstance().tenantId(),
                new ValueCallBack<String>() {
                    @Override
                    public void onSuccess(String value) {
                        Log.e("tttttttttt", "onSuccess = " + value);
                        if (!isOnLine) {
                            return;
                        }
                        // 设置url
                        try {
                            JSONObject object = new JSONObject(value);
                            if (object.isNull("status") || object.isNull("entity")) {
                                return;
                            }
                            if (!"ok".equalsIgnoreCase(object.getString("status"))) {
                                return;
                            }

                            if (!object.isNull("entity")) {
                                mUrl = object.getString("entity");
                                mCloudFile.url = mUrl;
                            }

                            if (mIsConvertedType) {
                                Message obtain = Message.obtain();
                                obtain.what = MSG_DELAYED_FILE_CONVERTED;
                                mHandler.sendMessage(obtain);
                                return;
                            }

                            insertFlatFile(mCloudFile);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("tttttttttt", "JSONException = " + e.toString());
                            mHandler.sendEmptyMessage(MSG_UPLOAD_FLAT_FILE_FAIL);
                        }
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        Log.e("tttttttttt", "onError = " + errorMsg);
                        mHandler.sendEmptyMessage(MSG_UPLOAD_FLAT_FILE_FAIL);
                    }
                });
    }

    // 上传成功
    private void insertFlatFile(CloudFile obj) {
        /*String type = obj.type;
        if ("png".equals(type) || "jpg".equals(type)) {
            insertImage(obj);
        } else if ("mp4".equals(type) || "mp3".equals(type)) {
            insertVideo(obj);
        } else if ("pptx".equals(type) || "ppt".equals(type)
                || "pdf".equals(type) || "doc".equals(type)
                || "docx".equals(type) || "xlsx".equals(type)) {
            insertDocs(obj);
        }*/

        if (mClickType == CloudActivity.REQUEST_PICTURE) {
            insertImage(obj);
            closeDialog();
        } else if (mClickType == CloudActivity.REQUEST_FILE) {
            insertDocs(obj, new FastResult<String>() {
                @Override
                public void onSuccess(String value) {
                    closeDialog();
                }

                @Override
                public void onError(Exception exception) {
                    closeDialog();
                }
            });
        } else {
            insertVideo(obj);
            closeDialog();
        }
    }

    private int mClickType;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (CloudActivity.UPLOAD_REQUEST == requestCode) {
            if (data == null) {
                showToast("上传失败，文件路径为空！");
                return;
            }
            // 图片
            //Uri selectedImage = data.getData();
            String path = data.getStringExtra("path");
            if (TextUtils.isEmpty(path)) {
                showToast("上传失败，选择文件路径为空！");
                return;
            }
            if (resultCode == CloudActivity.REQUEST_FILE) {
                mIsConvertedType = true;
                mClickType = CloudActivity.REQUEST_FILE;
            } else if (resultCode == CloudActivity.REQUEST_PICTURE) {
                mIsConvertedType = false;
                mClickType = CloudActivity.REQUEST_PICTURE;
            } else if (resultCode == CloudActivity.REQUEST_VIDEO) {
                mIsConvertedType = false;
                mClickType = CloudActivity.REQUEST_VIDEO;
            } else {
                mIsConvertedType = false;
                mClickType = CloudActivity.REQUEST_VOICE;
            }

            showDialog(getResources().getString(R.string.file_uploading));
            /*String path = UriUtils.getFilePath(this, selectedImage);*/
            Log.e("ttttttttt", "path = " + path);
            CloudFile cloudFile = new CloudFile();
            cloudFile.url = path;
            cloudFile.width = getWidth();
            cloudFile.height = getHeight();
            getFileTypeByFilePath(cloudFile, path);
            loadDataToService(cloudFile);
        }
    }

    private int getWidth() {
        return mFlatRoomItem.getMeasuredWidth() / 2;
    }

    private int getHeight() {
        return mFlatRoomItem.getMeasuredHeight() / 2;
    }


    public boolean requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 12);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

}
