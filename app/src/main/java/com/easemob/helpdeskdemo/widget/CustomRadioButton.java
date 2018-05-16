package com.easemob.helpdeskdemo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 04/05/2018
 */

public class CustomRadioButton extends RadioButton {

	private String nickname;
	private String avatarUrl;

	public CustomRadioButton(Context context) {
		this(context, null);
	}

	public CustomRadioButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}






}
