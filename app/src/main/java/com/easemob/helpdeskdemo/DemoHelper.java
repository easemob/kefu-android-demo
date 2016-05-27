package com.easemob.helpdeskdemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.easeui.EaseConstant;
import com.easemob.easeui.R;
import com.easemob.easeui.controller.EaseUI;
import com.easemob.easeui.domain.EaseEmojicon;
import com.easemob.easeui.domain.EaseEmojiconGroupEntity;
import com.easemob.easeui.domain.EaseUser;
import com.easemob.easeui.model.EaseNotifier;
import com.easemob.easeui.model.EaseNotifier.EaseNotificationInfoProvider;
import com.easemob.easeui.utils.EaseCommonUtils;
import com.easemob.easeui.utils.EaseUserUtils;
import com.easemob.exceptions.EaseMobException;
import com.easemob.helpdeskdemo.domain.EmojiconExampleGroupData;
import com.easemob.helpdeskdemo.ui.ChatActivity;
import com.easemob.helpdeskdemo.ui.MainActivity;
import com.easemob.helpdeskdemo.utils.PreferenceManager;
import com.easemob.util.EMLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class DemoHelper {

    protected static final String TAG = DemoHelper.class.getSimpleName();
    
	private EaseUI easeUI;
	
    /**
     * EMEventListener
     */
    protected EMEventListener eventListener = null;

	private Map<String, EaseUser> contactList;

	private static DemoHelper instance = null;
	
	private DemoModel demoModel = null;
	
    private boolean alreadyNotified = false;
	
	public boolean isVoiceCalling;
    public boolean isVideoCalling;

	private String username;

    private Context appContext;

    private EMConnectionListener connectionListener;

	private DemoHelper() {
	}

	public synchronized static DemoHelper getInstance() {
		if (instance == null) {
			instance = new DemoHelper();
		}
		return instance;
	}

	/**
	 * init helper
	 * 
	 * @param context
	 *            application context
	 */
	public void init(Context context) {
		if (EaseUI.getInstance().init(context)) {
		    appContext = context;
            //在小米手机上当app被kill时使用小米推送进行消息提示，SDK已支持，可选
//            EMChatManager.getInstance().setMipushConfig("2882303761517370134", "5131737040134");
		    //设为调试模式，打成正式包时，最好设为false，以免消耗额外的资源
		    EMChat.getInstance().setDebugMode(true);
		    //get easeui instance
		    easeUI = EaseUI.getInstance();
		    //调用easeui的api设置providers
		    setEaseUIProviders();
		    demoModel = new DemoModel(context);
			//初始化PreferenceManager
			PreferenceManager.init(context);
			//设置全局监听
			setGlobalListeners();
//			broadcastManager = LocalBroadcastManager.getInstance(appContext);
		}
	}

    protected void setEaseUIProviders() {
        //设置昵称头像
        //此方法设置需要考虑是发送方还是接收方,
        easeUI.setEaseUserInfoProvider(new EaseUI.EaseUserInfoProvider() {
            @Override
            public void setNickAndAvatar(Context context, EMMessage message, ImageView userAvatarView, TextView usernickView) {
                JSONObject jsonAgent = getAgentInfoByMessage(message);
                if (message.direct == EMMessage.Direct.SEND) {
                    EaseUserUtils.setUserAvatar(context, EMChatManager.getInstance().getCurrentUser(), userAvatarView);
                    //发送方不显示nick
                    //            UserUtils.setUserNick(EMChatManager.getInstance().getCurrentUser(), usernickView);
                } else {
                    if (jsonAgent == null) {
                        userAvatarView.setImageResource(R.drawable.ease_default_avatar);
                        usernickView.setText(message.getFrom());
                    } else {
                        String strNick = null;
                        String strUrl = null;
                        try {
                            strNick = jsonAgent.getString("userNickname");
                            strUrl = jsonAgent.getString("avatar");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //设置客服昵称
                        if (!TextUtils.isEmpty(strNick)) {
                            usernickView.setText(strNick);
                        } else {
                            usernickView.setText(message.getFrom());
                        }
                        //设置客服头像
                        if (!TextUtils.isEmpty(strUrl)) {
                            if (!strUrl.startsWith("http")) {
                                strUrl = "http:" + strUrl;
                            }
                            //正常的string路径
                            Glide.with(context).load(strUrl).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ease_default_avatar).into(userAvatarView);
                        } else {
                            Glide.with(context).load(R.drawable.ease_default_avatar).into(userAvatarView);
                        }
                    }
                }
            }
        });

        //不设置，则使用easeui默认的
        easeUI.getNotifier().setNotificationInfoProvider(new EaseNotificationInfoProvider() {

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
                String ticker = EaseCommonUtils.getMessageDigest(message, appContext);
                if (message.getType() == Type.TXT) {
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
                    intent.putExtra(EaseConstant.EXTRA_USER_ID, message.getFrom());
                    intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_SINGLE);
                    intent.putExtra(EaseConstant.EXTRA_SHOW_USERNICK, true);
                }
                return intent;
            }
        });

        //设置表情provider
        easeUI.setEmojiconInfoProvider(new EaseUI.EaseEmojiconInfoProvider() {
            @Override
            public EaseEmojicon getEmojiconInfo(String emojiconIdentityCode) {
                EaseEmojiconGroupEntity data = EmojiconExampleGroupData.getData();
                for (EaseEmojicon emojicon : data.getEmojiconList()) {
                    if (emojicon.getIdentityCode().equals(emojiconIdentityCode)) {
                        return emojicon;
                    }
                }
                return null;
            }

            @Override
            public Map<String, Object> getTextEmojiconMapping() {
                //返回文字表情emoji文本和图片(resource id或者本地路径)的映射map
                return null;
            }
        });


    }

    /**
     * 设置全局事件监听
     */
    protected void setGlobalListeners(){
        // create the global connection listener
        connectionListener = new EMConnectionListener() {
            @Override
            public void onDisconnected(int error) {
                if (error == EMError.USER_REMOVED) {
                    onCurrentAccountRemoved();
                }else if (error == EMError.CONNECTION_CONFLICT) {
                    onConnectionConflict();
                }
            }

            @Override
            public void onConnected() {
                // in case group and contact were already synced, we supposed to notify sdk we are ready to receive the events
            	DemoHelper.getInstance().notifyForRecevingEvents();
            }
        };
        //注册连接监听
        EMChatManager.getInstance().addConnectionListener(connectionListener);       
        //注册消息事件监听
        registerEventListener();
    }
    
    /**
     * 账号在别的设备登录
     */
    protected void onConnectionConflict(){
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.ACCOUNT_CONFLICT, true);
        appContext.startActivity(intent);
    }
    
    /**
     * 账号被移除
     */
    protected void onCurrentAccountRemoved(){
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.ACCOUNT_REMOVED, true);
        appContext.startActivity(intent);
    }
	
	 /**
     * 全局事件监听
     * 因为可能会有UI页面先处理到这个消息，所以一般如果UI页面已经处理，这里就不需要再次处理
     * activityList.size() <= 0 意味着所有页面都已经在后台运行，或者已经离开Activity Stack
     */
    protected void registerEventListener() {
        eventListener = new EMEventListener() {
            private BroadcastReceiver broadCastReceiver = null;
            
            @Override
            public void onEvent(EMNotifierEvent event) {
                EMMessage message = null;
                if(event.getData() instanceof EMMessage){
                    message = (EMMessage)event.getData();
                    EMLog.d(TAG, "receive the event : " + event.getEvent() + ",id : " + message.getMsgId());
                }
                
                switch (event.getEvent()) {
                case EventNewMessage:
                    //应用在后台，不需要刷新UI,通知栏提示新消息
                    if(!easeUI.hasForegroundActivies()){
                        getNotifier().onNewMsg(message);
                    }
                    break;
                case EventOfflineMessage:
                    if(!easeUI.hasForegroundActivies()){
                        EMLog.d(TAG, "received offline messages");
                        List<EMMessage> messages = (List<EMMessage>) event.getData();
                        getNotifier().onNewMesg(messages);
                    }
                    break;
                // below is just giving a example to show a cmd toast, the app should not follow this
                // so be careful of this
                case EventNewCMDMessage:
                { 
                    
                    EMLog.d(TAG, "收到透传消息");
                    //获取消息body
                    CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
                    final String action = cmdMsgBody.action;//获取自定义action
                    
                    //获取扩展属性 此处省略
                    //message.getStringAttribute("");
                    EMLog.d(TAG, String.format("透传消息：action:%s,message:%s", action,message.toString()));
                    final String str = appContext.getString(R.string.receive_the_passthrough);
                    
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
	 * 是否登录成功过
	 * 
	 * @return
	 */
	public boolean isLoggedIn() {
		return EMChat.getInstance().isLoggedIn();
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
		EMChatManager.getInstance().logout(unbindDeviceToken, new EMCallBack() {

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
	
	public DemoModel getModel(){
        return (DemoModel) demoModel;
    }
	
    
    /**
     * 设置当前用户的环信id
     * @param username
     */
    public void setCurrentUserName(String username){
    	this.username = username;
    	demoModel.setCurrentUserName(username);
    }
    
    /**
     * 设置当前用户的环信密码
     */
    public void setCurrentPassword(String password){
    	demoModel.setCurrentUserPwd(password);
    }
    
    /**
     * 获取当前用户的环信id
     */
    public String getCurrentUsernName(){
    	if(username == null){
    		username = demoModel.getCurrentUsernName();
    	}
    	return username;
    }
	 
	public synchronized void notifyForRecevingEvents(){
        if(alreadyNotified){
            return;
        }
        
        // 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
        EMChat.getInstance().setAppInited();
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
			JSONObject jsonObj = message.getJSONObjectAttribute(Constant.MESSAGE_ATTR_MSGTYPE);
			if (jsonObj.has("choice") && !jsonObj.isNull("choice")) {
                JSONObject jsonChoice = jsonObj.getJSONObject("choice");
                if(jsonChoice.has("items") || jsonChoice.has("list")){
                    return true;
                }
			}
		} catch (Exception e) {
		}
		return false;
    }
    
    public String getRobotMenuMessageDigest(EMMessage message) {
		String title = "";
		try {
			JSONObject jsonObj = message.getJSONObjectAttribute(Constant.MESSAGE_ATTR_MSGTYPE);
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
    
    /**
     * 检测是否为订单消息或者为轨迹消息
     * @param message
     * @return
     */
    public boolean isPictureTxtMessage(EMMessage message){
    	JSONObject jsonObj = null;
    	try {
			jsonObj = message.getJSONObjectAttribute(Constant.MESSAGE_ATTR_MSGTYPE);
		} catch (EaseMobException e) {
		}
    	if(jsonObj == null){
			return false;
		}
		if(jsonObj.has("order") || jsonObj.has("track")){
			return true;
		}
		return false;
    }

    /**
     * 显示客服昵称和头像信息
     * 获取头像和昵称
     * @param message
     * @return
     */
    public JSONObject getAgentInfoByMessage(EMMessage message) {
        try {
            JSONObject jsonWeichat = message.getJSONObjectAttribute(Constant.WEICHAT_MSG);
            if (jsonWeichat == null) {
                return null;
            }
            if (jsonWeichat.has("agent") && !jsonWeichat.isNull("agent")) {
                return jsonWeichat.getJSONObject("agent");
            }
        } catch (EaseMobException e) {
//            e.printStackTrace();
        } catch (JSONException e) {
//            e.printStackTrace();
        }
        return null;
    }

    /**
     * 检测是否为转人工的消息，如果是则需要显示转人工的按钮
     */
    public boolean isTransferToKefuMsg(EMMessage message){
        try {
            JSONObject jsonObj = message.getJSONObjectAttribute(Constant.WEICHAT_MSG);
            if(jsonObj.has("ctrlType")){
                try {
                    String type = jsonObj.getString("ctrlType");
                    if(!TextUtils.isEmpty(type)&&type.equalsIgnoreCase("TransferToKfHint")){
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
