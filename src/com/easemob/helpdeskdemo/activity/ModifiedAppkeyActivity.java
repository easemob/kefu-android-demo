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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.easemob.helpdeskdemo.R;

public class ModifiedAppkeyActivity extends Activity {
	private ImageButton clearSearch;
	private EditText edittext;
	private ImageButton ibModifyAppkey;
	private InputMethodManager inputMethodManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modified_appkey);
		initView();
		initListener();

	}

	private void initListener() {
		showSoftkeyboard();
		String oldAppkey = getIntent().getStringExtra(SettingFragment.INTENT_KEY_MODIFY_APPKEY);
		edittext.setText(oldAppkey);
		ibModifyAppkey.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String stAppkey = edittext.getText().toString();
				setResult(RESULT_OK, new Intent().putExtra(SettingFragment.INTENT_KEY_MODIFY_APPKEY, stAppkey));
				finish();
			}
		});

		edittext.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() > 0) {
					clearSearch.setVisibility(View.VISIBLE);
				} else {
					clearSearch.setVisibility(View.INVISIBLE);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				if (s.length() > 0) {
					clearSearch.setVisibility(View.VISIBLE);
				} else if (count > 0) {
					clearSearch.setVisibility(View.VISIBLE);
				}
			}

			public void afterTextChanged(Editable s) {
			}
		});
		clearSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				edittext.getText().clear();
				hideSoftKeyboard();
			}
		});

	}

	private void initView() {
		clearSearch = (ImageButton) findViewById(R.id.ib_search_clear);
		ibModifyAppkey = (ImageButton) findViewById(R.id.ib_modified_appkey_back);
		edittext = (EditText) findViewById(R.id.et_appkey);
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

}
