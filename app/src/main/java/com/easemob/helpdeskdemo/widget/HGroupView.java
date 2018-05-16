package com.easemob.helpdeskdemo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioGroup;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 04/05/2018
 */

public class HGroupView extends RadioGroup {

	public HGroupView(Context context) {
		this(context, null);
	}

	public HGroupView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(HORIZONTAL);
	}




}
