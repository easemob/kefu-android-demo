package com.easemob.helpdeskdemo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.easemob.helpdeskdemo.R;


public class LauncherActivity extends Activity {

	private static final int AUTO_DELAY_MILLIS = 3000;

	private Handler mHandler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(!isTaskRoot()){
			finish();
			return;
		}
		setContentView(R.layout.activity_launcher);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(LauncherActivity.this, MainActivity.class));
				LauncherActivity.this.finish();
			}
		}, AUTO_DELAY_MILLIS);

	}
}
