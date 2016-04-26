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
package com.easemob.helpdeskdemo;

import java.util.List;

import com.easemob.helpdeskdemo.ui.ChatActivity;
import com.hyphenate.chat.EMMessage.Type;
import com.hyphenate.helpdesk.Chat.MessageListener;
import com.hyphenate.helpdesk.ChatClient;
import com.hyphenate.helpdesk.message.Message;
import com.hyphenate.helpdesk.ui.Notifier.NotificationInfoProvider;
import com.hyphenate.helpdesk.ui.UIProvider;
import com.hyphenate.helpdesk.ui.util.CommonUtils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

public class DemoApplication extends Application {

	public void onCreate() {
		super.onCreate();
		Preferences.init(this);
		ChatClient.Options options = ChatClient.getInstance().createOptions();
		options.setAppkey(Preferences.getInstance().getAppKey());
		
		ChatClient.getInstance().getChat().addMessageListener(new MessageListener() {

			@Override
			public void onMessage(List<Message> msgs) {
				if(!ChatActivity.isActive())
				    UIProvider.getInstance().getNotifier().onNewMesg(msgs);
			}

			@Override
			public void onCmdMessage(List<Message> msg) {
				// TODO Auto-generated method stub
				
			}
			
		});
        ChatClient.getInstance().init((Context)this, options);
		UIProvider.getInstance().init((Context)this);
		UIProvider.getInstance().getNotifier().setNotificationInfoProvider(new NotificationInfoProvider() {
        @Override
        public String getTitle(Message message) {
          //修改标题,这里使用默认
            return null;
        }
        
        @Override
        public int getSmallIcon(Message message) {
          //设置小图标，这里为默认
            return 0;
        }
        
        @Override
        public String getDisplayedText(Message message) {
            // 设置状态栏的消息提示，可以根据message的类型做相应提示
            String ticker = CommonUtils.getMessageDigest(message, DemoApplication.this);
            if(message.getType() == Type.TXT){
                ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
            }
            return message.getFrom() + ": " + ticker;
        }
        
        @Override
        public String getLatestText(Message message, int fromUsersNum, int messageNum) {
            return null;
        }
        
        @Override
        public Intent getLaunchIntent(Message message) {
            //设置点击通知栏跳转事件
            Intent intent = new Intent(DemoApplication.this, ChatActivity.class);
            intent.putExtra("userId", message.getFrom());
            return intent;           
        }
    });
	}
}
