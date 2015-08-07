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

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.applib.utils.HelpDeskPreferenceUtils;
import com.easemob.chat.EMChat;
import com.easemob.helpdeskdemo.R;

public class SettingFragment extends Fragment implements OnClickListener {
	
	private RelativeLayout rlAppkey;
	private RelativeLayout rlAccount;
	private TextView tvAppkey;
	private TextView tvAccount;
	private static final int REQUEST_CODE_APPKEY = 1;
	private static final int REQUEST_CODE_ACCOUNT = 2;
	public static final String INTENT_KEY_MODIFY_APPKEY = "modify_appkey";
	public static final String INTENT_KEY_MODIFY_ACCOUNT = "modify_account";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.setting_fragment, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initListener();
	}
	private void initView() {
		tvAppkey = (TextView) getView().findViewById(R.id.tv_setting_appkey);
		tvAccount = (TextView) getView().findViewById(R.id.tv_setting_account);
		rlAppkey = (RelativeLayout) getView().findViewById(R.id.ll_setting_list_appkey);
		rlAccount = (RelativeLayout) getView().findViewById(R.id.ll_setting_list_account);
	}

	private void initListener() {
		tvAppkey.setText(HelpDeskPreferenceUtils.getInstance(getActivity()).getSettingCustomerAppkey());
		tvAccount.setText(HelpDeskPreferenceUtils.getInstance(getActivity()).getSettingCustomerAccount());
		rlAppkey.setOnClickListener(this);
		rlAccount.setOnClickListener(this);
	}

	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK){
			switch (requestCode) {
			case REQUEST_CODE_APPKEY:
				String oldAppkey = tvAppkey.getText().toString();
				String newAppkey = data.getStringExtra(INTENT_KEY_MODIFY_APPKEY);
				if(oldAppkey.equals(newAppkey)){
					return;
				}
				tvAppkey.setText(newAppkey);
				showCustomMessage(newAppkey);
				break;
			case REQUEST_CODE_ACCOUNT:
				String oldAccount = tvAccount.getText().toString();
				String newAccount = data.getStringExtra(INTENT_KEY_MODIFY_ACCOUNT);
				if(oldAccount.equals(newAccount)){
					return;
				}
				tvAccount.setText(newAccount);
				HelpDeskPreferenceUtils.getInstance(getActivity()).setSettingCustomerAccount(newAccount);
				break;
			default:
				break;
			}
		}
	}

	private void showCustomMessage(final String newAppkey) {
		final Dialog lDialog = new Dialog(getActivity(), R.style.MyAlertDialog);
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.r_okcanceldialogview, null);
		lDialog.setContentView(view);
		Button btnOk = (Button) view.findViewById(R.id.ok);
		Button btnCancel = (Button) view.findViewById(R.id.cancel);
		btnOk.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						lDialog.dismiss();
						HelpDeskPreferenceUtils.getInstance(getActivity()).setSettingCustomerAppkey(newAppkey);
						EMChat.getInstance().setAppkey(newAppkey);
						// 退出登录
						HXSDKHelper.getInstance().logout(null);
						getActivity().finish();
					}
				});
		btnCancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						lDialog.dismiss();
					}
				});
		lDialog.show();
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.ll_setting_list_appkey:
			String strAppkey = tvAppkey.getText().toString();
			intent.setClass(getActivity(), ModifiedAppkeyActivity.class);
			intent.putExtra(INTENT_KEY_MODIFY_APPKEY, strAppkey);
			startActivityForResult(intent, REQUEST_CODE_APPKEY);
			break;
		case R.id.ll_setting_list_account:
			String strAccount = tvAccount.getText().toString();
			intent.setClass(getActivity(), ModifiedCustomerActivity.class);
			intent.putExtra(INTENT_KEY_MODIFY_ACCOUNT, strAccount);
			startActivityForResult(intent, REQUEST_CODE_ACCOUNT);
			break;
		default:
			break;
		}
		
	}

}
