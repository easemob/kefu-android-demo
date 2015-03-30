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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.helpdeskdemo.DemoApplication;
import com.easemob.helpdeskdemo.DemoHXSDKHelper;
import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.domain.User;
import com.easemob.util.EMLog;

/**
 * 登陆页面
 * 
 */
public class LoginActivity extends BaseActivity {
	public static final int REQUEST_CODE_SETNICK = 1;

	private boolean progressShow;

	private String currentUsername;
	private String currentPassword;
	private static final int sleepTime = 2000;

	private EditText account;
	private EditText pwd;
	SharedPreferences sharedPreferences;
	private String image,price;

	private ProgressDialog progressDialog = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent newintent = getIntent();
		image = newintent.getStringExtra("image");
		price = newintent.getStringExtra("price");
		
		sharedPreferences = getSharedPreferences("shared", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();// 获取编辑器
		if (sharedPreferences.getBoolean("flag", true)) {
			editor.putBoolean("flag", false);
			editor.commit();
			// 自动生成账号
			currentUsername = getAccount();
			currentPassword = "123456";

			editor.putString("name", currentUsername);
			editor.putString("pwd", currentPassword);
			editor.commit();

			progressDialog = new ProgressDialog(LoginActivity.this);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					progressShow = false;
				}
			});
			progressDialog.setMessage(getResources().getString(R.string.system_is_regist));
//			if(pd.isShowing()){
//			}else{
			progressDialog.show();
//			}
			CreateAccountTask task = new CreateAccountTask();
			task.execute(currentUsername, currentPassword);
		} else {
			currentUsername = sharedPreferences.getString("name", "");
			currentPassword = sharedPreferences.getString("pwd", "");
			// Intent intent = new Intent(LoginActivity.this,
			// com.easemob.helpdeskdemo.activity.AlertDialog.class);
			// startActivityForResult(intent, REQUEST_CODE_SETNICK);
			// startActivity(new Intent(LoginActivity.this,
			// ChatActivity.class));
			// finish();
			
			if(EMChat.getInstance().isLoggedIn()){
//				EMChatManager.getInstance().logout(null);
				final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
				pd.setCanceledOnTouchOutside(false);
				pd.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						progressShow = false;
					}
				});
				pd.setMessage(getResources().getString(
						R.string.is_contact_customer));
				pd.show();
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							EMChatManager.getInstance()
									.loadAllConversations();
						} catch (Exception e) {
							e.printStackTrace();
							// 取好友或者群聊失败，不让进入主页面，也可以不管这个exception继续进到主页面
							runOnUiThread(new Runnable() {
								public void run() {
									pd.dismiss();
									DemoApplication.getInstance()
											.logout(null);
									Toast.makeText(
											getApplicationContext(),
											R.string.is_contact_customer_failure,
											1).show();
								}
							});
							return;
						}
						
						LoginActivity.this.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								if(pd!=null&&pd.isShowing()){
									pd.dismiss();
								}
								startActivity(new Intent(LoginActivity.this,
										ChatActivity.class).putExtra("userId",
										"customers").putExtra("image", image).putExtra("price", price));
								finish();
							}
						});
						
					}
				}).start();
				
			}else{
				Intent intent = new Intent(LoginActivity.this,
						com.easemob.helpdeskdemo.activity.AlertDialog.class);
				startActivityForResult(intent, REQUEST_CODE_SETNICK);
			}
			
		}
	}

	private class CreateAccountTask extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... args) {
			String userid = args[0];
			String pwd = args[1];
			try {
				EMChatManager.getInstance().createAccountOnServer(userid, pwd);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return userid;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(progressDialog!=null&&progressDialog.isShowing())
			{
				progressDialog.dismiss();
				progressDialog = null;
			}
			
			
			Intent intent = new Intent(LoginActivity.this,
					com.easemob.helpdeskdemo.activity.AlertDialog.class);
			startActivityForResult(intent, REQUEST_CODE_SETNICK);
		}
	}

	public String getAccount() {
		String val = "";
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; // 输出字母还是数字
			if ("char".equalsIgnoreCase(charOrNum)) // 字符串
			{
				int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; // 取得大写字母还是小写字母
				val += (char) (choice + random.nextInt(26));
			} else if ("num".equalsIgnoreCase(charOrNum)) // 数字
			{
				val += String.valueOf(random.nextInt(10));
			}
		}
		return val.toLowerCase();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_SETNICK) {
//				DemoApplication.currentUserNick = data
//						.getStringExtra("edittext");
				progressShow = true;
				final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
				pd.setCanceledOnTouchOutside(false);
				pd.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						progressShow = false;
					}
				});
				pd.setMessage(getResources().getString(
						R.string.is_contact_customer));
				pd.show();

				final long start = System.currentTimeMillis();
				// 调用sdk登陆方法登陆聊天服务器
				EMChatManager.getInstance().login(currentUsername,
						currentPassword, new EMCallBack() {
							@Override
							public void onSuccess() {
								if (!progressShow) {
									return;
								}
								DemoApplication.getInstance().setUserName(
										currentUsername);
								DemoApplication.getInstance().setPassword(
										currentPassword);
								try {
									EMChatManager.getInstance()
											.loadAllConversations();
								} catch (Exception e) {
									e.printStackTrace();
									// 取好友或者群聊失败，不让进入主页面，也可以不管这个exception继续进到主页面
									runOnUiThread(new Runnable() {
										public void run() {
											pd.dismiss();
											DemoApplication.getInstance()
													.logout(null);
											Toast.makeText(
													getApplicationContext(),
													R.string.is_contact_customer_failure,
													1).show();
										}
									});
									return;
								}
								if (!LoginActivity.this.isFinishing())
									pd.dismiss();
								// 进入主页面
								
								startActivity(new Intent(LoginActivity.this,
										ChatActivity.class).putExtra("userId",
										"customers").putExtra("image", image).putExtra("price", price));
								finish();
							}

							@Override
							public void onProgress(int progress, String status) {
							}

							@Override
							public void onError(final int code,
									final String message) {
								if (!progressShow) {
									return;
								}
								runOnUiThread(new Runnable() {
									public void run() {
										pd.dismiss();
										Intent intent = new Intent();
										intent.setClass(getApplication(),
												FirstActivity.class);
										startActivity(intent);
										Toast.makeText(
												getApplicationContext(),
												getResources()
														.getString(
																R.string.is_contact_customer_failure_seconed)
														+ message,
												Toast.LENGTH_SHORT).show();
									}
								});
							}
						});
			}
		}
	}

}
