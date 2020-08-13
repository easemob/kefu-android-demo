package com.easemob.helpdeskdemo.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.helpdeskdemo.Preferences;
import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.widget.CallControllers;
import com.easemob.helpdeskdemo.widget.CircleImageView;
import com.easemob.helpdeskdemo.widget.MyChronometer;
import com.hyphenate.chat.CallManager;
import com.hyphenate.chat.CallSurfaceView;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.MediaStream;
import com.hyphenate.helpdesk.easeui.widget.ToastHelper;
import com.hyphenate.helpdesk.util.Log;
import com.hyphenate.util.EMLog;
import com.jaouan.compoundlayout.CompoundLayout;
import com.jaouan.compoundlayout.RadioLayout;
import com.jaouan.compoundlayout.RadioLayoutGroup;
import com.superrtc.mediamanager.EMediaEntities;
import com.superrtc.mediamanager.ScreenCaptureManager;
import com.superrtc.sdk.VideoView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 04/05/2018
 */

public class CallActivity extends DemoBaseActivity implements CallManager.CallManagerDelegate {

	private static final String TAG = "call_activity";
	private final int MSG_CALL_ANSWER = 2;
	private final int MSG_CALL_END = 3;
	private final int MSG_CALL_RELEASE_HANDLER = 4;

	private final int MAKE_CALL_TIMEOUT = 60 * 1000;// 未接听，1分钟后超时关闭

	private Map<String, List<StreamItem>> mStreamItemMaps = new HashMap<>();
	private Map<String, Integer> mMemberViewIds = new HashMap<>();

