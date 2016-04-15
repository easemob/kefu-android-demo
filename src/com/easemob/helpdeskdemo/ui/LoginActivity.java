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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.Preferences;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.helpdesk.Callback;
import com.hyphenate.helpdesk.ChatClient;
import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.ui.Arguments;


public class LoginActivity extends Activity {

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
		if (ChatClient.getInstance().isLoggedin()) {
			progressDialog = getProgressDialog();
			progressDialog.setMessage(getResources().getString(R.string.is_contact_customer));
			progressDialog.show();
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						//加载本地数据库中的消息到内存中
						EMClient.getInstance().chatManager().loadAllConversations();
					} catch (Exception e) {
						e.printStackTrace();
					}
					toChatActivity();
				}
			}).start();
		} else {
			//随机创建一个用户并登录环信服务器
			createAccountAndLoginChatServer();
		}

	}

	private void createAccountAndLoginChatServer() {
		// 自动生成账号
		final String account = Preferences.getInstance().getUserName();
		final String userPwd = Constant.DEFAULT_ACCOUNT_PWD;
		progressDialog = getProgressDialog();
		progressDialog.setMessage(getResources().getString(R.string.system_is_regist));
		progressDialog.show();
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if(ChatClient.getInstance().createAccount(account, userPwd)) {
					login(account, userPwd);
				} else {
					Toast.makeText(getApplicationContext(), "创建账户失败", Toast.LENGTH_SHORT).show();
					login(account, userPwd);
				}
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
				if (!progressShow) {
					return;
				}
				//DemoHelper.getInstance().setCurrentUserName(uname);
				//DemoHelper.getInstance().setCurrentPassword(upwd);
				try {
					EMClient.getInstance().chatManager().loadAllConversations();
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
				toChatActivity();
			}

			@Override
			public void onError() {
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
		});
	}

	private void toChatActivity() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (!LoginActivity.this.isFinishing())
					progressDialog.dismiss();
				// 进入主页面
				startActivity(new Intent(LoginActivity.this, ChatActivity.class).putExtra(
						Constant.INTENT_CODE_IMG_SELECTED_KEY, selectedIndex).putExtra(
						Constant.MESSAGE_TO_INTENT_EXTRA, messageToIndex).putExtra(
						Arguments.EXTRA_USER_ID, Preferences.getInstance().getCustomerAccount()));
				finish();
			}
		});
	}

}
