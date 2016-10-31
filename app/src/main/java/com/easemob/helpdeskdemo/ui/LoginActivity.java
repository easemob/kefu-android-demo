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

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.MessageHelper;
import com.easemob.helpdeskdemo.Preferences;
import com.easemob.helpdeskdemo.R;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.Error;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.easeui.util.IntentBuilder;


public class LoginActivity extends DemoBaseActivity {

	private static final String TAG = "LoginActivity";

	private boolean progressShow;
	private ProgressDialog progressDialog;
	private int selectedIndex = Constant.INTENT_CODE_IMG_SELECTED_DEFAULT;
	private int messageToIndex = Constant.MESSAGE_TO_DEFAULT;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		Intent intent = getIntent();
		selectedIndex = intent.getIntExtra(Constant.INTENT_CODE_IMG_SELECTED_KEY,
				Constant.INTENT_CODE_IMG_SELECTED_DEFAULT);
		messageToIndex = intent.getIntExtra(Constant.MESSAGE_TO_INTENT_EXTRA, Constant.MESSAGE_TO_DEFAULT);
		
		//EMChat.getInstance().isLoggedIn() 可以检测是否已经登录过环信，如果登录过则环信SDK会自动登录，不需要再次调用登录操作
		if (ChatClient.getInstance().isLoggedInBefore()) {
			progressDialog = getProgressDialog();
			progressDialog.setMessage(getResources().getString(R.string.is_contact_customer));
			progressDialog.show();
			toChatActivity();
		} else {
			//随机创建一个用户并登录环信服务器
			createRandomAccountThenLoginChatServer();
		}

	}


	private void createRandomAccountThenLoginChatServer() {
		// 自动生成账号,此处每次都随机生成一个账号,为了演示.正式应从自己服务器获取账号
		final String account = Preferences.getInstance().getUserName();
		final String userPwd = Constant.DEFAULT_ACCOUNT_PWD;
		progressDialog = getProgressDialog();
		progressDialog.setMessage(getString(R.string.system_is_regist));
		progressDialog.show();
		// createAccount to huanxin server
		// if you have a account, this step will ignore
		ChatClient.getInstance().createAccount(account, userPwd, new Callback() {
			@Override
			public void onSuccess() {
				Log.d(TAG, "demo register success");
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//登录环信服务器
						login(account, userPwd);
					}
				});
			}

			@Override
			public void onError(final int errorCode, String error) {
				runOnUiThread(new Runnable() {
					public void run() {
						if(progressDialog != null && progressDialog.isShowing()){
							progressDialog.dismiss();
						}
						if (errorCode == Error.NETWORK_ERROR){
							Toast.makeText(getApplicationContext(), "网络不可用", Toast.LENGTH_SHORT).show();
						}else if (errorCode == Error.USER_ALREADY_EXIST){
							Toast.makeText(getApplicationContext(), "用户已经存在", Toast.LENGTH_SHORT).show();
						}else if(errorCode == Error.USER_AUTHENTICATION_FAILED){
							Toast.makeText(getApplicationContext(), "无开放注册权限", Toast.LENGTH_SHORT).show();
						} else if (errorCode == Error.USER_ILLEGAL_ARGUMENT){
							Toast.makeText(getApplicationContext(), "用户名非法", Toast.LENGTH_SHORT).show();
						}else {
							Toast.makeText(getApplicationContext(), getString(R.string.register_user_fail), Toast.LENGTH_SHORT).show();
						}
						finish();
					}
				});
			}

			@Override
			public void onProgress(int progress, String status) {

			}
		});
	}

	private ProgressDialog getProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(LoginActivity.this);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					progressShow = false;
				}
			});
		}
		return progressDialog;
	}

	private void login(final String uname, final String upwd) {
		progressShow = true;
		progressDialog = getProgressDialog();
		progressDialog.setMessage(getResources().getString(R.string.is_contact_customer));
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}
		// login huanxin server
		ChatClient.getInstance().login(uname, upwd, new Callback() {
			@Override
			public void onSuccess() {
				Log.d(TAG, "demo login success!");
				if (!progressShow) {
					return;
				}
				toChatActivity();
			}

			@Override
			public void onError(int code, String error) {
				Log.e(TAG, "login fail,code:" + code + ",error:" + error);
				if (!progressShow) {
					return;
				}
				runOnUiThread(new Runnable() {
					public void run() {
						progressDialog.dismiss();
						Toast.makeText(LoginActivity.this,
								getResources().getString(R.string.is_contact_customer_failure_seconed),
								Toast.LENGTH_SHORT).show();
						finish();
					}
				});
			}

			@Override
			public void onProgress(int progress, String status) {

			}
		});
	}

	private void toChatActivity() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (!LoginActivity.this.isFinishing())
					progressDialog.dismiss();

				//此处演示设置技能组,如果后台设置的技能组名称为[shouqian|shouhou],这样指定即分配到技能组中.
				//为null则不按照技能组分配,同理可以设置直接指定客服scheduleAgent
				String queueName = null;
				switch (messageToIndex){
					case Constant.MESSAGE_TO_AFTER_SALES:
						queueName = "shouhou";
						break;
					case Constant.MESSAGE_TO_PRE_SALES:
						queueName = "shouqian";
						break;
				}
				Bundle bundle = new Bundle();
				bundle.putInt(Constant.INTENT_CODE_IMG_SELECTED_KEY, selectedIndex);

				// 进入主页面
				Intent intent = new IntentBuilder(LoginActivity.this)
						.setTargetClass(ChatActivity.class)
						.setVisitorInfo(MessageHelper.createVisitorInfo())
						.setServiceIMNumber(Preferences.getInstance().getCustomerAccount())
						.setScheduleQueue(MessageHelper.createQueueIdentity(queueName))
//						.setScheduleAgent(MessageHelper.createAgentIdentity("ceshiok1@qq.com"))
						.setShowUserNick(true)
						.setBundle(bundle)
						.build();
				startActivity(intent);
				finish();

			}
		});
	}

}
