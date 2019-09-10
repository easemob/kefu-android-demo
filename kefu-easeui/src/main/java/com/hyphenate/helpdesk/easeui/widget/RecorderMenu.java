package com.hyphenate.helpdesk.easeui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.easeui.recorder.AudioManager;
import com.hyphenate.helpdesk.easeui.recorder.DialogManager;
import com.hyphenate.util.PathUtil;

import java.lang.ref.WeakReference;

/**
 * Created by tiancruyff on 2017/5/2.
 */

public class RecorderMenu extends RelativeLayout implements AudioManager.AudioStateListener{

	private TextView mRecordText;
	private ImageButton mRecordBtn;
	private AudioManager mAudioManager;
	private WeakHandler mHandler;

	private static int DISTANCE_X_CANCEL = 10;
	private static int DISTANCE_Y_CANCEL = 10;

	private static final int STATE_NORMAL = 1;
	private static final int STATE_RECORDING = 2;
	private static final int STATE_WANT_TO_CANCEL = 3;

	private int mCurState = STATE_NORMAL;

	private static final int MSG_AUDIO_REPARED = 0x110;
	private static final int MSG_VOICE_CHANGE = 0x111;
	private static final int MSG_TOO_SHORT_DISMISS = 0x112;

	private volatile boolean isRecording = false;
    private DialogManager mDialogManager;
	private boolean isHasRecorderPermission = false;
	/*记录时间*/
	private volatile float mTime;

	/*是否触发longClick*/
	private boolean mReady;

	private class WeakHandler extends Handler {
		WeakReference<ImageButton> weakReference;

