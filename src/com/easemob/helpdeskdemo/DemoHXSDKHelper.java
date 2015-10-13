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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.text.TextUtils;

import com.easemob.EMCallBack;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.applib.model.HXNotifier;
import com.easemob.applib.model.HXNotifier.HXNotificationInfoProvider;
import com.easemob.applib.model.HXSDKModel;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.OnMessageNotifyListener;
import com.easemob.exceptions.EaseMobException;
import com.easemob.helpdeskdemo.activity.ChatActivity;
import com.easemob.helpdeskdemo.domain.User;
import com.easemob.helpdeskdemo.utils.CommonUtils;
import com.easemob.util.EasyUtils;

/**
 * Demo UI HX SDK helper class which subclass HXSDKHelper
 * @author easemob
 *
 */
public class DemoHXSDKHelper extends HXSDKHelper{

	/**
	 * EMEventListener
	 */
	protected EMEventListener eventListener = null;
	
	
    /**
     * contact list in cache
     */
    private Map<String, User> contactList;
    
    private List<Activity> activityList = new ArrayList<Activity>();
    
    public void pushActivity(Activity activity){
    	if(!activityList.contains(activity)){
    		activityList.add(0, activity);
    	}
    }
    
    public void popActivity(Activity activity){
    	if(activityList.contains(activity)){
    		activityList.remove(activity);
    	}
    }
    
    @Override
    protected void initHXOptions(){
        super.initHXOptions();
        // you can also get EMChatOptions to set related SDK options
        // EMChatOptions options = EMChatManager.getInstance().getChatOptions();
    }

    @Override
    protected OnMessageNotifyListener getMessageNotifyListener(){
        // 取消注释，app在后台，有新消息来时，状态栏的消息提示换成自己写的
      return new OnMessageNotifyListener() {

          @Override
          public String onNewMessageNotify(EMMessage message) {
              // 设置状态栏的消息提示，可以根据message的类型做相应提示
              String ticker = CommonUtils.getMessageDigest(message, appContext);
              if(message.getType() == Type.TXT)
                  ticker = ticker.replaceAll("\\[.{2,3}\\]", "["+R.string.attach_smile+"]");
              return message.getFrom() + ": " + ticker;
          }

          @Override
          public String onLatestMessageNotify(EMMessage message, int fromUsersNum, int messageNum) {
              return null;
             // return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
          }

          @Override
          public String onSetNotificationTitle(EMMessage message) {
              //修改标题,这里使用默认
              return null;
          }

          @Override
          public int onSetSmallIcon(EMMessage message) {
              //设置小图标
              return 0;
          }
      };
    }
    
    @Override
    protected void initListener(){
        super.initListener();
        
        //注册消息事件监听
        initEventListener();
    }

