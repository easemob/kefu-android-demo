package com.easemob.helpdeskdemo.ui;

import android.content.Intent;
import android.os.Bundle;

import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.MessageHelper;
import com.easemob.helpdeskdemo.Preferences;
import com.easemob.helpdeskdemo.R;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.helpdesk.ChatClient;
import com.hyphenate.helpdesk.message.Message;
import com.hyphenate.helpdesk.ui.Arguments;
import com.hyphenate.helpdesk.ui.BaseChatActivity;
import com.hyphenate.helpdesk.ui.ChatFragment;

public class ChatActivity extends BaseChatActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

	}
	
	@Override
	protected void onStart() {
	    super.onStart();
	    setupUserInfo();
	    sendOrderOrTrack();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		// 点击notification bar进入聊天页面，保证只有一个聊天页面
		String username = intent.getStringExtra(Arguments.EXTRA_USER_ID);
		if (toChatUsername.equals(username))
			super.onNewIntent(intent);
		else {
			finish();
			startActivity(intent);
		}

	}
	
	private void setupUserInfo() {
		Bundle args = getIntent().getExtras();
		//判断是默认，还是用技能组（售前、售后）
		int messageToIndex = args.getInt(Constant.MESSAGE_TO_INTENT_EXTRA, Constant.MESSAGE_TO_DEFAULT);
		Message message = Message.createMessage();
		message.setTo(getToChatUsername());
		//specify agent name or queue name, todo
		
		message.addContent(MessageHelper.createVisitorInfo());
		ChatClient.getInstance().getChat().sendMessage(message);
	}
	
	private void sendOrderOrTrack() {
		Bundle args = getIntent().getExtras();
		//检查是否是从某个商品详情进来
		int    index = args.getInt(Constant.INTENT_CODE_IMG_SELECTED_KEY, Constant.INTENT_CODE_IMG_SELECTED_DEFAULT);

		Message message = Message.createMessage();
		message.setTo(getToChatUsername());
		if(index > 3)
		    message.addContent(MessageHelper.createOrderInfo(index));
		else
			message.addContent(MessageHelper.createVisitorTrack(index));
		ChatClient.getInstance().getChat().sendMessage(message);
	}
}
