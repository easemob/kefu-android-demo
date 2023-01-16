package com.easemob.helpdeskdemo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.easemob.helpdeskdemo.R;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 08/05/2018
 */

public class CallControllers extends LinearLayout {

	private ImageView ivSwitchCamera;
	private ImageView ivMic;
	private ImageView ivSpeaker;
	private ImageView ivLocalVideo;
	private ImageView ivShareWindow;

	private boolean isMuteState;
	private boolean isHandsfreeState;
	private boolean isLocalVideoOffState;
	private boolean isSharingWindow = false;

	private View.OnClickListener switchCameraOnClickListener;

	private OnCheckedChangeListener muteOnCheckedChangeListener;
	private OnCheckedChangeListener speakerOnCheckedChangeListener;
	private OnCheckedChangeListener localVideoOnCheckedChangeListener;
	private OnCheckedChangeListener sharedWindowOnCheckedChangeListener;

	public CallControllers(Context context) {
		this(context, null);
	}

	public CallControllers(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CallControllers(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		LayoutInflater.from(context).inflate(R.layout.layout_call_controller, this);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		ivSwitchCamera = (ImageView) findViewById(R.id.iv_switch_camera);
		ivMic = (ImageView) findViewById(R.id.iv_mic);
		ivSpeaker = (ImageView) findViewById(R.id.iv_speaker);
		ivLocalVideo = (ImageView) findViewById(R.id.iv_localvideo);
		ivShareWindow = (ImageView) findViewById(R.id.iv_sharewindow);


		ivSwitchCamera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (switchCameraOnClickListener != null){
					switchCameraOnClickListener.onClick(v);
				}
			}
		});

		ivMic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (muteOnCheckedChangeListener != null){
					boolean ret = muteOnCheckedChangeListener.onCheckedChanged(v, !isMuteState);
					if (ret){
						isMuteState = !isMuteState;
						if (isMuteState){
							ivMic.setImageResource(R.drawable.em_icon_call_mic_off);
						}else{
							ivMic.setImageResource(R.drawable.em_icon_call_mic_on);
						}
					}
				}
			}
		});

		ivSpeaker.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (speakerOnCheckedChangeListener != null){
					boolean ret = speakerOnCheckedChangeListener.onCheckedChanged(v, !isHandsfreeState);
					if (ret){
						isHandsfreeState = !isHandsfreeState;
						if (isHandsfreeState){
							ivSpeaker.setImageResource(R.drawable.em_icon_call_speaker_off);
						}else{
							ivSpeaker.setImageResource(R.drawable.em_icon_call_speaker_on);
						}
					}
				}
			}
		});
		ivLocalVideo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (localVideoOnCheckedChangeListener != null){
					boolean ret = localVideoOnCheckedChangeListener.onCheckedChanged(v, !isLocalVideoOffState);
					if (ret){
						isLocalVideoOffState = !isLocalVideoOffState;
						if (isLocalVideoOffState){
							ivLocalVideo.setImageResource(R.drawable.em_icon_call_localvideo_off);
						}else{
							ivLocalVideo.setImageResource(R.drawable.em_icon_call_localvideo_on);
						}
					}
				}
			}
		});

		ivShareWindow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sharedWindowOnCheckedChangeListener != null){
					boolean ret = sharedWindowOnCheckedChangeListener.onCheckedChanged(v, !isSharingWindow);
					if (ret){
						isSharingWindow = !isSharingWindow;
						if (isSharingWindow){
							ivShareWindow.setImageResource(R.drawable.em_icon_call_share_desktop_on);
						}else{
							ivShareWindow.setImageResource(R.drawable.em_icon_call_share_desktop_off);
						}
					}
				}
			}
		});
	}

	public void setSwitchCameraOnClickListener(OnClickListener listener){
		switchCameraOnClickListener = listener;
	}

	public void setMuteOnCheckedChangeListener(OnCheckedChangeListener listener){
		muteOnCheckedChangeListener = listener;
	}

	public void setSpeakerOnCheckedChangedListener(OnCheckedChangeListener listener){
		speakerOnCheckedChangeListener = listener;
	}

	public void setLocalVideoOnCheckedChangeListener(OnCheckedChangeListener listener){
		localVideoOnCheckedChangeListener = listener;
	}

	public void setSharedWindowOnCheckedChangeListener(OnCheckedChangeListener listener){
		sharedWindowOnCheckedChangeListener = listener;
	}

	public static interface OnCheckedChangeListener {
		boolean onCheckedChanged(View buttonView, boolean isChecked);
	}


}
