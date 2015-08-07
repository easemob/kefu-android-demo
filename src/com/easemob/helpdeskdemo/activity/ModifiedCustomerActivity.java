package com.easemob.helpdeskdemo.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.easemob.helpdeskdemo.R;

public class ModifiedCustomerActivity extends Activity {
	private InputMethodManager inputMethodManager;
	private ImageButton ibModifyAccount;
	private EditText edittext;
	private ImageButton clearSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modified_customer);
		initView();
		initListener();
	}

	private void initListener() {
		showSoftkeyboard();
		String oldAccount = getIntent().getStringExtra(SettingFragment.INTENT_KEY_MODIFY_ACCOUNT);
		edittext.setText(oldAccount);
		ibModifyAccount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideSoftKeyboard();
				String newAccount = edittext.getText().toString();
				Intent intent = new Intent();
				intent.putExtra(SettingFragment.INTENT_KEY_MODIFY_ACCOUNT, newAccount);
				setResult(RESULT_OK, intent);
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
		ibModifyAccount = (ImageButton) findViewById(R.id.ib_modified_zhanghao_back);
		edittext = (EditText) findViewById(R.id.et_zhanghao);
		clearSearch = (ImageButton) findViewById(R.id.ib_search_clear);
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
