/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.easemob.helpdeskdemo.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.ui.LoginActivity;


public class PopupListWindow extends PopupWindow implements OnClickListener {

	private View contentView;
	private Context mContext;

	public PopupListWindow(Context context) {
		this.mContext = context;
		contentView = LayoutInflater.from(context).inflate(R.layout.em_popup_list_window, null);
		this.setContentView(contentView);
		this.setWidth(LayoutParams.WRAP_CONTENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		this.update();
		ColorDrawable cDraw = new ColorDrawable(Color.parseColor("#00000000"));
		this.setBackgroundDrawable(cDraw);
		contentView.findViewById(R.id.btn_pre_sales).setOnClickListener(this);
		contentView.findViewById(R.id.btn_after_sales).setOnClickListener(this);
	}

	public void showPopupWindow(View parent) {
		if (!this.isShowing()) {
			this.showAsDropDown(parent);
		} else {
			this.dismiss();
		}
	}

	@Override
	public void onClick(View v) {
		this.dismiss();
		switch (v.getId()) {
		case R.id.btn_pre_sales:
			mContext.startActivity(new Intent(mContext, LoginActivity.class).putExtra(Constant.MESSAGE_TO_INTENT_EXTRA,
					Constant.MESSAGE_TO_PRE_SALES));
			break;
		case R.id.btn_after_sales:
			mContext.startActivity(new Intent(mContext, LoginActivity.class).putExtra(Constant.MESSAGE_TO_INTENT_EXTRA,
					Constant.MESSAGE_TO_AFTER_SALES));
			break;
		default:
			break;
		}
	}

}
