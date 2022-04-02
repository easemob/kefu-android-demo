package com.easemob.helpdeskdemo.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.widget.CallControllers;
import com.easemob.helpdeskdemo.widget.CircleImageView;
import com.easemob.helpdeskdemo.widget.MyChronometer;
import com.hyphenate.agora.AgoraStreamItem;
import com.hyphenate.agora.IAgoraMessageNotify;
import com.hyphenate.agora.IAgoraRtcEngineEventHandler;
import com.hyphenate.agora.RtcStats;
import com.hyphenate.agora.ZuoXiSendRequestObj;
import com.hyphenate.chat.AgoraMessage;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.easeui.agora.AgoraRtcEngine;
import com.hyphenate.helpdesk.easeui.agora.ScreenSharingClient;
import com.hyphenate.helpdesk.easeui.agora.VideoEncoderConfigurations;
import com.jaouan.compoundlayout.CompoundLayout;
import com.jaouan.compoundlayout.RadioLayout;
import com.jaouan.compoundlayout.RadioLayoutGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 04/05/2018
 */

public class CallActivity extends DemoBaseActivity implements IAgoraMessageNotify {

	private static final String TAG = CallActivity.class.getSimpleName();
	private final int MSG_CALL_ANSWER = 2;
	private final int MSG_CALL_END = 3;
	private final int MSG_CALL_END_Back = 6;
	private final int MSG_CALL_RELEASE_HANDLER = 4;
	private final int MSG_CALL_ZUO_XI_SEND = 5;

	private final int MAKE_CALL_TIMEOUT = 60 * 1000;// 未接听，1分钟后超时关闭

	private Map<String, Integer> mMemberViewIds = new HashMap<>();

	private AudioManager mAudioManager;
	private Ringtone mRingtone;
	private HeadsetReceiver mHeadsetReceiver = new HeadsetReceiver();
	private TextView mTvTitleTips;
	private RadioLayoutGroup mMembersContainer;
	private View llAcceptContainer;
	private ImageView mIvAccept;
	private ImageView mIvHangup;
	private ImageView mVideoModeFill;
	private ImageView mVideoModeFit;
	private ImageView mIvHide;
	private View mBottomContainer;
	private boolean isHideControllerState;
	private LayoutInflater mInflater;
	private com.easemob.helpdeskdemo.widget.MyChronometer mChronometer;
	private CallControllers mCallControllers;

	private FrameLayout fl_local;
	private ZuoXiSendRequestObj mZuoXiSendRequestObj;
	private AgoraRtcEngine mAgoraRtcEngine;