	private AudioManager mAudioManager;
	private Ringtone mRingtone;
	private HeadsetReceiver mHeadsetReceiver = new HeadsetReceiver();
	private CallSurfaceView mCurrentSurfaceView;
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
	private String mSelectedMemberName;
	private LayoutInflater mInflater;
	private com.easemob.helpdeskdemo.widget.MyChronometer mChronometer;
	private CallControllers mCallControllers;


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
		mInflater = LayoutInflater.from(this);
		String fromUsername = getIntent().getStringExtra("username");
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		initViews();
		initListeners();
		ChatClient.getInstance().callManager().addDelegate(this);
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
	}


	private void initViews(){
		mCurrentSurfaceView = (CallSurfaceView) findViewById(R.id.call_surfaceview);
		mBottomContainer = findViewById(R.id.bottom_container);
		mCurrentSurfaceView.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);
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
		mIvAccept.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mHandler.sendEmptyMessage(MSG_CALL_ANSWER);
			}
		});
		mIvHangup.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				mHandler.sendEmptyMessage(MSG_CALL_END);
			}
		});
		mVideoModeFit.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				mVideoModeFit.setVisibility(View.INVISIBLE);
				mVideoModeFill.setVisibility(View.VISIBLE);
				mCurrentSurfaceView.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFit);
			}
		});

		mVideoModeFill.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mVideoModeFill.setVisibility(View.INVISIBLE);
				mVideoModeFit.setVisibility(View.VISIBLE);
				mCurrentSurfaceView.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);
			}
		});

		mCallControllers.setSwitchCameraOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ChatClient.getInstance().callManager().switchCamera();
			}
		});
		mCallControllers.setMuteOnCheckedChangeListener(new CallControllers.OnCheckedChangeListener() {
			@Override
			public boolean onCheckedChanged(View buttonView, boolean isChecked) {
				if (isChecked){
					ChatClient.getInstance().callManager().pauseVoice();
				}else{
					ChatClient.getInstance().callManager().resumeVoice();
				}
				return true;
			}
		});

		mCallControllers.setSpeakerOnCheckedChangedListener(new CallControllers.OnCheckedChangeListener() {
			@Override
			public boolean onCheckedChanged(View buttonView, boolean isChecked) {
				if (isChecked){
					closeSpeakerOn();
				}else{
					openSpeakerOn();
				}
				return true;
			}
		});

		mCallControllers.setLocalVideoOnCheckedChangeListener(new CallControllers.OnCheckedChangeListener() {
			@Override
			public boolean onCheckedChanged(View buttonView, boolean isChecked) {
				if (isChecked){
					ChatClient.getInstance().callManager().pauseVideo();
				}else{
					ChatClient.getInstance().callManager().resumeVideo();
				}
				return true;
			}
		});

		mCallControllers.setSharedWindowOnCheckedChangeListener(new CallControllers.OnCheckedChangeListener() {
			@Override
			public boolean onCheckedChanged(View buttonView, boolean isChecked) {
				if (isChecked){
					ChatClient.getInstance().callManager().publishWindow(CallActivity.this, null);
				}else{
					ChatClient.getInstance().callManager().unPublishWindow(null);
					stopForegroundService();
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

	private void info(String msg){
		Log.d(TAG, " " + msg);
	}


	private String getSelfNick() {
		String nickName = Preferences.getInstance().getNickName();
		if (TextUtils.isEmpty(nickName)) {
			nickName = ChatClient.getInstance().currentUserName();
		}
		return nickName;
	}

	@Override
	public void onAddStream(final MediaStream stream) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				streamIn(stream);
			}
		});

	}

	@Override
	public void onRemoveStream(final MediaStream stream) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				streamOut(stream);
			}
		});
	}

	@Override
	public void onUpdateStream(MediaStream stream) {

	}

	private void streamIn(MediaStream stream){
		List<StreamItem> streamItemList;
		if (!mStreamItemMaps.containsKey(stream.memberName)){
			streamItemList = new ArrayList<>();
		}else{
			streamItemList = mStreamItemMaps.get(stream.memberName);
		}
		final StreamItem streamItem = new StreamItem();
		streamItem.stream = stream;
		streamItem.nickName = getNickName(stream.memberName);
		streamItemList.add(streamItem);
		if (!mStreamItemMaps.containsKey(stream.memberName)){
			addRadioButton(streamItem.stream.memberName, streamItem.nickName);
		}
		mStreamItemMaps.put(stream.memberName, streamItemList);
//		if (streamItemList.size() > 1) {
//			ChatClient.getInstance().callManager().updateSubscribe(streamItemList.get(0).stream.streamId, null, null);
//		} else {
//
//		}
		ChatClient.getInstance().callManager().subscribe(streamItem.stream, null, null);
		if (mSelectedMemberName != null && mSelectedMemberName.equals(stream.memberName)) {
			if (!streamItemList.isEmpty()) {
				setStreamToSurfaceView(streamItemList.get(streamItemList.size() - 1));
			} else {
				setStreamToSurfaceView(null);
			}
		}
	}

	private void streamOut(MediaStream stream){
		if (!mStreamItemMaps.containsKey(stream.memberName)){
			return;
		}
		List<StreamItem> streamItemList = mStreamItemMaps.get(stream.memberName);
		if (streamItemList == null || streamItemList.isEmpty()){
			mStreamItemMaps.remove(stream.memberName);
			return;
		}
		int index = -1;
		for (int i = streamItemList.size() - 1; i >=0; i--){
			StreamItem item = streamItemList.get(i);
			if (item.stream.streamId.equals(stream.streamId)){
				index = i;
				break;
			}
		}
		if (index != -1){
			streamItemList.remove(index);
		}

		if (streamItemList.isEmpty()){
			removeRadioButton(stream.memberName);
			mStreamItemMaps.remove(stream.memberName);
			setStreamToSurfaceView(null);
		}else{
			setStreamToSurfaceView(streamItemList.get(streamItemList.size() - 1));
		}
	}

	private int selfRadioButtonId = -1;

	private void addSelfRadioButton(){
		final RadioLayout selfRadioBtn = (RadioLayout) mInflater.inflate(R.layout.layout_call_head_item, null);
		selfRadioButtonId = View.generateViewId();
		selfRadioBtn.setId(selfRadioButtonId);
		selfRadioBtn.setChecked(true);
		final CircleImageView imageView = (CircleImageView) selfRadioBtn.findViewById(R.id.iv_avatar);
		imageView.setImageResource(R.drawable.hd_default_avatar);
		imageView.setBorderColor(Color.WHITE);
		imageView.setBorderWidth(dp2px(2));

		TextView ivNick = (TextView) selfRadioBtn.findViewById(R.id.tv_nick);
		ivNick.setText(getSelfNick());
		selfRadioBtn.setOnCheckedChangeListener(mOnCheckedChangeListener);
		ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.leftMargin = lp.topMargin = lp.rightMargin = lp.bottomMargin = dp2px(15);
		mMembersContainer.addView(selfRadioBtn, lp);
		notifyTitleTips();
	}

	private int dp2px(float dpValue){
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
	}


	private void addRadioButton(String memberName, String nickName){
		final RadioLayout radioBtn = (RadioLayout) mInflater.inflate(R.layout.layout_call_head_item, null);
		radioBtn.setTag(memberName);
		radioBtn.setId(getViewIdByMemberName(memberName));
		final CircleImageView imageView = (CircleImageView) radioBtn.findViewById(R.id.iv_avatar);
		imageView.setImageResource(R.drawable.hd_default_image);
		imageView.setBorderColor(Color.WHITE);

//		Glide.with(this).load(R.drawable.hd_default_image).transform(new GlideCircleTransform(this)).into(imageView);
		TextView ivNick = (TextView) radioBtn.findViewById(R.id.tv_nick);
		ivNick.setText(nickName);
		radioBtn.setOnCheckedChangeListener(mOnCheckedChangeListener);
		ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.leftMargin = lp.topMargin = lp.rightMargin = lp.bottomMargin = dp2px(15);
		mMembersContainer.addView(radioBtn, lp);
		notifyTitleTips();
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
		int viewId = mMemberViewIds.get(memberName).intValue();
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
		mTvTitleTips.setText(String.format(getString(R.string.tip_video_calling), (mMemberViewIds.size() + 1)));
	}



	private CompoundLayout.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundLayout.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundLayout compoundLayout, boolean isChecked) {
			CircleImageView imageView = (CircleImageView) compoundLayout.findViewById(R.id.iv_avatar);
			if (isChecked){
				if (imageView != null){
					imageView.setBorderWidth(dp2px(2));
				}
				mSelectedMemberName = (String) compoundLayout.getTag();
				if (TextUtils.isEmpty(mSelectedMemberName)){
					setStreamToSurfaceView(null);
					return;
				}
				List<StreamItem> list = mStreamItemMaps.get(mSelectedMemberName);
				if (list != null && !list.isEmpty()){
					setStreamToSurfaceView(list.get(list.size() -1));
				}
			}else{
				if (imageView != null){
					imageView.setBorderWidth(0);
				}
			}

		}
	};

	private String lastStreamId = null;


	private void setStreamToSurfaceView(StreamItem item){
		if (item == null){
			if (lastStreamId != null){
				ChatClient.getInstance().callManager().updateSubscribe(lastStreamId, null, null);
				lastStreamId = null;
				ChatClient.getInstance().callManager().setLocalView(mCurrentSurfaceView);
			}
			return;
		}
		if (lastStreamId == null){
			ChatClient.getInstance().callManager().setLocalView(null);
			lastStreamId = item.stream.streamId;
			ChatClient.getInstance().callManager().updateSubscribe(item.stream.streamId, mCurrentSurfaceView, null);
			return;
		}

		if (!lastStreamId.equals(item.stream.streamId)){
			ChatClient.getInstance().callManager().updateSubscribe(lastStreamId, null, null);
			lastStreamId = item.stream.streamId;
			ChatClient.getInstance().callManager().updateSubscribe(item.stream.streamId, mCurrentSurfaceView, null);
		}

	}


	@Override
	public void onCallEnd(int reason, String desc) {
		EMLog.d(TAG, "onCallend-reason:" + reason + "， desc:" + desc);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				finish();
			}
		});

	}


	static class StreamItem {
		MediaStream stream;
		String nickName;
	}


	@Override
	public void onNotice(CallManager.HMediaNoticeCode code, String arg1, String arg2, Object arg3) {
		switch (code){
			case HMediaNoticeOpenCameraFail:
				EMLog.e(TAG, "onNotice:HMediaNoticeOpenCameraFail");
				break;
			case HMediaNoticeOpenMicFail:
				EMLog.e(TAG, "onNotice:HMediaNoticeOpenMicFail");
				break;
			case HMediaNoticeTakeCameraPictureFailed:
				ToastHelper.show(getBaseContext(), "截图失败！" + arg1);
				break;
		}

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
			EMLog.d(TAG, "handleMessage -- what:" + msg.what);
			switch (msg.what) {
				case MSG_CALL_ANSWER:
					EMLog.d(TAG, "MSG_CALL_ANSWER");
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
							ChatClient.getInstance().callManager().acceptCall(getSelfNick(), new com.hyphenate.helpdesk.callback.Callback() {
								@Override
								public void onSuccess() {
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											notifyAcceptedStateUI();
											addSelfRadioButton();
											ChatClient.getInstance().callManager().setLocalView(mCurrentSurfaceView);
										}
									});
								}

								@Override
								public void onError(int code, final String error) {
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											ToastHelper.show(getBaseContext(), "Publish Failed:" + error);
										}
									});
								}

								@Override
								public void onProgress(int progress, String status) {

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
							ChatClient.getInstance().callManager().endCall();
							stopForegroundService();
							finish();
						}
					});
					break;
				case MSG_CALL_RELEASE_HANDLER:
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							info("click hangup");
							ChatClient.getInstance().callManager().endCall();
							stopForegroundService();
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


	void releaseHandler(){
		mHandler.sendEmptyMessage(MSG_CALL_RELEASE_HANDLER);
	}

	Runnable timeoutHangup = new Runnable() {
		@Override
		public void run() {
			mHandler.sendEmptyMessage(MSG_CALL_END);
		}
	};

	@Override
	public void onBackPressed() {
		mHandler.sendEmptyMessage(MSG_CALL_END);
		finish();
		super.onBackPressed();
	}

	/**
	 * 停止服务
	 */
	private void stopForegroundService() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			Intent service = new Intent(this, SRForegroundService.class);
			stopService(service);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ChatClient.getInstance().callManager().removeDelegate(this);
		if (mRingtone != null && mRingtone.isPlaying()){
			mRingtone.stop();
		}
		unregisterReceiver(mHeadsetReceiver);
		mAudioManager.setMode(AudioManager.MODE_NORMAL);
		mAudioManager.setMicrophoneMute(false);
		releaseHandler();
	}

	public String getNickName(String memberName){
		EMediaEntities.EMediaMember mediaMember = ChatClient.getInstance().callManager().getEMediaMember(memberName);
		if (mediaMember != null && !TextUtils.isEmpty(mediaMember.extension)){
			try {
				JSONObject jsonObject = new JSONObject(mediaMember.extension);
				return jsonObject.getString("nickname");
			}catch (Exception ignored){}
		}
		return memberName;
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ScreenCaptureManager.RECORD_REQUEST_CODE) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
					Intent service = new Intent(this, SRForegroundService.class);
					service.putExtra("code", resultCode);
					service.putExtra("data", data);
					startForegroundService(service);
				}else {
					ChatClient.getInstance().callManager().onActivityResult(requestCode, resultCode, data);
				}
			}
		}
	}
}