    /**
     * 全局事件监听
     * 因为可能会有UI页面先处理到这个消息，所以一般如果UI页面已经处理，这里就不需要再次处理
     * activityList.size() <= 0 意味着所有页面都已经在后台运行，或者已经离开Activity Stack
     */
    private void initEventListener() {
    	eventListener = new EMEventListener() {
    		private BroadcastReceiver broadCastReceiver = null;
			@Override
			public void onEvent(EMNotifierEvent event) {
				 EMMessage message = null;
	                if(event.getData() instanceof EMMessage){
	                    message = (EMMessage)event.getData();
	                }
	                
	                switch (event.getEvent()) {
	                case EventNewMessage:
	                    //应用在后台，不需要刷新UI,通知栏提示新消息
	                    if(activityList.size() <= 0){
	                        HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
	                    }
	                    break;
	                case EventOfflineMessage:
	                    if(activityList.size() <= 0){
	                        List<EMMessage> messages = (List<EMMessage>) event.getData();
	                        HXSDKHelper.getInstance().getNotifier().onNewMesg(messages);
	                    }
	                    break;
	                // below is just giving a example to show a cmd toast, the app should not follow this
	                // so be careful of this
	                case EventNewCMDMessage:
	                {
	                    //TODO
	                    
	                    break;
	                }
	                case EventDeliveryAck:
	                    message.setDelivered(true);
	                    break;
	                case EventReadAck:
	                    message.setAcked(true);
	                    break;
	                // add other events in case you are interested in
	                default:
	                    break;
	                }
	                
				
			}
		};
		EMChatManager.getInstance().registerEventListener(eventListener);
	}
    
    
    /**
     * 自定义通知栏提示内容
     * @return
     */
    @Override
    protected HXNotificationInfoProvider getNotificationListener() {
        //可以覆盖默认的设置
        return new HXNotificationInfoProvider() {
            
            @Override
            public String getTitle(EMMessage message) {
              //修改标题,这里使用默认
                return null;
            }
            
            @Override
            public int getSmallIcon(EMMessage message) {
              //设置小图标，这里为默认
                return 0;
            }
            
            @Override
            public String getDisplayedText(EMMessage message) {
                // 设置状态栏的消息提示，可以根据message的类型做相应提示
                String ticker = CommonUtils.getMessageDigest(message, appContext);
                if(message.getType() == Type.TXT){
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }
                return message.getFrom() + ": " + ticker;
            }
            
            @Override
            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                return null;
                // return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
            }
            
            @Override
            public Intent getLaunchIntent(EMMessage message) {
                //设置点击通知栏跳转事件
                Intent intent = new Intent(appContext, ChatActivity.class);
                    ChatType chatType = message.getChatType();
                    if (chatType == ChatType.Chat) { // 单聊信息
                        intent.putExtra("userId", message.getFrom());
                        intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
                    } else { // 群聊信息
                        // message.getTo()为群聊id
                        intent.putExtra("groupId", message.getTo());
                        if(chatType == ChatType.GroupChat){
                            intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                        }
                }
                return intent;
            }
        };
    }

    @Override
    public HXNotifier createNotifier(){
        return new HXNotifier(){
            public synchronized void onNewMsg(final EMMessage message) {
                if(EMChatManager.getInstance().isSlientMessage(message)){
                    return;
                }
                
                String chatUsename = null;
                // 获取设置的不提示新消息的用户或者群组ids
                if (message.getChatType() == ChatType.Chat) {
                    chatUsename = message.getFrom();
                } else {
                    chatUsename = message.getTo();
                }
                // 判断app是否在后台
                if (!EasyUtils.isAppRunningForeground(appContext)) {
                    sendNotification(message, false);
                } else {
                    sendNotification(message, true);
                }
                viberateAndPlayTone(message);
            }
        };
    }
    
    
    
	@Override
    protected HXSDKModel createModel() {
        return new DemoHXSDKModel(appContext);
    }
    
    /**
     * get demo HX SDK Model
     */
    public DemoHXSDKModel getModel(){
        return (DemoHXSDKModel) hxModel;
    }
    
    /**
     * 获取内存中好友user list
     *
     * @return
     */
    public Map<String, User> getContactList() {
        if (getHXId() != null && contactList == null) {
            contactList = ((DemoHXSDKModel) getModel()).getContactList();
        }
        
        return contactList;
    }

    /**
     * 设置好友user list到内存中
     *
     * @param contactList
     */
    public void setContactList(Map<String, User> contactList) {
        this.contactList = contactList;
    }
    
    @Override
    public void logout(final EMCallBack callback){
        super.logout(new EMCallBack(){

            @Override
            public void onSuccess() {
                setContactList(null);
                getModel().closeDB();
                if(callback != null){
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(int code, String message) {
                
            }

            @Override
            public void onProgress(int progress, String status) {
                if(callback != null){
                    callback.onProgress(progress, status);
                }
            }
            
        });
    }
    
    public boolean isRobotMenuMessage(EMMessage message) {

		try {
			JSONObject jsonObj = message.getJSONObjectAttribute(Constant.MESSAGE_ATTR_ROBOT_MSGTYPE);
			if (jsonObj.has("choice")) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}
	
	public String getRobotMenuMessageDigest(EMMessage message) {
		String title = "";
		try {
			JSONObject jsonObj = message.getJSONObjectAttribute(Constant.MESSAGE_ATTR_ROBOT_MSGTYPE);
			if (jsonObj.has("choice")) {
				JSONObject jsonChoice = jsonObj.getJSONObject("choice");
				title = jsonChoice.getString("title");
			}
		} catch (Exception e) {
		}
		return title;
	}
	
	//it is evaluation message
	public boolean isEvalMessage(EMMessage message){
		try {
			JSONObject jsonObj = message.getJSONObjectAttribute(Constant.WEICHAT_MSG);
			if(jsonObj.has("ctrlType")){
				try {
					String type = jsonObj.getString("ctrlType");
					if(!TextUtils.isEmpty(type)&&(type.equalsIgnoreCase("inviteEnquiry")||type.equalsIgnoreCase("enquiry"))){
						return true;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (EaseMobException e) {
		}
		return false;
	}
}
