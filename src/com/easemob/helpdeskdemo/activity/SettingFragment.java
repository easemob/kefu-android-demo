package com.easemob.helpdeskdemo.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatConfig;
import com.easemob.helpdeskdemo.R;

public class SettingFragment extends Fragment {
	private int REQUEST_ONE = 1;
	private int REQUEST_TWO = 2;
	private TextView tv1, tv2;
	SharedPreferences sharedPreFerencesAppkey, sharedPreFerencesCustomer;
	private TextView editTextCustomer,editTextAppKey;
	private String stForAppkey;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.setting_fragment, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		tv1 = (TextView) getView().findViewById(R.id.tv_setting_appkey);
		tv2 = (TextView) getView().findViewById(R.id.tv_setting_zhanghao);
		// 修改appkey
		RelativeLayout rlAppkey = (RelativeLayout) getView().findViewById(
				R.id.ll_setting_list_appkey);
		rlAppkey.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), ModifiedAppkeyActivity.class);
				intent.putExtra("ap", tv1.getText().toString());
				startActivityForResult(intent, REQUEST_ONE);
			}
		});
		// 修改customer
		RelativeLayout rlCustomer = (RelativeLayout) getView().findViewById(
				R.id.ll_setting_list_zhanghao);
		rlCustomer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), ModifiedCustomerActivity.class);
				intent.putExtra("zh", tv2.getText().toString());
				startActivityForResult(intent, REQUEST_TWO);
			}
		});

		sharedPreFerencesAppkey = getActivity().getSharedPreferences(
				"customerappkey", Context.MODE_PRIVATE);
		String strAppkey = sharedPreFerencesAppkey.getString("customerappkey",
				"sipsoft#sandbox");
		TextView etAppkey = (TextView) getActivity().findViewById(R.id.tv_setting_appkey);
		etAppkey.setText(strAppkey);

		sharedPreFerencesCustomer = getActivity().getSharedPreferences("customernumber", Context.MODE_PRIVATE);
		String strcustomer = sharedPreFerencesCustomer.getString("customerkey",
				"yuanhui");
		TextView etCustomer = (TextView) getActivity().findViewById(R.id.tv_setting_zhanghao);
		etCustomer.setText(strcustomer);

		editTextCustomer = (TextView) getActivity().findViewById(R.id.tv_setting_zhanghao);
		editTextAppKey = (TextView) getActivity().findViewById(R.id.tv_setting_appkey);

		ApplicationInfo ai = null;
		String appPackageName = getActivity().getPackageName();
		try {
			ai = getActivity().getPackageManager().getApplicationInfo(
					appPackageName, PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Bundle metaData = ai.metaData;
		String appKey = metaData.getString("EASEMOB_APPKEY");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 1:
			stForAppkey = data.getExtras().getString("forappkey");
			if (stForAppkey.equals(tv1.getText().toString())) {
				return;
			} else {
				showCustomMessage();
			}
			tv1.setText(stForAppkey);
			break;
		case 2:
			String stForZhanghao = data.getExtras().getString("forzhanghao");
			tv2.setText(stForZhanghao);
			//保存账号到share中
			String stCustomer = editTextCustomer
					.getText().toString();
			SharedPreferences sharedPreFerences = getActivity()
					.getSharedPreferences(
							"customernumber",
							Context.MODE_PRIVATE);
			Editor editor = sharedPreFerences
					.edit();
			editor.putString("customerkey",
					stCustomer);
			editor.commit();
			break;
		}
	}

	private void showCustomMessage() {
		Toast.makeText(getActivity(), stForAppkey, 0).show();
		final Dialog lDialog = new Dialog(getActivity(), R.style.MyAlertDialog);
		lDialog.setContentView(R.layout.r_okcanceldialogview);
		((Button) lDialog.findViewById(R.id.ok))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// write your code to do things after users clicks
						SharedPreferences sharedPreFerencesAppKey = getActivity()
								.getSharedPreferences(
										"customerappkey",
										Context.MODE_PRIVATE);
						Editor edit = sharedPreFerencesAppKey
								.edit();
						edit.putString("customerappkey",
								stForAppkey);
						edit.commit();
						SharedPreferences share = getActivity()
								.getSharedPreferences("shared",
										Context.MODE_PRIVATE);
						Editor edi = share.edit();// 获取编辑器
						edi.clear();
						edi.commit();
						EMChatConfig.getInstance().setAppKey(stForAppkey);
						// 退出登录
						HXSDKHelper.getInstance().logout(
								new EMCallBack() {
									@Override
									public void onSuccess() {
									}

									@Override
									public void onProgress(
											int progress,
											String status) {
									}

									@Override
									public void onError(
											int code,
											String message) {
									}
								});
						// kill掉这个进程
						Process.killProcess(android.os.Process
								.myPid());
						lDialog.dismiss();
					}
				});
		((Button) lDialog.findViewById(R.id.cancel))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// write your code to do things after users clicks OK
						lDialog.dismiss();
					}
				});
		lDialog.show();
	}

}
