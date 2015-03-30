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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class ModifiedAppkeyActivity extends Activity {
	private ImageButton clearSearch;
	private int RESULT_ONE = 1;
	private ImageButton ib;
	private EditText edittext;
	private InputMethodManager inputMethodManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modified_appkey);
		clearSearch = (ImageButton) findViewById(R.id.ib_search_clear);
		ib = (ImageButton) findViewById(R.id.ib_modified_appkey_back);
		edittext = (EditText) findViewById(R.id.et_appkey);
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		showSoftkeyboard();
		
		String ap = getIntent().getStringExtra("ap");
		edittext.setText(ap);
		
		ib.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String stAppkey = edittext.getText().toString();
				Intent intent = new Intent();
				intent.putExtra("forappkey", stAppkey);
				ModifiedAppkeyActivity.this.setResult(RESULT_ONE, intent);
				ModifiedAppkeyActivity.this.finish();
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
				}else if(count>0){
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_modified_appkey, menu);
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
//			String stAppkey = edittext.getText().toString();
//			Intent intent = new Intent();
//			intent.putExtra("forappkey", stAppkey);
//			ModifiedAppkeyActivity.this.setResult(RESULT_ONE, intent);
//			ModifiedAppkeyActivity.this.finish();
//			return true;
//		}
//
//		return super.onKeyDown(keyCode, event);
//	}

}
