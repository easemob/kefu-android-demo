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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.HMSPushHelper;
import com.easemob.helpdeskdemo.Preferences;
import com.easemob.helpdeskdemo.R;
import com.easemob.veckit.VECKitCalling;
import com.easemob.veckit.utils.FlatFunctionUtils;
import com.hyphenate.agora.FunctionIconItem;
import com.hyphenate.chat.AgoraMessage;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.VecConfig;
import com.hyphenate.helpdesk.Error;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.callback.ValueCallBack;
import com.hyphenate.helpdesk.easeui.widget.ToastHelper;

import java.util.List;
import java.util.Locale;

import static java.lang.Compiler.enable;

public class ShopDetailsActivity extends DemoBaseActivity {
	private View rl_tochat;
	private RelativeLayout mImageButton;
	private ImageView iv_buy_1;
	private ImageView iv_buy_2;
	private ImageView iv_buy_3;
	private int index = Constant.INTENT_CODE_IMG_SELECTED_DEFAULT;
	private ProgressDialog progressDialog;
	private boolean progressShow;
	private View mLineView;
	private View mCallVideoLlt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.em_activity_shop_details);
		index = getIntent().getIntExtra(Constant.INTENT_CODE_IMG_SELECTED_KEY, Constant.INTENT_CODE_IMG_SELECTED_DEFAULT);


		rl_tochat = $(R.id.rl_tochat);
		mImageButton = $(R.id.rl_back);
		iv_buy_1 = $(R.id.iv_buy_part1);
		iv_buy_2 = $(R.id.iv_buy_part2);
		iv_buy_3 = $(R.id.iv_buy_part3);

		mLineView = $(R.id.lineView);
		mCallVideoLlt = $(R.id.callVideoLlt);
		mLineView.setVisibility(VecConfig.newVecConfig().isEnableVideo() ? View.VISIBLE : View.GONE);
		mCallVideoLlt.setVisibility(VecConfig.newVecConfig().isEnableVideo() ? View.VISIBLE : View.GONE);

		/*if (!VecConfig.newVecConfig().isEnableVideo()){
			getIconEndable(ChatClient.getInstance().tenantId());
		}*/

		getIconEndable(ChatClient.getInstance().tenantId());

		mCallVideoLlt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//设置点击通知栏跳转事件
				if (!VecConfig.newVecConfig().isEnableVideo()){
					Toast.makeText(getApplication(), "未开灰度！", Toast.LENGTH_LONG).show();
					return;
				}


				if (ChatClient.getInstance().isLoggedInBefore()){
					VECKitCalling.callingRequest(ShopDetailsActivity.this, Preferences.getInstance().getCustomerAccount());
					// CallVideoActivity.callingRequest(ShopDetailsActivity.this, Preferences.getInstance().getCustomerAccount());
				}else {
					createRandomAccountThenLoginChatServer();
				}
			}
		});

		//长图分图显示
		setLongPicRes();

		mImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShopDetailsActivity.this.finish();
			}
		});
		rl_tochat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				progressDialog = getProgressDialog();
				progressDialog.setMessage(getString(R.string.is_contact_customer));
				progressDialog.show();

				AgoraMessage.asyncGetTenantIdFunctionIcons(ChatClient.getInstance().tenantId(), new ValueCallBack<List<FunctionIconItem>>() {
					@Override
					public void onSuccess(List<FunctionIconItem> value) {
						checkLogin();
					}

					@Override
					public void onError(int error, String errorMsg) {
						checkLogin();
					}
				});
			}
		});
	}

	private void checkLogin(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressDialog.dismiss();

				Intent intent = new Intent();
				intent.putExtra(Constant.INTENT_CODE_IMG_SELECTED_KEY, index);
				intent.putExtra(Constant.MESSAGE_TO_INTENT_EXTRA, Constant.MESSAGE_TO_AFTER_SALES);
				intent.setClass(ShopDetailsActivity.this, LoginActivity.class);
				startActivity(intent);
			}
		});

	}

	private void getIconEndable(String tenantId) {
		AgoraMessage.asyncGetTenantIdFunctionIcons(tenantId, new ValueCallBack<List<FunctionIconItem>>() {
			@Override
			public void onSuccess(List<FunctionIconItem> value) {

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mLineView.setVisibility(VecConfig.newVecConfig().isEnableVideo() ? View.VISIBLE : View.GONE);
						mCallVideoLlt.setVisibility(VecConfig.newVecConfig().isEnableVideo() ? View.VISIBLE : View.GONE);
					}
				});
			}

			@Override
			public void onError(int error, String errorMsg) {

			}
		});
	}

	private void setLongPicRes() {
		int iImageView1;
		int iImageView2;
		int iImageView3;
		String sImageView1;
		String sImageView2;
		String sImageView3;

		sImageView1 = String.format(Locale.getDefault(),"product_details_%d_a", index);
		sImageView2 = String.format(Locale.getDefault(),"product_details_%d_b", index);
		sImageView3 = String.format(Locale.getDefault(),"product_details_%d_c", index);

		iImageView1 = getResources().getIdentifier(sImageView1, "drawable",getPackageName());
		iImageView2 = getResources().getIdentifier(sImageView2, "drawable",getPackageName());
		iImageView3 = getResources().getIdentifier(sImageView3, "drawable",getPackageName());

		if (iImageView1 != 0) {
			iv_buy_1.setImageResource(iImageView1);
		}
		if (iImageView2 != 0) {
			iv_buy_2.setImageResource(iImageView2);
		}
		if (iImageView3 != 0) {
			iv_buy_3.setImageResource(iImageView3);
		}
	}

	@Override
	protected void onDestroy() {
		//释放长图资源避免OOM
		iv_buy_1.setImageDrawable(null);
		iv_buy_2.setImageDrawable(null);
		iv_buy_3.setImageDrawable(null);
		System.gc();
		super.onDestroy();
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
		ChatClient.getInstance().register(account, userPwd, new Callback() {
			@Override
			public void onSuccess() {
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
							ToastHelper.show(getBaseContext(), R.string.network_unavailable);
						}else if (errorCode == Error.USER_ALREADY_EXIST){
							ToastHelper.show(getBaseContext(), R.string.user_already_exists);
						}else if(errorCode == Error.USER_AUTHENTICATION_FAILED){
							ToastHelper.show(getBaseContext(), R.string.no_register_authority);
						} else if (errorCode == Error.USER_ILLEGAL_ARGUMENT){
							ToastHelper.show(getBaseContext(), R.string.illegal_user_name);
						}else {
							ToastHelper.show(getBaseContext(), R.string.register_user_fail);
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
			progressDialog = new ProgressDialog(this);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
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
			if (isFinishing()){
				return;
			}
			progressDialog.show();
		}
		// login huanxin server
		ChatClient.getInstance().login(uname, upwd, new Callback() {
			@Override
			public void onSuccess() {
				if (!progressShow) {
					return;
				}
				toChatActivity();
			}

			@Override
			public void onError(int code, String error) {
				if (!progressShow) {
					return;
				}
				runOnUiThread(new Runnable() {
					public void run() {
						progressDialog.dismiss();
						ToastHelper.show(getBaseContext(), R.string.is_contact_customer_failure_seconed);
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
				if (!isFinishing())
					progressDialog.dismiss();

				// 获取华为 HMS 推送 token
				HMSPushHelper.getInstance().getHMSToken(ShopDetailsActivity.this);
				AgoraMessage.newAgoraMessage().setCurrentChatUsername(Preferences.getInstance().getCustomerAccount());
				//CallVideoActivity.callingRequest(ShopDetailsActivity.this, Preferences.getInstance().getCustomerAccount());
				VECKitCalling.callingRequest(ShopDetailsActivity.this, Preferences.getInstance().getCustomerAccount());
			}
		});
	}
}
