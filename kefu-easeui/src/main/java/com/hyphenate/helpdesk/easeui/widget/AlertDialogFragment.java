package com.hyphenate.helpdesk.easeui.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.hyphenate.helpdesk.R;

public class AlertDialogFragment extends DialogFragment {
   /**
    * 默认左键隐藏
    * 左键默认text是取消 cancel
    * 右键默认text是确认 ok
    * 左右键默认listener 是 dismiss
    * **/


	private String title = null;
	private String content = null;
	private String leftBtnText = null;
	private String rightBtnText = null;
	private boolean showLeftBtn = false;
	private View.OnClickListener leftBtnListener = null;
	private View.OnClickListener rightBtnListener = null;

	private View.OnClickListener defaultListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			dismiss();
		}
	};

	public AlertDialogFragment() {
		// Required empty public constructor
	}


	public void setupLeftButton(String text,View.OnClickListener listener) {
		showLeftBtn = true;
		if (text != null) {
			leftBtnText = text;
		}
		if (listener != null) {
			leftBtnListener = listener;
		}
	}

	public void setTitleText(String title) {
		this.title = title;
	}

	public void setContentText(String content) {
		this.content = content;
	}

	public void setRightBtnText(String rightBtnText) {
		this.rightBtnText = rightBtnText;
	}

	public void setupRightBtn(String text, View.OnClickListener listener) {
		if (text != null) {
			rightBtnText = text;
		}
		if (listener != null) {
			rightBtnListener = listener;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		View dialogView = inflater.inflate(R.layout.hd_fragment_alert_dialog_layout, null);
		((TextView) dialogView.findViewById(R.id.alert_tittle)).setText(title);
		((TextView) dialogView.findViewById(R.id.alert_content)).setText(content);
		//LeftButton default gone

		if (showLeftBtn) {
			Button leftBtn = (Button) dialogView.findViewById(R.id.alert_left_btn);
			leftBtn.setVisibility(View.VISIBLE);
			if (leftBtnText != null) {
				leftBtn.setText(leftBtnText);
			}
			if (leftBtnListener == null) {
				leftBtn.setOnClickListener(defaultListener);
			} else {
				leftBtn.setOnClickListener(leftBtnListener);
			}
		}
		Button rightbtn = (Button) dialogView.findViewById(R.id.alert_right_btn);
		//RightButton
		if (rightBtnText != null) {
			rightbtn.setText(rightBtnText);
		}
		if (rightBtnListener == null) {
			rightbtn.setOnClickListener(defaultListener);
		} else {
			rightbtn.setOnClickListener(rightBtnListener);
		}
		return dialogView;
	}
}
