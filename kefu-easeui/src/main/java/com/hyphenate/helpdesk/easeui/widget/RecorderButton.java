package com.hyphenate.helpdesk.easeui.widget;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.easeui.recorder.AudioManager;
import com.hyphenate.helpdesk.easeui.recorder.DialogManager;
import com.hyphenate.util.PathUtil;

import java.lang.ref.WeakReference;

/**
 * Created by liyuzhao on 09/11/2016.
 */
public class RecorderButton extends Button implements AudioManager.AudioStateListener {

    private static int DISTANCE_Y_CANCEL = 10;

    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_TO_CANCEL = 3;

    private int mCurState = STATE_NORMAL;
    private boolean isRecording = false;

    /*是否触发longClick*/
    private boolean mReady;

    private DialogManager mDialogManager;
    private AudioManager mAudioManager;

    private boolean isHasRecorderPermission = false;

    /*记录时间*/
    private volatile float mTime;

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

    private static final int MSG_AUDIO_REPARED = 0x110;
    private static final int MSG_VOICE_CHANGE = 0x111;
    private static final int MSG_DIALOG_DISMISS = 0x112;

    private WeakHandler mHandler;

    private static class WeakHandler extends Handler {
        WeakReference<RecorderButton> weakReference;

        public WeakHandler(RecorderButton button) {
            this.weakReference = new WeakReference<RecorderButton>(button);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            RecorderButton button = weakReference.get();
            switch (msg.what) {
                case MSG_AUDIO_REPARED:
                    if (null != button) {
                        //真正显示应该是在audio prepare()之后
                        button.mDialogManager.showRecordingDialog();
                        button.isRecording = true;
                         /*开启线程获取音量,因为获取VoiceLevel是有时间间隔的*/
                        new Thread(button.mGetAudioVoiceLevelRunnable).start();
                    }
                    break;
                case MSG_VOICE_CHANGE:
                    if (null != button) {
                        button.mDialogManager.updateVoiceLevel(button.mAudioManager.getVoiceLevel(14));
                    }
                    break;
                case MSG_DIALOG_DISMISS:
                    if (null != button) {
                        button.mDialogManager.dismissDialog();
                    }
                    break;
            }
        }
    }


    public RecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        String dir = PathUtil.getInstance().getVoicePath().getPath();
        Log.i("info", dir);
        mAudioManager = AudioManager.getInstance(dir);
        mAudioManager.setOnAudioStateListener(this);
        mHandler = new WeakHandler(this);
        mDialogManager = new DialogManager(getContext());
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(new long[]{5, 9, 5, 9, 5, 9}, -1);
                mReady = true;
                mAudioManager.prepareAudio();
                return false;
            }
        });
    }

    public RecorderButton(Context context) {
        this(context, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
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
                reset();
                /*Audio结束，释放资源*/
                mAudioManager.cancel();
                mHandler.sendEmptyMessage(MSG_DIALOG_DISMISS);
                break;
            case MotionEvent.ACTION_UP:
                if (!mReady) {
                    reset();
                    return super.onTouchEvent(event);
                } else if (!isRecording || mTime < 0.6) {
                    //开始了prepare但是没有成功

                    /*Dialog显示时间短*/
                    mDialogManager.tooShort();

                    /*Audio结束，释放资源*/
                    mAudioManager.cancel();

                    /*延迟1s关闭tooShort的Dialog*/
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS, 1000);
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
        }
        return super.onTouchEvent(event);
    }

    private void reset() {
        //恢复标志位
        isRecording = false;
        mReady = false;
        mTime = 0;
        changeState(STATE_NORMAL);
    }

    private boolean wanttoCancel(int x, int y) {
        if (x < 0 || x > getWidth()) {
            return true;
        }
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        DISTANCE_Y_CANCEL = displayMetrics.heightPixels / 5;
        return y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL;
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
                    setBackgroundResource(R.drawable.btn_recorder_normal);
                    setText(R.string.button_pushtotalk);
                    break;
                case STATE_RECORDING:
                    if (!isHasRecorderPermission) {
                        PackageManager pkm = getContext().getPackageManager();
                        boolean hasPermission = (PackageManager.PERMISSION_GRANTED == pkm.checkPermission("android.permission.RECORD_AUDIO", getContext().getPackageName()));
                        if (!hasPermission) {
                            Toast.makeText(getContext(), R.string.Recording_without_permission, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    isHasRecorderPermission = true;
                    setBackgroundResource(R.drawable.btn_recorder_recording);
                    setText(R.string.str_recorder_recording);
                    mDialogManager.recording();
                    break;
                case STATE_WANT_TO_CANCEL:
                    setBackgroundResource(R.drawable.btn_recorder_recording);
                    setText(R.string.release_to_cancel);
                    mDialogManager.wantToCancel();
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


}
