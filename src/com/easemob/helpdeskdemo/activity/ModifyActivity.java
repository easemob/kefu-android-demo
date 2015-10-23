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
package com.easemob.helpdeskdemo.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.R;

public class ModifyActivity extends BaseActivity implements OnClickListener, TextWatcher{

	private ImageButton btnClear;
	private EditText edittext;
	private ImageButton btnBack;
	private InputMethodManager inputMethodManager;
	private int index = Constant.MODIFY_INDEX_DEFAULT;
	private String txtContent;
	private TextView txtTitle;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_modify);
		Intent intent = getIntent();
		index = intent.getIntExtra(Constant.MODIFY_ACTIVITY_INTENT_INDEX, Constant.MODIFY_INDEX_DEFAULT);
		txtContent = intent.getStringExtra(Constant.MODIFY_ACTIVITY_INTENT_CONTENT);
		initView();
		initListener();
		switch (index) {
		case Constant.MODIFY_INDEX_APPKEY:
			txtTitle.setText(R.string.appkey);
			break;
		case Constant.MODIFY_INDEX_ACCOUNT:
			txtTitle.setText(R.string.customer_account);
			break;
		case Constant.MODIFY_INDEX_NICK:
			txtTitle.setText(R.string.login_user_nick);
			break;
		default:
			break;
		}
		if(!TextUtils.isEmpty(txtContent)){
			edittext.setText(txtContent);
		}
	}


	private void initListener() {
		edittext.addTextChangedListener(this);
		btnBack.setOnClickListener(this);
		btnClear.setOnClickListener(this);
		showSoftkeyboard();
	}


	private void initView() {
		btnClear = (ImageButton) findViewById(R.id.ib_clear);
		btnBack = (ImageButton) findViewById(R.id.ib_back);
		edittext = (EditText) findViewById(R.id.edittext);
		txtTitle = (TextView) findViewById(R.id.txtTitle);
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	}
	
	
	void hideSoftKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	void showSoftkeyboard() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				InputMethodManager inputManager = (InputMethodManager) edittext.getContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(edittext, 0);
			}
		}, 100);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_back:
			hideSoftKeyboard();
			String strContent = edittext.getText().toString();
			setResult(RESULT_OK, new Intent().putExtra("content", strContent));
			finish();
			break;
		case R.id.ib_clear:
			edittext.getText().clear();
			hideSoftKeyboard();
			break;
		default:
			break;
		}
	}


	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		if (s.length() > 0 || count > 0) {
			btnClear.setVisibility(View.VISIBLE);
		}
	}


	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if(s.length()>0){
			btnClear.setVisibility(View.VISIBLE);
		}else{
			btnClear.setVisibility(View.INVISIBLE);
		}
	}


	@Override
	public void afterTextChanged(Editable s) {
	}
	
	
}
