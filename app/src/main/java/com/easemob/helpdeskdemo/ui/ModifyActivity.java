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
package com.easemob.helpdeskdemo.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.R;

import java.util.Timer;
import java.util.TimerTask;

public class ModifyActivity extends DemoBaseActivity implements View.OnClickListener, TextWatcher {

	private ImageButton btnClear;
	private EditText edittext;
	private RelativeLayout btnBack;
	private RelativeLayout rlSave;
	private InputMethodManager inputMethodManager;
	private int index = Constant.MODIFY_INDEX_DEFAULT;
	private String txtContent;
	private TextView txtTitle;
    private RelativeLayout titleLayout;
	private RelativeLayout saveLayout;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.em_activity_modify);
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
		case Constant.MODIFY_INDEX_TENANT_ID:
			txtTitle.setText(R.string.set_tenantId);
			break;
		case Constant.MODIFY_INDEX_PROJECT_ID:
			txtTitle.setText(R.string.set_leave_messageid);
			break;
		case Constant.MODIFY_INDEX_LEAVE_NAME:
			txtTitle.setText(R.string.leave_name);
			break;
		case Constant.MODIFY_INDEX_LEAVE_PHONE:
			txtTitle.setText(R.string.leave_phone);
			break;
		case Constant.MODIFY_INDEX_LEAVE_EMAIL:
			txtTitle.setText(R.string.leave_email);
			break;
		case Constant.MODIFY_INDEX_LEAVE_CONTENT:
			txtTitle.setText(R.string.leave_content);
			break;
		case Constant.MODIFY_INDEX_LEAVE_CONFIG:
			txtTitle.setText(R.string.leave_config);
			break;

		case Constant.MODIFY_INDEX_VEC_ACCOUNT:
			txtTitle.setText(R.string.vec_customernumber);
			break;

		case Constant.MODIFY_INDEX_LEAVE_VEC_CONFIG:
			txtTitle.setText(R.string.customernumber_config);
			break;
		default:
			break;
		}
		/*if (index >= Constant.MODIFY_INDEX_LEAVE_NAME) {
			titleLayout.setBackgroundResource(R.color.sub_page_title_bg_color);
			saveLayout.setBackgroundResource(R.color.sub_page_title_bg_color);
		} else {
			titleLayout.setBackgroundResource(R.color.title_bg_color);
			saveLayout.setBackgroundResource(R.color.title_bg_color);
		}*/

		titleLayout.setBackgroundResource(R.color.title_bg_color);
		saveLayout.setBackgroundResource(R.color.title_bg_color);

		if (!TextUtils.isEmpty(txtContent)) {
			edittext.setText(txtContent);
			edittext.setSelection(txtContent.length());
		}
	}

	private void initListener() {
		edittext.addTextChangedListener(this);
		btnBack.setOnClickListener(this);
		btnClear.setOnClickListener(this);
		rlSave.setOnClickListener(this);
		edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
					actionSave();
					return true;
				}
				return false;
			}
		});
		showSoftkeyboard();
	}

	private void initView() {
		btnClear = (ImageButton) findViewById(R.id.ib_clear);
		btnBack = (RelativeLayout) findViewById(R.id.rl_back);
		edittext = (EditText) findViewById(R.id.edittext);
		txtTitle = (TextView) findViewById(R.id.txtTitle);
		rlSave = (RelativeLayout) findViewById(R.id.rl_sub_modify_save);
		titleLayout = (RelativeLayout) findViewById(R.id.rl_modified_title);
		saveLayout = (RelativeLayout) findViewById(R.id.rl_modified_save);
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
				assert inputManager != null;
				inputManager.showSoftInput(edittext, 0);
			}
		}, 100);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_back:
			finish();
			break;
		case R.id.rl_sub_modify_save:
			actionSave();
			break;
		case R.id.ib_clear:
			edittext.getText().clear();
			break;
		default:
			break;
		}
	}

	private void actionSave() {
		hideSoftKeyboard();
		String strContent = edittext.getText().toString();
		setResult(RESULT_OK, new Intent().putExtra("content", strContent));
		finish();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		if (s.length() > 0 || count > 0) {
			btnClear.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (s.length() > 0) {
			btnClear.setVisibility(View.VISIBLE);
		} else {
			btnClear.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

}
