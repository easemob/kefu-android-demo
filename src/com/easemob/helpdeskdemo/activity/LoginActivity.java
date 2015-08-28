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

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.utils.CommonUtils;

/**
 * 登陆页面
 * 
 */
public class LoginActivity extends BaseActivity {
	private boolean progressShow;
	private ProgressDialog progressDialog;
	private int selectedIndex = Constant.INTENT_CODE_IMG_SELECTED_DEFAULT;
	private int messageToIndex = Constant.MESSAGE_TO_DEFAULT;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		selectedIndex = intent.getIntExtra(Constant.INTENT_CODE_IMG_SELECTED_KEY, Constant.INTENT_CODE_IMG_SELECTED_DEFAULT);
		messageToIndex = intent.getIntExtra(Constant.MESSAGE_TO_INTENT_EXTRA, Constant.MESSAGE_TO_DEFAULT);
		if(EMChat.getInstance().isLoggedIn()){
			progressDialog = getProgressDialog();
			progressDialog.setMessage(getResources().getString(
					R.string.is_contact_customer));
			progressDialog.show();
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						EMChatManager.getInstance()
								.loadAllConversations();
					} catch (Exception e) {
						e.printStackTrace();
					}
					toChatActivity();
				}
			}).start();
		}else{
			createRandomAccountAndLoginChatServer();
		}
	}
	
	
	public void createRandomAccountAndLoginChatServer() {
		// 自动生成账号
		final String randomAccount = CommonUtils.getRandomAccount();
		final String userPwd = Constant.DEFAULT_ACCOUNT_PWD;
		progressDialog = getProgressDialog();
		progressDialog.setMessage(getResources().getString(R.string.system_is_regist));
		progressDialog.show();
		createAccountToServer(randomAccount, userPwd, new EMCallBack() {

			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						loginHuanxinServer(randomAccount, userPwd);
					}
				});
			}

			@Override
			public void onProgress(int progress, String status) {
			}

			@Override
			public void onError(final int errorCode, final String message) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (!LoginActivity.this.isFinishing()) {
							progressDialog.dismiss();
						}
						if (errorCode == EMError.NONETWORK_ERROR) {
							Toast.makeText(getApplicationContext(), "网络不可用", Toast.LENGTH_SHORT).show();
						} else if (errorCode == EMError.USER_ALREADY_EXISTS) {
							Toast.makeText(getApplicationContext(), "用户已存在", Toast.LENGTH_SHORT).show();
						} else if (errorCode == EMError.UNAUTHORIZED) {
							Toast.makeText(getApplicationContext(), "无开放注册权限", Toast.LENGTH_SHORT).show();
						} else if (errorCode == EMError.ILLEGAL_USER_NAME) {
							Toast.makeText(getApplicationContext(), "用户名非法", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplicationContext(), "注册失败：" + message, Toast.LENGTH_SHORT).show();
						}
						finish();
					}
				});
			}
		});
	}
	
	
	private void createAccountToServer(final String uname, final String pwd, final EMCallBack callback) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					EMChatManager.getInstance().createAccountOnServer(uname, pwd);
					if (callback != null) {
						callback.onSuccess();
					}
				} catch (EaseMobException e) {
					if (callback != null) {
						callback.onError(e.getErrorCode(), e.getMessage());
					}
				}
			}
		});
		thread.start();
	}
	
	private ProgressDialog  getProgressDialog(){
		if(progressDialog == null){
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
	
	
	 
	public void loginHuanxinServer(final String uname,final String upwd){
		progressShow = true;
		progressDialog = getProgressDialog();
		progressDialog.setMessage(getResources().getString(
				R.string.is_contact_customer));
		if(!progressDialog.isShowing()){
			progressDialog.show();
		}
		// login huanxin server
		EMChatManager.getInstance().login(uname, upwd, new EMCallBack() {
			@Override
			public void onSuccess() {
				if (!progressShow) {
					return;
				}
				HXSDKHelper.getInstance().setHXId(uname);
				HXSDKHelper.getInstance().setPassword(upwd);
				try {
					EMChatManager.getInstance().loadAllConversations();
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
				toChatActivity();
			}

			@Override
			public void onProgress(int progress, String status) {
			}

			@Override
			public void onError(final int code, final String message) {
				if (!progressShow) {
					return;
				}
				runOnUiThread(new Runnable() {
					public void run() {
						progressDialog.dismiss();
						Toast.makeText(LoginActivity.this,
								getResources().getString(R.string.is_contact_customer_failure_seconed) + message,
								Toast.LENGTH_SHORT).show();
						finish();
					}
				});
			}
		});
	}
	
	private void toChatActivity(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (!LoginActivity.this.isFinishing())
					progressDialog.dismiss();
				// 进入主页面
				startActivity(new Intent(LoginActivity.this, ChatActivity.class)
						.putExtra(Constant.INTENT_CODE_IMG_SELECTED_KEY, selectedIndex).putExtra(Constant.MESSAGE_TO_INTENT_EXTRA, messageToIndex));
				finish();
			}
		});
	}

}