	private boolean isSharing = false;
	private ScreenSharingClient mSSClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			finish();
			return;
		}
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
				| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		setContentView(R.layout.activity_call_new);
		fl_local = findViewById(R.id.fl_local);

		mInflater = LayoutInflater.from(this);
		mZuoXiSendRequestObj = getIntent().getParcelableExtra("zuoXiSendRequestObj");

		if (mZuoXiSendRequestObj != null){
			mZuoXis.put(mZuoXiSendRequestObj.getThreeUid(), mZuoXiSendRequestObj);
		}else {
			// 在其它页面调用方法，开启此页面，查看效果 startActivity(new Intent(this, CallActivity.class)
			//                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			// TODO 临时Demo测试，需要指定：appId，token，channel，uid（可以默认给0，内不自动创建）
			// TODO 在Constant类里指定 appId，token，channel
			// TODO 采用此种方式不要点击挂断按钮，会有异常，此方式只是为了方便查看效果
			mZuoXiSendRequestObj = new ZuoXiSendRequestObj();
			mZuoXiSendRequestObj.setAppId(Constant.APP_ID);
			mZuoXiSendRequestObj.setToken(Constant.TOKEN);
			mZuoXiSendRequestObj.setChannel(Constant.CHANNEL);
			mZuoXiSendRequestObj.setThreeUid(Constant.UID);
		}

		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		initViews();
		initListeners();
		Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		mAudioManager.setMode(AudioManager.MODE_RINGTONE);
		mAudioManager.setSpeakerphoneOn(true);
		mRingtone = RingtoneManager.getRingtone(this, ringUri);
		if (mRingtone != null) {
			mRingtone.play();
		}
		mHandler.removeCallbacks(timeoutHangup);
		mHandler.postDelayed(timeoutHangup, MAKE_CALL_TIMEOUT);
		registerReceiver(mHeadsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
		mTvTitleTips.setText(getString(R.string.tip_video_in));
		mStreams.clear();


		AgoraMessage.newAgoraMessage().registerAgoraMessageNotify(getClass().getSimpleName(), this);

		mAgoraRtcEngine = AgoraRtcEngine.builder()
				.build(getApplicationContext(), mZuoXiSendRequestObj.getAppId(), new IAgoraRtcEngineEventHandler() {
					@Override
					public void onUserJoined(int uid, int elapsed) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ZuoXiSendRequestObj obj = mZuoXis.get(uid);
								mUids.put(uid, uid);
								createZuoXiSurfaceView(uid);
								if (obj != null){
									if (!obj.isAddThreeUser()){
										addAgoraRadioButton(obj.getNickName(), uid);
									}else {
										addAgoraRadioButton(getThreeName(obj), uid);
									}
								}else {
									addAgoraRadioButton("", uid);
								}
							}
						});
					}

					@Override
					public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
					}

					@Override
					public void onLeaveChannel(RtcStats stats) {
					}

					@Override
					public void onUserOffline(int uid, int reason) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// 第三方离开通道
								removeRadioButton(String.valueOf(uid));
								mUids.remove(uid);
								mZuoXis.remove(uid);
								if (mUids.size() < 1){
									// 关闭页面
									mHandler.sendEmptyMessage(MSG_CALL_END);
								}
							}
						});

					}
				});

		// 屏幕分享
		mSSClient = ScreenSharingClient.getInstance();
		mSSClient.setListener(mListener);

	}

	/*private String appId = "74855635d3a64920b0c7ee3684f68a9f";
	private String token = "00674855635d3a64920b0c7ee3684f68a9fIAAwgepHlJH86alB4UV1O/vdMZKtgs6S5XNt0Yr7cvMIkBo6pkUAAAAAEAAg4mLWC6VCYgEAAQAKpUJi";
	private String channel = "huanxin";*/

	private final ScreenSharingClient.IStateListener mListener = new ScreenSharingClient.IStateListener() {
		@Override
		public void onError(int error) {
			Log.e(TAG, "Screen share service error happened: " + error);
		}

		@Override
		public void onTokenWillExpire() {
			Log.d(TAG, "Screen share service token will expire");
			mSSClient.renewToken(null); // Replace the token with your valid token
		}

		@Override
		public void onDialogStart() {
			// 权限点击确认

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mAgoraRtcEngine.leaveChannel();
					new Handler(getMainLooper()).postDelayed(new Runnable() {
						@Override
						public void run() {
							joinChannel(mZuoXiSendRequestObj);
						}
					}, 200);
				}
			});

		}

		@Override
		public void onDialogDeniedError(int error) {
			// 权限拒绝
		}
	};

	// 访客端和座席端视频切换
	final private CompoundLayout.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundLayout.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundLayout compoundLayout, boolean isChecked) {
			CircleImageView imageView = (CircleImageView) compoundLayout.findViewById(R.id.iv_avatar);
			Integer uid = (Integer) compoundLayout.getTag();
			if (isChecked){
				if (imageView != null){
					imageView.setBorderWidth(dp2px(2));
				}
				// 点击自己
				if(uid == 0){
					showZiJiAgoraSurfaceView();
				}else {
					// 显示座席端
					showZuoXiAgoraSurfaceView(uid);
				}
			}else{
				if (imageView != null){
					for(AgoraStreamItem item : mStreams.values()){
						if (uid != item.getUid()){
							imageView.setBorderWidth(0);
						}
					}
				}
			}

		}
	};


	private Map<Integer, Integer> mUids = new HashMap<>();

	private AgoraStreamItem createZiJiSurfaceView(Integer uid){
		AgoraStreamItem item;
		if (!mStreams.containsKey(uid)){
			SurfaceView surfaceView = mAgoraRtcEngine.createRendererView();
			item = new AgoraStreamItem();
			item.setSurfaceView(surfaceView);
			item.setUid(uid);
			mStreams.put(uid, item);
		}else {
			item = mStreams.get(uid);
		}
		ViewParent parent = item.getSurfaceView().getParent();
		if (parent instanceof ViewGroup){
			((ViewGroup) parent).removeView(item.getSurfaceView());
		}
		return item;
	}

	private void joinChannel(ZuoXiSendRequestObj obj) {
		if (fl_local.getChildCount() > 0) {
			fl_local.removeAllViews();
		}
		SurfaceView surfaceView = createZiJiSurfaceView(0).getSurfaceView();
		fl_local.addView(surfaceView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		mAgoraRtcEngine.setupLocalVideo(surfaceView, AgoraRtcEngine.RENDER_MODE_HIDDEN, obj.getUid());
		mAgoraRtcEngine.joinChannel(obj.getToken(), obj.getChannel(), obj.getUid());
	}

	private void initViews(){

		mBottomContainer = findViewById(R.id.bottom_container);
		mTvTitleTips = (TextView) findViewById(R.id.tv_title_tip);
		mMembersContainer = (RadioLayoutGroup) findViewById(R.id.rlg_container);
		mCallControllers = (CallControllers) findViewById(R.id.layout_controllers);
		mIvAccept = (ImageView) findViewById(R.id.iv_accept);
		mIvHangup = (ImageView) findViewById(R.id.iv_hangup);
		llAcceptContainer = findViewById(R.id.ll_accpet_container);
		mChronometer = (MyChronometer) findViewById(R.id.chronometer);
		mVideoModeFit = (ImageView) findViewById(R.id.iv_model_fit);
		mVideoModeFill = (ImageView) findViewById(R.id.iv_model_fill);
		mIvHide = (ImageView) findViewById(R.id.iv_hide);
		mIvHide.setVisibility(View.INVISIBLE);
		mVideoModeFit.setVisibility(View.INVISIBLE);
		mCallControllers.setVisibility(View.GONE);
	}

	private void notifyAcceptedStateUI(){
		llAcceptContainer.setVisibility(View.GONE);
		mVideoModeFit.setVisibility(View.VISIBLE);
		mVideoModeFill.setVisibility(View.INVISIBLE);
		mIvHide.setVisibility(View.VISIBLE);
		mCallControllers.setVisibility(View.VISIBLE);
	}



	private void initListeners(){
		// 接通
		mIvAccept.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mIsClick = true;
				sendIsOnLineState(true);
				mHandler.sendEmptyMessage(MSG_CALL_ANSWER);
			}
		});

		// 挂断
		mIvHangup.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				mIsClick = true;
				mHandler.sendEmptyMessage(MSG_CALL_END);
			}
		});

		// 视频窗口大小切换
		mVideoModeFit.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				mVideoModeFit.setVisibility(View.INVISIBLE);
				mVideoModeFill.setVisibility(View.VISIBLE);
				fl_local.setPadding(0, 600, 0, 600);
			}
		});

		// 视频窗口大小切换
		mVideoModeFill.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mVideoModeFill.setVisibility(View.INVISIBLE);
				mVideoModeFit.setVisibility(View.VISIBLE);
				fl_local.setPadding(0, 0, 0, 0);
			}
		});

		// 切换摄像头
		mCallControllers.setSwitchCameraOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mAgoraRtcEngine.switchCamera();
			}
		});

		mCallControllers.setMuteOnCheckedChangeListener(new CallControllers.OnCheckedChangeListener() {
			@Override
			public boolean onCheckedChanged(View buttonView, boolean isChecked) {
				Log.e("onCheckedChanged","onCheckedChanged = "+isChecked);
				mAgoraRtcEngine.muteLocalAudioStream(isChecked);
				/*if (isChecked){
					mAgoraRtcEngine.disableAudio();
				}else{
					mAgoraRtcEngine.enableAudio();
				}*/
				return true;
			}
		});

		// 开启/关闭扬声器播放
		mCallControllers.setSpeakerOnCheckedChangedListener(new CallControllers.OnCheckedChangeListener() {
			@Override
			public boolean onCheckedChanged(View buttonView, boolean isChecked) {
				mAgoraRtcEngine.setEnableSpeakerphone(!isChecked);
				return true;
			}
		});

		mCallControllers.setLocalVideoOnCheckedChangeListener(new CallControllers.OnCheckedChangeListener() {
			@Override
			public boolean onCheckedChanged(View buttonView, boolean isChecked) {
				mAgoraRtcEngine.muteLocalVideoStream(isChecked);
				return true;

			}
		});

		// 桌面分享
		mCallControllers.setSharedWindowOnCheckedChangeListener(new CallControllers.OnCheckedChangeListener() {
			@Override
			public boolean onCheckedChanged(View buttonView, boolean isChecked) {

				// 临时测试分享
				if (!isSharing) {
					mAgoraRtcEngine.shareWindows(mSSClient, getApplication(), mZuoXiSendRequestObj.getAppId(), mZuoXiSendRequestObj.getToken(),
							mZuoXiSendRequestObj.getChannel(), mZuoXiSendRequestObj.getUid(), new VideoEncoderConfigurations(
									getScreenDimensions(),
									VideoEncoderConfigurations.FRAME_RATE.FRAME_RATE_FPS_30,
									VideoEncoderConfigurations.STANDARD_BITRATE,
									VideoEncoderConfigurations.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE));

					// 更新状态
					// screenShare.setText(getResources().getString(R.string.stop));
					isSharing = true;

				} else {
					mSSClient.stop(getApplication());
					// 更新状态
					// screenShare.setText(getResources().getString(R.string.screenshare));
					isSharing = false;
					joinChannel(mZuoXiSendRequestObj);
				}
				return true;
			}
		});


		mIvHide.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isHideControllerState = !isHideControllerState;
				if (isHideControllerState){
					mTvTitleTips.setVisibility(View.GONE);
					mChronometer.setVisibility(View.INVISIBLE);
					mCallControllers.setVisibility(View.GONE);
					mMembersContainer.setVisibility(View.INVISIBLE);
					mBottomContainer.setVisibility(View.INVISIBLE);
					mIvHide.setImageResource(R.drawable.em_icon_call_controller_show);
				}else{
					mTvTitleTips.setVisibility(View.VISIBLE);
					mChronometer.setVisibility(View.VISIBLE);
					mCallControllers.setVisibility(View.VISIBLE);
					mMembersContainer.setVisibility(View.VISIBLE);
					mBottomContainer.setVisibility(View.VISIBLE);
					mIvHide.setImageResource(R.drawable.em_icon_call_controller_hide);
				}
			}
		});
	}


	private VideoEncoderConfigurations.VideoDimensions getScreenDimensions(){
		WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(outMetrics);
		return new VideoEncoderConfigurations.VideoDimensions(outMetrics.widthPixels / 2, outMetrics.heightPixels / 2);
	}



	private int selfRadioButtonId = -1;

	private void addAgoraSelfRadioButton(String niceName, int uid){
		RadioLayout selfRadioBtn = getRadioLayoutView(uid);
		if (selfRadioBtn == null){
			selfRadioBtn = (RadioLayout) mInflater.inflate(R.layout.layout_call_head_item, null);
			selfRadioBtn.setId(getViewIdByMemberName(String.valueOf(uid)));
			selfRadioBtn.setTag(uid);
			selfRadioBtn.setChecked(true);
			CircleImageView imageView = (CircleImageView) selfRadioBtn.findViewById(R.id.iv_avatar);
			imageView.setImageResource(R.drawable.hd_default_avatar);
			imageView.setBorderColor(Color.WHITE);
			imageView.setBorderWidth(dp2px(2));

			TextView ivNick = (TextView) selfRadioBtn.findViewById(R.id.tv_nick);
			ivNick.setText(TextUtils.isEmpty(niceName) ? "" : niceName);
			selfRadioBtn.setOnCheckedChangeListener(mOnCheckedChangeListener);
			ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			lp.leftMargin = lp.topMargin = lp.rightMargin = lp.bottomMargin = dp2px(15);
			mMembersContainer.addView(selfRadioBtn, lp);
		}else {
			CircleImageView imageView = (CircleImageView) selfRadioBtn.findViewById(R.id.iv_avatar);
			imageView.setImageResource(R.drawable.hd_default_avatar);
			imageView.setBorderColor(Color.WHITE);
			imageView.setBorderWidth(dp2px(2));

			TextView ivNick = (TextView) selfRadioBtn.findViewById(R.id.tv_nick);
			ivNick.setText(TextUtils.isEmpty(niceName) ? "" : niceName);
		}
		notifyTitleTips();
	}

	private RadioLayout getRadioLayoutView(int uid){
		if (mMembersContainer != null){
			for (int i = 0; i < mMembersContainer.getChildCount(); i ++){
				RadioLayout childAt = (RadioLayout) mMembersContainer.getChildAt(i);
				Object tag = childAt.getTag();
				if (tag instanceof Integer){
					Integer u = (Integer)tag;
					if (u == uid){
						return childAt;
					}
				}
			}
		}
		return null;
	}

	private void addAgoraRadioButton(String niceName, int uid){

		RadioLayout radioLayoutView = getRadioLayoutView(uid);

		if (radioLayoutView == null){
			radioLayoutView = (RadioLayout) mInflater.inflate(R.layout.layout_call_head_item, null);
			radioLayoutView.setTag(uid);
			radioLayoutView.setId(getViewIdByMemberName(String.valueOf(uid)));
			final CircleImageView imageView = (CircleImageView) radioLayoutView.findViewById(R.id.iv_avatar);
			imageView.setImageResource(R.drawable.hd_default_image);
			imageView.setBorderColor(Color.WHITE);
			// Glide.with(this).load(R.drawable.hd_default_image).transform(new GlideCircleTransform(this)).into(imageView);
			TextView ivNick = (TextView) radioLayoutView.findViewById(R.id.tv_nick);
			ivNick.setText(TextUtils.isEmpty(niceName) ? "" : niceName);
			radioLayoutView.setOnCheckedChangeListener(mOnCheckedChangeListener);
			ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			lp.leftMargin = lp.topMargin = lp.rightMargin = lp.bottomMargin = dp2px(15);
			mMembersContainer.addView(radioLayoutView, lp);

		}else {
			CircleImageView imageView = (CircleImageView) radioLayoutView.findViewById(R.id.iv_avatar);
			imageView.setImageResource(R.drawable.hd_default_image);
			imageView.setBorderColor(Color.WHITE);
			// Glide.with(this).load(R.drawable.hd_default_image).transform(new GlideCircleTransform(this)).into(imageView);
			TextView ivNick = (TextView) radioLayoutView.findViewById(R.id.tv_nick);
			ivNick.setText(TextUtils.isEmpty(niceName) ? "" : niceName);
		}
		notifyTitleTips();
	}

	private int dp2px(float dpValue){
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
	}

	private int getViewIdByMemberName(String memberName){
		if (mMemberViewIds.containsKey(memberName)){
			return mMemberViewIds.get(memberName).intValue();
		}
		int viewId = View.generateViewId();
		mMemberViewIds.put(memberName, viewId);
		return viewId;
	}

	private void removeRadioButton(String memberName){
		int viewId = mMemberViewIds.get(memberName);
		if (viewId <= 0){
			return;
		}
		if (viewId == mMembersContainer.getCheckedRadioLayoutId()){
			mMembersContainer.check(selfRadioButtonId);
		}
		mMembersContainer.removeView(findViewById(viewId));
		notifyTitleTips();
	}


	private void notifyTitleTips(){
		mTvTitleTips.setText(String.format(getString(R.string.tip_video_calling), mMemberViewIds.size()));
	}

	private void createZuoXiSurfaceView(Integer uid){
		if (!mStreams.containsKey(uid)){
			SurfaceView surfaceView = mAgoraRtcEngine.createRendererView();
			surfaceView.setZOrderMediaOverlay(true);
			AgoraStreamItem item = new AgoraStreamItem();
			item.setSurfaceView(surfaceView);
			item.setUid(uid);
			mStreams.put(uid, item);
		}
	}

	private SurfaceView getZuoXiSurfaceView(Integer uid){
		AgoraStreamItem item = mStreams.get(uid);
		ViewParent parent = item.getSurfaceView().getParent();
		if (parent instanceof ViewGroup){
			((ViewGroup) parent).removeView(item.getSurfaceView());
		}
		return item.getSurfaceView();
	}


	private final Map<Integer, AgoraStreamItem> mStreams = new ConcurrentHashMap<>();
	private void showZuoXiAgoraSurfaceView(Integer uid){
		if (fl_local.getChildCount() > 0) {
			fl_local.removeAllViews();
		}
		SurfaceView surfaceView = getZuoXiSurfaceView(uid);
		fl_local.addView(surfaceView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		mAgoraRtcEngine.setupRemoteVideo(surfaceView, AgoraRtcEngine.RENDER_MODE_HIDDEN, uid);
	}

	private void showZiJiAgoraSurfaceView(){
		if (fl_local.getChildCount() > 0) {
			fl_local.removeAllViews();
		}

		SurfaceView surfaceView = createZiJiSurfaceView(0).getSurfaceView();
		fl_local.addView(surfaceView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		mAgoraRtcEngine.setupLocalVideo(surfaceView, AgoraRtcEngine.RENDER_MODE_HIDDEN, mZuoXiSendRequestObj.getUid());
	}

	class HeadsetReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// 插入和拔出耳机会触发广播
			if (Intent.ACTION_HEADSET_PLUG.equals(action)){
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
							if (mRingtone != null){
								mRingtone.stop();
							}
							openSpeakerOn();
							mChronometer.setVisibility(View.VISIBLE);
							mChronometer.setBase(SystemClock.elapsedRealtime());
							mChronometer.start();

							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									notifyAcceptedStateUI();
									addAgoraSelfRadioButton(getName(mZuoXiSendRequestObj), 0);
									joinChannel(mZuoXiSendRequestObj);
								}
							});
						}
					});
					break;
				case MSG_CALL_END:
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (mRingtone != null){
								mRingtone.stop();
							}
							mChronometer.stop();
							ChatClient.getInstance().callManager().endCall(mZuoXiSendRequestObj.getCallId(), isOnLine);
							//stopForegroundService();
							// 挂断
							finish();

						}
					});
					break;
				case MSG_CALL_RELEASE_HANDLER:
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// 拒接
							ChatClient.getInstance().callManager().endCall(mZuoXiSendRequestObj.getCallId(), false);
							//stopForegroundService();
							mHandler.removeCallbacks(timeoutHangup);
							mHandler.removeMessages(MSG_CALL_ANSWER);
							mHandler.removeMessages(MSG_CALL_END);
							callHandlerThread.quit();
						}
					});
					break;
				default:
					break;
			}
		}
	};

	private String getName(ZuoXiSendRequestObj obj){
		return TextUtils.isEmpty(obj.getTrueName()) || "null".equals(obj.getTrueName()) ? obj.getNiceName() : obj.getTrueName();
	}

	private String getThreeName(ZuoXiSendRequestObj obj){
		return TextUtils.isEmpty(obj.getThreeTrueName()) || "null".equals(obj.getThreeTrueName()) ? obj.getThreeNiceName() : obj.getThreeTrueName();
	}


	void releaseHandler(){
		mHandler.sendEmptyMessage(MSG_CALL_RELEASE_HANDLER);
	}

	Runnable timeoutHangup = new Runnable() {
		@Override
		public void run() {
			mHandler.sendEmptyMessage(MSG_CALL_RELEASE_HANDLER);
		}
	};

	private boolean mIsClick;
	@Override
	public void onBackPressed() {
		if (!mIsClick){
			if (isOnLine){
				mHandler.sendEmptyMessage(MSG_CALL_END);
			}else {
				mHandler.sendEmptyMessage(MSG_CALL_RELEASE_HANDLER);
			}
		}
		finish();
		super.onBackPressed();
	}


	private void openSpeakerOn(){
		try{
			if (!mAudioManager.isSpeakerphoneOn()){
				mAudioManager.setSpeakerphoneOn(true);
			}
			mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private void closeSpeakerOn(){
		try {
			if (mAudioManager != null){
				if (mAudioManager.isSpeakerphoneOn()){
					mAudioManager.setSpeakerphoneOn(false);
				}
				mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
			}
		}catch (Exception e){e.printStackTrace();}
	}


	@Override
	public void zuoXiToBreakOff() {
		// 先检测房间里是否还有人，如果没有人直接退出
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mUids.size() >= 1){
					return;
				}
				mHandler.sendEmptyMessage(MSG_CALL_END);
			}
		});
	}


	private final Map<Integer, ZuoXiSendRequestObj> mZuoXis = new ConcurrentHashMap<>();
	@Override
	public void zuoXiSendThreeUserRequest(ZuoXiSendRequestObj obj) {
		// 访客端添加第三方人员进入视频

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// 检测三方视频是否已经加入
				int threeUid = obj.getThreeUid();
				mZuoXis.put(threeUid, obj);
				if (mUids.containsKey(threeUid)){
					// 执行加入
					createZuoXiSurfaceView(threeUid);
					addAgoraRadioButton(TextUtils.isEmpty(obj.getThreeTrueName()) || "null".equals(obj.getTrueName()) ? obj.getThreeNiceName() : obj.getThreeTrueName(), threeUid);
				}else {
					mUids.put(threeUid, threeUid);
				}

			}
		});

	}

	/*@Override
	public void zuoXiSendRequest(ZuoXiSendRequestObj obj) {
		for (ZuoXiSendRequestObj obs : mZuoXis.values()){
			if (obs.getUid() == obj.getUid()){
				return;
			}
		}
		mZuoXis.put(obj.getUid(), obj);
	}*/
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mUids.clear();

		mIsClick = false;
		sendIsOnLineState(false);
		if (mRingtone != null && mRingtone.isPlaying()){
			mRingtone.stop();
		}
		if (mHandler != null){
			mHandler.removeCallbacks(timeoutHangup);
			mHandler.removeCallbacksAndMessages(null);
		}

		unregisterReceiver(mHeadsetReceiver);
		mAudioManager.setMode(AudioManager.MODE_NORMAL);
		mAudioManager.setMicrophoneMute(false);
		releaseHandler();


		AgoraMessage.newAgoraMessage().unRegisterAgoraMessageNotify(getClass().getSimpleName());

		for (AgoraStreamItem item : mStreams.values()){
			item.onDestroy();
		}

		// 释放屏幕分享
		if (isSharing && mSSClient != null) {
			mSSClient.stop(getApplication());
		}

		if (mAgoraRtcEngine != null){
			mAgoraRtcEngine.onDestroy();
		}


		mStreams.clear();
		mZuoXis.clear();
	}

	boolean isOnLine;
	private void sendIsOnLineState(boolean isOnLine){
		this.isOnLine = isOnLine;
		Intent intent = new Intent(ChatClient.getInstance().callManager().getIncomingCallBroadcastAction());
		intent.setAction("calling.state");
		intent.putExtra("state", isOnLine);
		sendBroadcast(intent);
	}
}
