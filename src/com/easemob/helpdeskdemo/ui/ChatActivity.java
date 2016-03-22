package com.easemob.helpdeskdemo.ui;

import android.content.Intent;
import android.os.Bundle;

import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.Preferences;
import com.easemob.helpdeskdemo.R;
import com.hyphenate.helpdesk.ui.BaseChatActivity;
import com.hyphenate.helpdesk.ui.ChatFragment;

public class ChatActivity extends BaseChatActivity {

	public static ChatActivity activityInstance;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.em_activity_chat);
		activityInstance = this;
		// 聊天人或群id
		toChatUsername = Preferences.getInstance().getCustomerAccount();
		// 可以直接new EaseChatFratFragment使用
		chatFragment = new ChatFragment();
		Intent intent = getIntent();
		intent.putExtra(Constant.EXTRA_USER_ID, toChatUsername);
		// 传入参数
		chatFragment.setArguments(intent.getExtras());
		getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		activityInstance = null;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		// 点击notification bar进入聊天页面，保证只有一个聊天页面
		String username = intent.getStringExtra("userId");
		if (toChatUsername.equals(username))
			super.onNewIntent(intent);
		else {
			finish();
			startActivity(intent);
		}

	}
}
