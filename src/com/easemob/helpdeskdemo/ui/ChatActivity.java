package com.easemob.helpdeskdemo.ui;

import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.MessageHelper;
import com.hyphenate.helpdesk.ChatClient;
import com.hyphenate.helpdesk.message.Message;
import com.hyphenate.helpdesk.ui.entities.BaseChatActivity;

import android.content.Intent;
import android.os.Bundle;

public class ChatActivity extends BaseChatActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

	}
	
	@Override
	protected void onStart() {
	    super.onStart();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		// 点击notification bar进入聊天页面，保证只有一个聊天页面
		String username = intent.getStringExtra(com.hyphenate.helpdesk.ui.Constant.EXTRA_USER_ID);
		if (toChatUsername.equals(username))
			super.onNewIntent(intent);
		else {
			finish();
			startActivity(intent);
		}

	}
	
	protected void onReady() {
	    setupUserInfo();
	    sendOrderOrTrack();		
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