		public WeakHandler(ImageButton button) {
			this.weakReference = new WeakReference<ImageButton>(button);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ImageButton button = weakReference.get();
			switch (msg.what) {
				case MSG_AUDIO_REPARED:
					if (null != button) {
                        mDialogManager.showRecordingDialog();
						isRecording = true;
                         /*开启线程获取音量,因为获取VoiceLevel是有时间间隔的*/
						new Thread(mGetAudioVoiceLevelRunnable).start();
					}
					break;
				case MSG_VOICE_CHANGE:
					if (null != button) {
						mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(14));
						mDialogManager.updateRecordTime(mTime);
					}
					break;
				case MSG_TOO_SHORT_DISMISS:
					if (null != button) {
						mRecordBtn.setEnabled(true);
						reset();
					}
					break;
				default:
					break;
			}
		}
	}

	Runnable mGetAudioVoiceLevelRunnable = new Runnable() {
		@Override
		public void run() {
			//只要isRecording = true 就不停记录时间(+0.1s)和动态显示音量
			try {
				while (isRecording) {
                    /*开启线程获取音量,因为获取VoiceLevel是有时间间隔的*/
					Thread.sleep(100);
					mTime += 0.1f;
					mHandler.sendEmptyMessage(MSG_VOICE_CHANGE);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	};

	public RecorderMenu(Context context) {
		super(context);
		init(context);
	}

	public RecorderMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public RecorderMenu(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public RecorderMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater.from(context).inflate(R.layout.hd_widget_recorder_menu, this, true);
		mRecordText = (TextView) findViewById(R.id.record_menu_text);
		mRecordBtn = (ImageButton) findViewById(R.id.record_menu_image_btn);
		setMotionEventSplittingEnabled(false);
		String dir = null;
		try{
			dir = PathUtil.getInstance().getVoicePath().getPath();
		}catch (Exception e){
			dir = Environment.getDownloadCacheDirectory().getPath();
		}
		mAudioManager = AudioManager.getInstance(dir);
		mAudioManager.setOnAudioStateListener(this);
		mHandler = new WeakHandler(mRecordBtn);
        mDialogManager = new DialogManager(getContext());
		mRecordBtn.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
				assert vibrator != null;
				vibrator.vibrate(new long[]{5, 9, 5, 9, 5, 9}, -1);
				mReady = true;
				mAudioManager.prepareAudio();
				return false;
			}
		});
		mRecordBtn.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				int x = (int) event.getX();
				int y = (int) event.getY();
				switch (action) {
					case MotionEvent.ACTION_DOWN:
						changeState(STATE_RECORDING);
						break;
					case MotionEvent.ACTION_MOVE:
						if (isRecording) {
							//根据x，y的坐标判断是否想要取消
							if (wanttoCancel(x, y)) {
								changeState(STATE_WANT_TO_CANCEL);
							} else {
								changeState(STATE_RECORDING);
							}
						}
						break;
					case MotionEvent.ACTION_CANCEL:
                        /*Audio结束，释放资源*/
						mAudioManager.cancel();
						if (mHandler.hasMessages(MSG_AUDIO_REPARED)){
							mHandler.removeMessages(MSG_AUDIO_REPARED);
						}
						mReady = false;
						isRecording = false;
						mTime = 0;
						changeState(STATE_NORMAL);
						mDialogManager.dismissDialog();
						break;
					case MotionEvent.ACTION_UP:
						if (!mReady) {
							reset();
							return false;
						} else if (!isRecording || mTime < 0.6) {
							//开始了prepare但是没有成功
							if (mHandler.hasMessages(MSG_AUDIO_REPARED)){
								mHandler.removeMessages(MSG_AUDIO_REPARED);
							}
							/*Dialog显示时间短*/
							mDialogManager.dismissDialog();
							mRecordText.setText(R.string.ease_record_menu_too_short);
							mRecordBtn.setBackgroundResource(R.drawable.hd_record_menu_too_short);
							mRecordBtn.setEnabled(false);
                            /*Audio结束，释放资源*/
							mAudioManager.cancel();
                            /*延迟1s*/
							mHandler.sendEmptyMessageDelayed(MSG_TOO_SHORT_DISMISS, 1000);
							return false;
						} else if (mCurState == STATE_RECORDING) {
                            mDialogManager.dismissDialog();
							//release会保存录音文件，而cancel会删除录音文件
							mAudioManager.release();

							//callbackToActivity
							if (mListener != null) {
								mListener.onFinish(mTime, mAudioManager.getCurrentPath());
							}
						} else if (mCurState == STATE_WANT_TO_CANCEL) {
							//cancel
                            mDialogManager.dismissDialog();
							mAudioManager.cancel();
						}
						reset();
						break;
					default:
						break;
				}

				return false;
			}
		});
	}

	private void reset() {
		//恢复标志位
		isRecording = false;
		mReady = false;
		mTime = 0;
		changeState(STATE_NORMAL);
		mDialogManager.dismissDialog();
	}


	public boolean isRecording(){
		return isRecording;
	}

	private boolean wanttoCancel(int x, int y) {
		WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		assert windowManager != null;
		windowManager.getDefaultDisplay().getMetrics(displayMetrics);
		DISTANCE_X_CANCEL = displayMetrics.widthPixels / 5;
		DISTANCE_Y_CANCEL = displayMetrics.heightPixels / 5;

		return x < -DISTANCE_X_CANCEL || x > DISTANCE_X_CANCEL || y < -DISTANCE_Y_CANCEL || y > DISTANCE_Y_CANCEL;
	}


	@Override
	public void wellPrepared() {
		mHandler.sendEmptyMessage(MSG_AUDIO_REPARED);
	}

	private void changeState(int state) {
		//当前状态与目标状态不同时，changeState
		if (mCurState != state) {
			mCurState = state;
			switch (state) {
				case STATE_NORMAL:
					setBackgroundResource(R.drawable.hd_btn_recorder_normal);
					mRecordText.setText(R.string.button_pushtotalk);
					mRecordBtn.setBackgroundResource(R.drawable.hd_record_menu_mic_gray);
					break;
				case STATE_RECORDING:
					if (!isHasRecorderPermission) {
						PackageManager pkm = getContext().getPackageManager();
						boolean hasPermission = (PackageManager.PERMISSION_GRANTED == pkm.checkPermission("android.permission.RECORD_AUDIO", getContext().getPackageName()));
						if (!hasPermission) {
							reset();
							ToastHelper.show(getContext(), R.string.Recording_without_permission);
							return;
						}
					}
					isHasRecorderPermission = true;
					setBackgroundResource(R.drawable.hd_btn_recorder_recording);
					mRecordText.setText(R.string.recording_description);
					mRecordBtn.setBackgroundResource(R.drawable.hd_record_menu_mic_recording);
					//mDialogManager.recording();
					break;
				case STATE_WANT_TO_CANCEL:
					setBackgroundResource(R.drawable.hd_btn_recorder_recording);
					mRecordText.setText(R.string.release_to_cancel);
					mRecordBtn.setBackgroundResource(R.drawable.hd_record_menu_mic_cancel);
					break;
				default:
					break;
			}
		}
	}

	/*录音结束后的回调*/
	public interface AudioFinishRecorderListener {
		void onFinish(float seconds, String filePath);
	}

	AudioFinishRecorderListener mListener;

	public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener) {
		mListener = listener;
	}

	@Override
	protected void onDetachedFromWindow() {
		mAudioManager.setOnAudioStateListener(null);
		super.onDetachedFromWindow();
	}
}
