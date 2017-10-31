package com.easemob.helpdeskdemo.ui;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.easemob.helpdeskdemo.Preferences;
import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.widget.BottomRelativeLayout;
import com.easemob.helpdeskdemo.widget.CustomVideoContainer;
import com.easemob.helpdeskdemo.widget.CustomVideoView;
import com.hyphenate.chat.CallManager;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.MediaStream;
import com.hyphenate.helpdesk.util.Log;
import com.hyphenate.util.EMLog;
import com.superrtc.mediamanager.EMediaDefines;
import com.superrtc.mediamanager.EMediaEntities;
import com.superrtc.mediamanager.EMediaStream;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VideoCallActivity extends DemoBaseActivity implements CallManager.CallManagerDelegate {

	private static final String TAG = "call";
	protected final int MSG_CALL_ANSWER = 2;
	protected final int MSG_CALL_END = 3;
	protected final int MSG_CALL_RELEASE_HANDLER = 4;
	protected final int MSG_CALL_SWITCH_CAMERA = 5;

//	protected int streamID = -1;

	private View rootLayout;
	private CustomVideoContainer multiVideoView;
	private CustomVideoView localSurfaceView;
	private ImageButton ibMinimize;
	private Map<String, List<StreamItem>> streamItemMaps = new HashMap<>();

	private BottomRelativeLayout bottomRelativeLayout;
	protected AudioManager audioManager;
	protected Ringtone ringtone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null){
			finish();
			return;
		}
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
				| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		setContentView(R.layout.activity_call);
		String fromUsername = getIntent().getStringExtra("username");
		audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		initViews();
		info("conference demo");
		initListeners();

		ChatClient.getInstance().callManager().addDelegate(this);


		Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		audioManager.setMode(AudioManager.MODE_RINGTONE);
		audioManager.setSpeakerphoneOn(true);
		ringtone = RingtoneManager.getRingtone(this, ringUri);
		ringtone.play();

		final int MAKE_CALL_TIMEOUT = 60 * 1000;
		handler.removeCallbacks(timeoutHangup);
		handler.postDelayed(timeoutHangup, MAKE_CALL_TIMEOUT);
	}

	private String getSelfNick(){
		String nickName = Preferences.getInstance().getNickName();
		if (TextUtils.isEmpty(nickName)){
			nickName = ChatClient.getInstance().currentUserName();
		}
		return nickName;
	}


	HandlerThread callHandlerThread = new HandlerThread("callHandlerThread");
	{ callHandlerThread.start(); }

	protected Handler handler = new Handler(callHandlerThread.getLooper()){
		@Override
		public void handleMessage(android.os.Message msg) {
			EMLog.d(TAG, "handleMessage -- enter block -- msg.what: " + msg.what);
			switch (msg.what){
				case MSG_CALL_ANSWER:
					EMLog.d(TAG, "MSG_CALL_ANSWER");
					handler.removeCallbacks(timeoutHangup);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (ringtone != null){
								ringtone.stop();
							}
							openSpeakerOn();
							bottomRelativeLayout.startChronometer();
							bottomRelativeLayout.setCallStateText(getString(R.string.tip_multi_video_calling));
							ChatClient.getInstance().callManager().acceptCall(getSelfNick(), new com.hyphenate.helpdesk.callback.Callback() {
								@Override
								public void onSuccess() {
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											bottomRelativeLayout.setControllerButtonsVisibile(true);
											localSurfaceView = addViewToMultiLayout(null);
											localSurfaceView.setLabel(getSelfNick());
											ChatClient.getInstance().callManager().setLocalView(localSurfaceView.getSurfaceView());
										}
									});
								}

								@Override
								public void onError(int code, final String error) {
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											Toast.makeText(getApplicationContext(), "Publish Failed:" + error, Toast.LENGTH_SHORT).show();
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
							if (ringtone != null){
								ringtone.stop();
							}
							bottomRelativeLayout.stopChronometer();
							streamItemMaps.clear();
							multiVideoView.removeAllVideoViews();
							ChatClient.getInstance().callManager().endCall();
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
							handler.removeCallbacks(timeoutHangup);
							handler.removeMessages(MSG_CALL_ANSWER);
							handler.removeMessages(MSG_CALL_END);
							callHandlerThread.quit();
						}
					});
					break;
				case MSG_CALL_SWITCH_CAMERA:
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							ChatClient.getInstance().callManager().switchCamera();
						}
					});
					break;
				default:
					break;
			}
		}
	};


	void releaseHandler(){
		handler.sendEmptyMessage(MSG_CALL_RELEASE_HANDLER);
	}

	Runnable timeoutHangup = new Runnable() {
		@Override
		public void run() {
			handler.sendEmptyMessage(MSG_CALL_END);
		}
	};


	@Override
	public void onBackPressed() {
		handler.sendEmptyMessage(MSG_CALL_END);
		finish();
		super.onBackPressed();
	}


	private CustomVideoView addViewToMultiLayout(String streamId){
		final CustomVideoView item = new CustomVideoView(this);
		item.setTag(streamId);
		multiVideoView.addView(item);
		return item;
	}


	private void initListeners() {

		bottomRelativeLayout.setAnswerOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				handler.sendEmptyMessage(MSG_CALL_ANSWER);
			}
		});

		bottomRelativeLayout.setHangUpOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				handler.sendEmptyMessage(MSG_CALL_END);
			}
		});

		bottomRelativeLayout.setLocalVideoOnCheckedChangeListener(new BottomRelativeLayout.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(View buttonView, boolean isChecked) {
				if (isChecked){
					ChatClient.getInstance().callManager().pauseVideo();
					localSurfaceView.setSurfaceViewVisible(false);
				}else{
					ChatClient.getInstance().callManager().resumeVideo();
					localSurfaceView.setSurfaceViewVisible(true);
				}
			}
		});

		bottomRelativeLayout.setMuteOnCheckedChangeListener(new BottomRelativeLayout.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(View buttonView, boolean isChecked) {
				if (isChecked) {
					ChatClient.getInstance().callManager().resumeVoice();
				} else {
					ChatClient.getInstance().callManager().pauseVoice();
				}
			}
		});

		bottomRelativeLayout.setSwitchCameraOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				handler.sendEmptyMessage(MSG_CALL_SWITCH_CAMERA);
			}
		});

		bottomRelativeLayout.setSpeakerOnCheckedChangeListener(new BottomRelativeLayout.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(View buttonView, boolean isChecked) {
				if (isChecked) {
					closeSpeakerOn();
				} else {
					openSpeakerOn();
				}
			}
		});

		multiVideoView.setOnMaxVideoChangeListener(new CustomVideoContainer.OnMaxVideoChangeListener() {
			@Override
			public void onChanged(boolean isMax) {
				bottomRelativeLayout.setMaxScreenState(isMax);
				ibMinimize.setVisibility(isMax ? View.VISIBLE : View.GONE);
			}
		});

		ibMinimize.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ibMinimize.setVisibility(View.GONE);
				multiVideoView.minimizeChildView();
				bottomRelativeLayout.setMaxScreenState(false);
			}
		});

	}

	private void initViews() {

		ibMinimize = (ImageButton) findViewById(R.id.ib_minimize);
		ibMinimize.setVisibility(View.GONE);
		multiVideoView = (CustomVideoContainer) findViewById(R.id.multi_view_container);
		bottomRelativeLayout = (BottomRelativeLayout) findViewById(R.id.bottom_relative);
		rootLayout = findViewById(R.id.root_layout);
		bottomRelativeLayout.setAgentNick(ChatClient.getInstance().callManager().getCallNickName());

	}

	private boolean firstChanged = true;

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (firstChanged){
			firstChanged = false;
			ViewGroup.LayoutParams layoutParams = bottomRelativeLayout.getLayoutParams();
			layoutParams.height = rootLayout.getHeight() - multiVideoView.getHeight();
			bottomRelativeLayout.setLayoutParams(layoutParams);
		}
	}


	private void info(String msg) {
		Log.d(TAG, "" + msg);
	}


	@Override
	public void onAddStream(final MediaStream stream) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				List<StreamItem> streamItemList;
				if (!streamItemMaps.containsKey(stream.memberName)){
					streamItemList = new ArrayList<>();
					final StreamItem streamItem = new StreamItem();
					streamItem.stream = stream;
					streamItem.videoView = addViewToMultiLayout(stream.streamId);
					streamItem.videoView.setLabel(getNickName(stream.memberName));
					streamItemList.add(streamItem);
					streamItemMaps.put(stream.memberName, streamItemList);
					ChatClient.getInstance().callManager().subscribe(streamItem.stream, streamItem.videoView.getSurfaceView(), null);
				}else{
					streamItemList = streamItemMaps.get(stream.memberName);
					if (streamItemList.isEmpty()){
						final StreamItem streamItem = new StreamItem();
						streamItem.stream = stream;
						streamItem.videoView = addViewToMultiLayout(stream.streamId);
						streamItem.videoView.setLabel(getNickName(stream.memberName));
						streamItemList.add(streamItem);
						ChatClient.getInstance().callManager().subscribe(streamItem.stream, streamItem.videoView.getSurfaceView(), null);
						streamItemMaps.put(stream.memberName, streamItemList);
					}else if (streamItemList.size() == 1){
						if (stream.streamType == EMediaDefines.EMediaStreamType.EMSTREAM_TYPE_DESKTOP){
							StreamItem item0 = streamItemList.get(0);
							final StreamItem streamItem = new StreamItem();
							streamItem.stream = stream;
							streamItem.videoView = item0.videoView;
							streamItem.videoView.setLabel(getNickName(stream.memberName));
							streamItemList.add(streamItem);
							ChatClient.getInstance().callManager().setRemoteView(item0.stream.streamId, null);
							ChatClient.getInstance().callManager().subscribe(streamItem.stream, streamItem.videoView.getSurfaceView(), null);
							streamItemMaps.put(stream.memberName, streamItemList);
						}

					}

				}
			}
		});
	}

	@Override
	public void onRemoveStream(final MediaStream stream) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (!streamItemMaps.containsKey(stream.memberName)){
					return;
				}
				List<StreamItem> streamItemList = streamItemMaps.get(stream.memberName);
				if (streamItemList.isEmpty()) {
					streamItemMaps.remove(stream.memberName);
				}else if (streamItemList.size() == 1){
					CustomVideoView videoView = streamItemList.get(0).videoView;
					if (videoView != null){
						multiVideoView.removeVideoView(videoView);
					}
					ChatClient.getInstance().callManager().unSubscribe(streamItemList.get(0).stream, null);
					streamItemMaps.remove(stream.memberName);
					ChatClient.getInstance().callManager().setRemoteView(stream.streamId, null);
				}else if (streamItemList.size() == 2){
					StreamItem item0 = streamItemList.get(0);
					StreamItem item1 = streamItemList.get(1);
					if (item0.stream.streamId.equalsIgnoreCase(stream.streamId)){
						item1.videoView = item0.videoView;
						ChatClient.getInstance().callManager().unSubscribe(item0.stream, null);
						ChatClient.getInstance().callManager().setRemoteView(item0.stream.streamId, null);
						streamItemList.remove(item0);
						item1.videoView.setLabel(item1.stream.memberName);
						ChatClient.getInstance().callManager().setRemoteView(item1.stream.streamId, item1.videoView.getSurfaceView());
					}else if (item1.stream.streamId.equalsIgnoreCase(stream.streamId)){
						item0.videoView = item1.videoView;
						ChatClient.getInstance().callManager().unSubscribe(item1.stream, null);
						ChatClient.getInstance().callManager().setRemoteView(item1.stream.streamId, null);
						streamItemList.remove(item1);
						item0.videoView.setLabel(item0.stream.memberName);
						ChatClient.getInstance().callManager().setRemoteView(item0.stream.streamId, item0.videoView.getSurfaceView());
					}
					streamItemMaps.put(stream.memberName, streamItemList);
				}
			}
		});
	}

	@Override
	public void onUpdateStream(MediaStream stream) {
	}

	@Override
	public void onCallEnd(int reason, String desc) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (ringtone != null){
					ringtone.stop();
				}
				bottomRelativeLayout.stopChronometer();
				streamItemMaps.clear();
				multiVideoView.removeAllVideoViews();
				ChatClient.getInstance().callManager().endCall();
				finish();
			}
		});
	}


	public static class StreamItem {
		 CustomVideoView videoView;
		 EMediaStream stream;
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		ChatClient.getInstance().callManager().removeDelegate(this);
		if (ringtone != null && ringtone.isPlaying()){
			ringtone.stop();
		}
		audioManager.setMode(AudioManager.MODE_NORMAL);
		audioManager.setMicrophoneMute(false);
		releaseHandler();

	}


	public String getNickName(String memberName){
		EMediaEntities.EMediaMember mediaMember = ChatClient.getInstance().callManager().getEMediaMember(memberName);
		if (mediaMember != null && !TextUtils.isEmpty(mediaMember.extension)){
			try{
				JSONObject jsonObject = new JSONObject(mediaMember.extension);
				return jsonObject.getString("nickname");
			}catch (Exception ignored){}
		}
		return memberName;
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

}
