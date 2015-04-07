package com.easemob.helpdeskdemo.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.easemob.helpdeskdemo.R;

public class FirstActivity extends FragmentActivity {

	private ShopFragment shopFragment;
	private SettingFragment settingFragment;
	private Button[] mRadioButtons;
	private Fragment[] fragments;
	private int index;
	private int currentTabIndex;
	private ImageButton imageButton_shop, imageButton_setting;
	private LinearLayout ll_shop, ll_setting;
	String currentUsername;
	String currentPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first);
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
		FragmentTransaction trx = getSupportFragmentManager()
				.beginTransaction();
		trx.add(R.id.fragment_container, shopFragment);
		trx.commit();
		mRadioButtons[0].setSelected(true);
		imageButton_shop.setImageResource(R.drawable.image_shop_click);
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
					.setImageResource(R.drawable.image_setting_unclick);
			imageButton_shop.setImageResource(R.drawable.image_shop_click);
			index = 0;
			break;
		case R.id.imageButton_shop:
			mRadioButtons[0].setTextColor(csl);
			mRadioButtons[1].setTextColor(Color.GRAY);
			imageButton_setting
					.setImageResource(R.drawable.image_setting_unclick);
			imageButton_shop.setImageResource(R.drawable.image_shop_click);
			index = 0;
			break;
		case R.id.main_btn_setting_page:
			imageButton_shop.setImageResource(R.drawable.image_shop_unclick);
			imageButton_setting
					.setImageResource(R.drawable.image_setting_click);
			mRadioButtons[0].setTextColor(Color.GRAY);
			mRadioButtons[1].setTextColor(csl);
			index = 1;
			break;
		case R.id.imageButton_setting:
			imageButton_shop.setImageResource(R.drawable.image_shop_unclick);
			imageButton_setting
					.setImageResource(R.drawable.image_setting_click);
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
			intent.setClass(FirstActivity.this, LoginActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_first, menu);
		return true;
	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		// this.finish();
//		return super.onKeyDown(keyCode, event);
//	}

	// /**
	// * 登录
	// *
	// * @param view
	// */
	// public void login(View view) {
	// SharedPreferences sharedPreferences = getSharedPreferences("shared",
	// Context.MODE_PRIVATE);
	// if (!CommonUtils.isNetWorkConnected(this)) {
	// Toast.makeText(this, R.string.network_isnot_available,
	// Toast.LENGTH_SHORT).show();
	// return;
	// }
	// currentUsername = sharedPreferences.getString("name", "");
	// currentPassword = sharedPreferences.getString("pwd", "");
	// Intent intent = new Intent(FirstActivity.this,
	// com.easemob.helpdeskdemo.activity.AlertDialog.class);
	// intent.putExtra("editTextShow", true);
	// intent.putExtra("titleIsCancel", true);
	// intent.putExtra("msg", "应用被kill掉后重新登录");
	// intent.putExtra("edit_text", currentUsername);
	// startActivityForResult(intent, 1);
	// }

	// @Override
	// protected void onActivityResult(int arg0, int arg1, Intent arg2) {
	// // TODO Auto-generated method stub
	// super.onActivityResult(arg0, arg1, arg2);
	// if (arg1 == 1) {
	// DemoApplication.currentUserNick = arg2.getStringExtra("edittext");
	//
	// progressShow = true;
	// final ProgressDialog pd = new ProgressDialog(FirstActivity.this);
	// pd.setCanceledOnTouchOutside(false);
	// pd.setOnCancelListener(new OnCancelListener() {
	//
	// @Override
	// public void onCancel(DialogInterface dialog) {
	// progressShow = false;
	// }
	// });
	// pd.setMessage("正在登陆");
	// pd.show();
	//
	// final long start = System.currentTimeMillis();
	// // 调用sdk登陆方法登陆聊天服务器
	// EMChatManager.getInstance().login(currentUsername, currentPassword,
	// new EMCallBack() {
	//
	// @Override
	// public void onSuccess() {
	// // umeng自定义事件，开发者可以把这个删掉
	// // loginSuccess2Umeng(start);
	//
	// if (!progressShow) {
	// return;
	// }
	// // 登陆成功，保存用户名密码
	// DemoApplication.getInstance().setUserName(
	// currentUsername);
	// DemoApplication.getInstance().setPassword(
	// currentPassword);
	// runOnUiThread(new Runnable() {
	// public void run() {
	// pd.setMessage("稍等");
	// }
	// });
	// try {
	// // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
	// // ** manually load all local groups and
	// // conversations in case we are auto login
	// EMGroupManager.getInstance().loadAllGroups();
	// EMChatManager.getInstance()
	// .loadAllConversations();
	// // 处理好友和群组
	// } catch (Exception e) {
	// e.printStackTrace();
	// // 取好友或者群聊失败，不让进入主页面
	// runOnUiThread(new Runnable() {
	// public void run() {
	// pd.dismiss();
	// DemoApplication.getInstance().logout(
	// null);
	// Toast.makeText(getApplicationContext(),
	// "登陆失败",
	// 1).show();
	// }
	// });
	// return;
	// }
	// // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
	// boolean updatenick = EMChatManager.getInstance()
	// .updateCurrentUserNick(
	// DemoApplication.currentUserNick
	// .trim());
	// if (!updatenick) {
	// Log.e("LoginActivity",
	// "update current user nick fail");
	// }
	// if (!FirstActivity.this.isFinishing())
	// pd.dismiss();
	// // 进入主页面
	// startActivity(new Intent(FirstActivity.this,
	// ChatActivity.class).putExtra("userId", "customers"));
	// finish();
	// }
	//
	// @Override
	// public void onProgress(int progress, String status) {
	// }
	//
	// @Override
	// public void onError(final int code, final String message) {
	// if (!progressShow) {
	// return;
	// }
	// runOnUiThread(new Runnable() {
	// public void run() {
	// pd.dismiss();
	// Toast.makeText(
	// getApplicationContext(),
	// "登陆失败"
	// + message,
	// Toast.LENGTH_SHORT).show();
	// }
	// });
	// }
	// });
	//
	// }
	// }

}
