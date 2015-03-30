package com.easemob.helpdeskdemo.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.R.layout;
import com.easemob.helpdeskdemo.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

public class ModifiedCustomerActivity extends Activity {
	private InputMethodManager inputMethodManager;
	private int RESULT_TWO = 2;
	private ImageButton ib;
	private EditText edittext;
	private ImageButton clearSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modified_customer);
		showSoftkeyboard();
		ib = (ImageButton) findViewById(R.id.ib_modified_zhanghao_back);
		edittext = (EditText) findViewById(R.id.et_zhanghao);
		clearSearch = (ImageButton) findViewById(R.id.ib_search_clear);
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		String ap = getIntent().getStringExtra("zh");
		edittext.setText(ap);
		
		ib.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideSoftKeyboard();
				String stAppkey = edittext.getText().toString();
				Intent intent = new Intent();
				intent.putExtra("forzhanghao", stAppkey);
				ModifiedCustomerActivity.this.setResult(RESULT_TWO, intent);
				ModifiedCustomerActivity.this.finish();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_modified_customer, menu);
		return true;
	}
	
	void hideSoftKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	void showSoftkeyboard(){
		Timer timer = new Timer();
	     timer.schedule(new TimerTask()
	     {
	         public void run() 
	         {
	             InputMethodManager inputManager =
	                 (InputMethodManager)edittext.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	             inputManager.showSoftInput(edittext, 0);
	         }
	     },  
	         100);
	}
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		if (event.getAction() == KeyEvent.KEYCODE_BACK) {
//			hideSoftKeyboard();
//			String stAppkey = edittext.getText().toString();
//			Intent intent = new Intent();
//			intent.putExtra("forzhanghao", stAppkey);
//			ModifiedCustomerActivity.this.setResult(RESULT_TWO, intent);
//			ModifiedCustomerActivity.this.finish();
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}

}
