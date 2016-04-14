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

import java.util.List;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hyphenate.helpdesk.ChatClient;
import com.hyphenate.helpdesk.Error;
import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.R;

public class MainActivity extends BaseActivity{

	private ShopFragment shopFragment;
	private SettingFragment settingFragment;
	private Button[] mRadioButtons;
	private Fragment[] fragments;
	private int index;
	private int currentTabIndex;
	private ImageButton imageButton_shop, imageButton_setting;
	private LinearLayout ll_shop, ll_setting;
	private MyConnectionListener connectionListener = null;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.em_activity_main);
		init();
	}
	
	@SuppressLint("NewApi")
	private void init() {
		ll_shop = (LinearLayout) findViewById(R.id.ll_shop);
		ll_setting = (LinearLayout) findViewById(R.id.ll_setting);
		imageButton_shop = (ImageButton) findViewById(R.id.imageButton_shop);
		imageButton_setting = (ImageButton) findViewById(R.id.imageButton_setting);
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl_buttom_bg);
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_btn_group);
		linearLayout.setAlpha(0.5f);
		shopFragment = new ShopFragment();
		settingFragment = new SettingFragment();
		fragments = new Fragment[] { shopFragment, settingFragment };
		mRadioButtons = new Button[2];
		mRadioButtons[0] = (Button) findViewById(R.id.main_btn_home_page);
		mRadioButtons[1] = (Button) findViewById(R.id.main_btn_setting_page);
		// 把shopFragment设为选中状态
		FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
		trx.add(R.id.fragment_container, shopFragment).add(R.id.fragment_container, settingFragment).hide(settingFragment).show(shopFragment);
		trx.commit();
		mRadioButtons[0].setSelected(true);
		imageButton_shop.setImageResource(R.drawable.em_icon_shop_select);
		
		//注册一个监听连接状态的listener
		connectionListener = new MyConnectionListener();
		ChatClient.getInstance().addConnectionListener(connectionListener);
	}
	
	public class MyConnectionListener implements ChatClient.ConnectionListener {

		@Override
		public void onConnected() {
			
		}

		@Override
		public void onDisconnected(final int error) {
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
				    ChatClient.getInstance().logout(null);	
				    
				}
			});
		}
		
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	
	

	public void onTabClick(View view) {
		Resources resource = (Resources) getBaseContext().getResources();
		ColorStateList csl = (ColorStateList) resource
				.getColorStateList(R.color.text_selected_color);
		switch (view.getId()) {
		case R.id.main_btn_home_page:
			mRadioButtons[0].setTextColor(csl);
			mRadioButtons[1].setTextColor(Color.GRAY);
			imageButton_setting
					.setImageResource(R.drawable.em_icon_setting_normal);
			imageButton_shop.setImageResource(R.drawable.em_icon_shop_select);
			index = 0;
			break;
		case R.id.imageButton_shop:
			mRadioButtons[0].setTextColor(csl);
			mRadioButtons[1].setTextColor(Color.GRAY);
			imageButton_setting
					.setImageResource(R.drawable.em_icon_setting_normal);
			imageButton_shop.setImageResource(R.drawable.em_icon_shop_select);
			index = 0;
			break;
		case R.id.main_btn_setting_page:
			imageButton_shop.setImageResource(R.drawable.em_icon_shop_normal);
			imageButton_setting
					.setImageResource(R.drawable.em_icon_setting_select);
			mRadioButtons[0].setTextColor(Color.GRAY);
			mRadioButtons[1].setTextColor(csl);
			index = 1;
			break;
		case R.id.imageButton_setting:
			imageButton_shop.setImageResource(R.drawable.em_icon_shop_normal);
			imageButton_setting
					.setImageResource(R.drawable.em_icon_setting_select);
			mRadioButtons[0].setTextColor(Color.GRAY);
			mRadioButtons[1].setTextColor(csl);
			index = 1;
			break;
		}
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager()
					.beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}
			trx.show(fragments[index]).commit();
		}
		mRadioButtons[currentTabIndex].setSelected(false);
		// 把当前tab设为选中状态
		mRadioButtons[index].setSelected(true);
		currentTabIndex = index;
	}

	public void contactCustomer(View view) {
		switch (view.getId()) {
		case R.id.ll_setting_list_customer:
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, LoginActivity.class);
			intent.putExtra(Constant.MESSAGE_TO_INTENT_EXTRA, Constant.MESSAGE_TO_DEFAULT);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		// 把此activity 从foreground activity 列表里移除
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(connectionListener != null){
			ChatClient.getInstance().removeConnectionListener();
		}
	}
}

