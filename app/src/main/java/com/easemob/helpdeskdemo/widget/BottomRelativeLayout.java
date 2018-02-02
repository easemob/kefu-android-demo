package com.easemob.helpdeskdemo.widget;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easemob.helpdeskdemo.R;


/**
 * Created by liyuzhao on 26/07/2017.
 */

public class BottomRelativeLayout extends LinearLayout {

	private ImageButton ivAnswer;
	private ImageButton ivHangUp;
	private FrameLayout bgLayout;
	private LinearLayout titleContainer;
	private LinearLayout bottomContainer;
	private LinearLayout llControllers;

	private View.OnClickListener answerOnClickListener;
	private View.OnClickListener hangUpOnClickListener;
	private View.OnClickListener switchCameraOnClickListener;

	private OnCheckedChangeListener muteOnClickListener;
	private OnCheckedChangeListener speakerOnClickListener;
	private OnCheckedChangeListener localVideoOnClickListener;
	private OnCheckedChangeListener sharedWindowOnClickListener;


	private ImageView ivSwitchCamara;
	private ImageView ivMute;
	private ImageView ivSpeaker;
	private ImageView ivLocalVideo;
	private ImageView ivShareWindow;

	private boolean isMuteState;
	private boolean isHandsfreeState;
	private boolean isLocalVideoOffState;
	private boolean isSharingWindow;

	private TextView tvAgentNick;
	private TextView tvCallState;
	private MyChronometer chronometer;



	public BottomRelativeLayout(Context context) {
		this(context, null);
	}

	public BottomRelativeLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BottomRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
//		setWillNotDraw(false);
		initView(context);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		titleContainer = (LinearLayout) findViewById(R.id.title_container);
		titleContainer.setVisibility(View.VISIBLE);

		bottomContainer = (LinearLayout) findViewById(R.id.bottom_container);
		bottomContainer.setVisibility(View.VISIBLE);

		llControllers = (LinearLayout) findViewById(R.id.ll_controllers);
		llControllers.setVisibility(View.INVISIBLE);

		bgLayout = (FrameLayout) findViewById(R.id.bg_layout);
		bgLayout.setVisibility(View.VISIBLE);
		ivAnswer = (ImageButton) findViewById(R.id.iv_answer);
		ivHangUp = (ImageButton) findViewById(R.id.iv_hangup);
		ivAnswer.setVisibility(View.VISIBLE);

		ivSwitchCamara = (ImageView) findViewById(R.id.iv_switch_camera);
		ivMute = (ImageView) findViewById(R.id.iv_mute);
		ivSpeaker = (ImageView) findViewById(R.id.iv_speaker);
		ivLocalVideo = (ImageView) findViewById(R.id.iv_localvideo);
		ivShareWindow = (ImageView) findViewById(R.id.iv_sharewindow);

		tvAgentNick = (TextView) findViewById(R.id.tv_agent_nick);
		tvCallState = (TextView) findViewById(R.id.tv_call_state);
		chronometer = (MyChronometer) findViewById(R.id.chronometer);

		ivAnswer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ivAnswer.setVisibility(View.GONE);
				if (answerOnClickListener != null){
					answerOnClickListener.onClick(v);
				}
			}
		});
		ivHangUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (hangUpOnClickListener != null){
					hangUpOnClickListener.onClick(v);
				}
			}
		});
		ivSwitchCamara.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (switchCameraOnClickListener != null){
					switchCameraOnClickListener.onClick(v);
				}
			}
		});
		ivMute.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isMuteState = !isMuteState;
				if (isMuteState){
					ivMute.setImageResource(R.drawable.em_icon_mute_on);
				} else {
					ivMute.setImageResource(R.drawable.em_icon_mute_normal);
				}
				if (muteOnClickListener != null){
					muteOnClickListener.onCheckedChanged(v, isMuteState);
				}
			}
		});
		ivSpeaker.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isHandsfreeState = !isHandsfreeState;
				if (isHandsfreeState){
					ivSpeaker.setImageResource(R.drawable.em_icon_speaker_off);
				}else{
					ivSpeaker.setImageResource(R.drawable.em_icon_speaker_on);
				}
				if (speakerOnClickListener != null){
					speakerOnClickListener.onCheckedChanged(v, isHandsfreeState);
				}
			}
		});
		ivLocalVideo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isLocalVideoOffState = !isLocalVideoOffState;
				if (isLocalVideoOffState) {
					ivLocalVideo.setImageResource(R.drawable.em_icon_local_video_off);
				} else {
					ivLocalVideo.setImageResource(R.drawable.em_icon_local_video_on);
				}
				if (localVideoOnClickListener != null){
					localVideoOnClickListener.onCheckedChanged(v, isLocalVideoOffState);
				}
			}
		});

		ivShareWindow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isSharingWindow = !isSharingWindow;
				if (isSharingWindow){
					ivShareWindow.setImageResource(R.drawable.em_icon_local_recard_off);
				}else{
					ivShareWindow.setImageResource(R.drawable.em_icon_local_recard_on);
				}
				if (sharedWindowOnClickListener != null){
					sharedWindowOnClickListener.onCheckedChanged(v, isSharingWindow);
				}
			}
		});

	}

	public void setMaxScreenState(boolean enable){
		if (enable){
			bottomContainer.setVisibility(View.INVISIBLE);
			titleContainer.setVisibility(View.GONE);
			bgLayout.setVisibility(View.GONE);

		}else{
			bottomContainer.setVisibility(View.VISIBLE);
			titleContainer.setVisibility(View.VISIBLE);
			bgLayout.setVisibility(View.VISIBLE);
		}
	}


	private void initView(Context context){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		assert inflater != null;
		inflater.inflate(R.layout.call_bottom_layout, this);
	}

	public void setAnswerOnClickListener(View.OnClickListener listener){
		answerOnClickListener = listener;
	}

	public void setHangUpOnClickListener(View.OnClickListener listener){
		hangUpOnClickListener = listener;
	}

	public void setSwitchCameraOnClickListener(View.OnClickListener listener){
		switchCameraOnClickListener = listener;
	}

	public void setMuteOnCheckedChangeListener(OnCheckedChangeListener listener){
		muteOnClickListener = listener;
	}

	public void setSpeakerOnCheckedChangeListener(OnCheckedChangeListener listener){
		speakerOnClickListener = listener;
	}

	public void setLocalVideoOnCheckedChangeListener(OnCheckedChangeListener listener){
		localVideoOnClickListener = listener;
	}

	public void setSharedWindowOnClickListener(OnCheckedChangeListener listener){
		this.sharedWindowOnClickListener = listener;
	}

	public void setCallStateText(CharSequence text){
		tvCallState.setText(text);
	}

	public void setAgentNick(CharSequence text){
		tvAgentNick.setText(text);
	}

	public void setControllerButtonsVisibile(boolean enable){
		llControllers.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
	}

	public void startChronometer(){
		chronometer.setBase(SystemClock.elapsedRealtime());
		chronometer.start();
	}

	public void stopChronometer(){
		chronometer.stop();
	}

	public static interface OnCheckedChangeListener {
		/**
		 * Called when the checked state of a View has changed.
		 *
		 * @param buttonView The compound button view whose state has changed.
		 * @param isChecked  The new checked state of buttonView.
		 */
		void onCheckedChanged(View buttonView, boolean isChecked);
	}


}
