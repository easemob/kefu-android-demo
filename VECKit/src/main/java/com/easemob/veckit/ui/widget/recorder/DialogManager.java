package com.easemob.veckit.ui.widget.recorder;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.veckit.R;

import java.text.DecimalFormat;

/**
 * Created by liyuzhao on 09/11/2016.
 */
public class DialogManager {

    private Dialog mDialog;
    //    private ImageView mIcon;
    private ImageView mVoice;

    private TextView mLabel;
    private Context mContext;
    private DecimalFormat decimalFormat = new DecimalFormat("0.0");

    public DialogManager(Context context) {
        mContext = context;
    }

    public void showRecordingDialog() {
        mDialog = new Dialog(mContext, R.style.Theme_Audio_Dialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.vec_widget_voice_recorder, null);
        mDialog.setContentView(view);
        mVoice = (ImageView) mDialog.findViewById(R.id.mic_image);
        mLabel = (TextView) mDialog.findViewById(R.id.recording_hint);
        mDialog.show();
    }

    public void recording() {
        if (mDialog != null && mDialog.isShowing()) {
            mVoice.setVisibility(View.VISIBLE);
            mLabel.setVisibility(View.VISIBLE);

            mLabel.setText(R.string.vec_move_up_to_cancel);
            mLabel.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    public void wantToCancel() {
        if (mDialog != null && mDialog.isShowing()) {
            mVoice.setVisibility(View.VISIBLE);
            mLabel.setVisibility(View.VISIBLE);

            mLabel.setText(R.string.vec_release_to_cancel);
            mLabel.setBackgroundResource(R.drawable.hd_recording_text_hint_bg);

        }
    }

    public void tooShort() {
        if (mDialog != null && mDialog.isShowing()) {
            mVoice.setVisibility(View.GONE);
            mLabel.setVisibility(View.VISIBLE);

            mLabel.setText(R.string.vec_the_recording_time_is_too_short);
        }

    }


    public void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public void updateVoiceLevel(int level) {
        if (mDialog != null && mDialog.isShowing()) {
            int resId = mContext.getResources().getIdentifier("hd_record_animate_" + level, "drawable", mContext.getPackageName());
            mVoice.setImageResource(resId);
        }
    }
    public void updateRecordTime(float time) {
        if (mDialog != null && mDialog.isShowing()) {
            mLabel.setText(decimalFormat.format(time));
        }
    }
}
