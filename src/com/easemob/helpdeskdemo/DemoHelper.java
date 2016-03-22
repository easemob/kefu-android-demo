package com.easemob.helpdeskdemo;

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMMessage.Type;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.easeui.model.EaseNotifier.EaseNotificationInfoProvider;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.exceptions.HyphenateException;
import com.easemob.helpdeskdemo.ui.MainActivity;
import com.hyphenate.util.EMLog;

public class DemoHelper {

    protected static final String TAG = DemoHelper.class.getSimpleName();
    
	private EaseUI easeUI;
	
    protected EMMessageListener messageListener = null;

	private Map<String, EaseUser> contactList;

	private static DemoHelper instance = null;
	
	
    private boolean alreadyNotified = false;
	
	public boolean isVoiceCalling;
    public boolean isVideoCalling;

	private String username;

    private Context appContext;

    private EMConnectionListener connectionListener;

	private DemoHelper() {
	}

	 /**
     * 全局事件监听
     * 因为可能会有UI页面先处理到这个消息，所以一般如果UI页面已经处理，这里就不需要再次处理
     * activityList.size() <= 0 意味着所有页面都已经在后台运行，或者已经离开Activity Stack
     */
    protected void registerEventListener() {
        messageListener = new EMMessageListener() {
            private BroadcastReceiver broadCastReceiver = null;

			@Override
			public void onMessageReceived(List<EMMessage> messages) {
				
                    EMLog.d(TAG, "receive the message : ");
                    if(!easeUI.hasForegroundActivies()){
                        getNotifier().onNewMesg(messages);
                    }
			}

			@Override
			public void onCmdMessageReceived(List<EMMessage> messages) {
				EMLog.d(TAG, "收到透传消息");
				for(EMMessage message : messages){
                    //获取消息body
                    EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
                    final String action = cmdMsgBody.action();//获取自定义action
                    
                    //获取扩展属性 此处省略
                    //message.getStringAttribute("");
                    EMLog.d(TAG, String.format("透传消息：action:%s,message:%s", action,message.toString()));
                    String str = null;
                    //final String str = appContext.getString(R.string.receive_the_passthrough);
                    
                    final String CMD_TOAST_BROADCAST = "easemob.demo.cmd.toast";
                    IntentFilter cmdFilter = new IntentFilter(CMD_TOAST_BROADCAST);
                    
                    if(broadCastReceiver == null){
                        broadCastReceiver = new BroadcastReceiver(){

                            @Override
                            public void onReceive(Context context, Intent intent) {
                                // TODO Auto-generated method stub
                                Toast.makeText(appContext, intent.getStringExtra("cmd_value"), Toast.LENGTH_SHORT).show();
                            }
                        };
                        
                      //注册广播接收者
                        appContext.registerReceiver(broadCastReceiver,cmdFilter);
                    }

                    Intent broadcastIntent = new Intent(CMD_TOAST_BROADCAST);
                    broadcastIntent.putExtra("cmd_value", str+action);
                    appContext.sendBroadcast(broadcastIntent, null);
				}
				
			}


			@Override
			public void onMessageReadAckReceived(List<EMMessage> messages) {
			    for(EMMessage message : messages)
			    	message.setAcked(true);
			}


			@Override
			public void onMessageDeliveryAckReceived(List<EMMessage> messages) {
			    for(EMMessage message : messages)
			    	message.setDelivered(true);
			}


			@Override
			public void onMessageChanged(EMMessage message, Object change) {
			}
        };
        
        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }


	/**
	 * 退出登录
	 * 
	 * @param unbindDeviceToken
	 *            是否解绑设备token(使用GCM才有)
	 * @param callback
	 *            callback
	 */
	public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
		EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

			@Override
			public void onSuccess() {
				if (callback != null) {
					callback.onSuccess();
				}

			}

			@Override
			public void onProgress(int progress, String status) {
				if (callback != null) {
					callback.onProgress(progress, status);
				}
			}

			@Override
			public void onError(int code, String error) {
				if (callback != null) {
					callback.onError(code, error);
				}
			}
		});
	}
	
	/**
	 * 获取消息通知类
	 * @return
	 */
	public EaseNotifier getNotifier(){
	    return easeUI.getNotifier();
	}
	
	 
	public synchronized void notifyForRecevingEvents(){
        if(alreadyNotified){
            return;
        }
        
        alreadyNotified = true;
    }
	
    public void pushActivity(Activity activity) {
        easeUI.pushActivity(activity);
    }

    public void popActivity(Activity activity) {
        easeUI.popActivity(activity);
    }
    
    public boolean isRobotMenuMessage(EMMessage message){
    	try {
			JSONObject jsonObj = message.getJSONObjectAttribute("");//Constant.MESSAGE_ATTR_MSGTYPE);
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
			JSONObject jsonObj = message.getJSONObjectAttribute("");//(Constant.MESSAGE_ATTR_MSGTYPE);
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
			JSONObject jsonObj = message.getJSONObjectAttribute("");//Constant.WEICHAT_MSG);
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
		} catch (HyphenateException e) {
		}
		return false;
	}
    
    /**
     * 检测是否为订单消息或者为轨迹消息
     * @param message
     * @return
     */
    public boolean isPictureTxtMessage(EMMessage message){
    	JSONObject jsonObj = null;
    	try {
			jsonObj = message.getJSONObjectAttribute("");//Constant.MESSAGE_ATTR_MSGTYPE);
		} catch (HyphenateException e) {
		}
    	if(jsonObj == null){
			return false;
		}
		if(jsonObj.has("order") || jsonObj.has("track")){
			return true;
		}
		return false;
    }
    
}
