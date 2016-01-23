package com.easemob.helpdeskdemo.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.easeui.ui.EaseChatFragment;
import com.easemob.easeui.widget.chatrow.EaseChatRow;
import com.easemob.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.easemob.easeui.widget.emojicon.EaseEmojiconMenu;
import com.easemob.easeuix.widget.chatrow.ChatRowEvaluation;
import com.easemob.easeuix.widget.chatrow.ChatRowPictureText;
import com.easemob.easeuix.widget.chatrow.ChatRowRobotMenu;
import com.easemob.easeuix.widget.chatrow.ChatRowTransferToKefu;
import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.DemoHelper;
import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.domain.EmojiconExampleGroupData;
import com.easemob.helpdeskdemo.domain.MessageHelper;
import com.easemob.helpdeskdemo.utils.HelpDeskPreferenceUtils;

import org.json.JSONException;
import org.json.JSONObject;


public class ChatFragment extends EaseChatFragment implements EaseChatFragment.EaseChatFragmentListener {

	// 避免和基类定义的常量可能发生的冲突，常量从11开始定义
	private static final int ITEM_FILE = 11;
	private static final int ITEM_SHORT_CUT_MESSAGE = 12;
	public static final int REQUEST_CODE_CONTEXT_MENU = 14;

	private static final int MESSAGE_TYPE_SENT_PICTURE_TXT = 1;
	private static final int MESSAGE_TYPE_RECV_PICTURE_TXT = 2;
	private static final int MESSAGE_TYPE_SENT_ROBOT_MENU = 3;
	private static final int MESSAGE_TYPE_RECV_ROBOT_MENU = 4;

	// evaluation
	private static final int MESSAGE_TYPE_SENT_EVAL = 5;
	private static final int MESSAGE_TYPE_RECV_EVAL = 6;

	// transfer to kefu message
	private static final int MESSAGE_TYPE_SENT_TRANSFER_TO_KEFU = 7;
	private static final int MESSAGE_TYPE_RECV_TRANSFER_TO_KEFU = 8;


	private static final int REQUEST_CODE_SELECT_FILE = 11;
	//EVALUATION
	public static final int REQUEST_CODE_EVAL = 26;
	//SHORT CUT MESSAGES
	public static final int REQUEST_CODE_SHORTCUT = 27;

	//从详情进来的，发送轨迹跟踪
	private int imgSelectedIndex = Constant.INTENT_CODE_IMG_SELECTED_DEFAULT;
	protected int messageToIndex = Constant.MESSAGE_TO_DEFAULT;

	protected String currentUserNick;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		//在父类中调用了initView和setUpView两个方法
		super.onActivityCreated(savedInstanceState);
		//检查是否是从某个商品详情进来
		imgSelectedIndex = fragmentArgs.getInt(Constant.INTENT_CODE_IMG_SELECTED_KEY, Constant.INTENT_CODE_IMG_SELECTED_DEFAULT);
		//判断是默认，还是用技能组（售前、售后）
		messageToIndex = fragmentArgs.getInt(Constant.MESSAGE_TO_INTENT_EXTRA, Constant.MESSAGE_TO_DEFAULT);
		currentUserNick = HelpDeskPreferenceUtils.getInstance(getActivity()).getSettingCurrentNick();

//		//从商品详情进来都为售后，只为演示用。
//		if (imgSelectedIndex != Constant.INTENT_CODE_IMG_SELECTED_DEFAULT) {
//			messageToIndex = Constant.MESSAGE_TO_AFTER_SALES;
//		}
		if (savedInstanceState == null) {
			sendPictureTxtMessage(imgSelectedIndex);
		}
		messageList.setShowUserNick(true);
	}

	@Override
	protected void setUpView() {
		setChatFragmentListener(this);
		super.setUpView();
		//注释这句，大表情消失，可自定义，为演示添加自己的表情
		((EaseEmojiconMenu)inputMenu.getEmojiconMenu()).addEmojiconGroup(EmojiconExampleGroupData.getData());
	}

	@Override
	protected void registerExtendMenuItem() {
		// demo这里不覆盖基类已经注册的item,item点击listener沿用基类的
		super.registerExtendMenuItem();
		//增加扩展item
		inputMenu.registerExtendMenuItem(R.string.attach_file, R.drawable.em_chat_file_selector, ITEM_FILE, extendMenuItemClickListener);
		// 增加扩展item
		inputMenu.registerExtendMenuItem(R.string.attach_short_cut_message, R.drawable.em_icon_answer, ITEM_SHORT_CUT_MESSAGE, extendMenuItemClickListener);
	}

	@SuppressLint("NewApi")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
			switch (resultCode) {
			case ContextMenuActivity.RESULT_CODE_COPY: // 复制消息
				clipboard.setText(((TextMessageBody) contextMenuMessage.getBody()).getMessage());
				break;
			case ContextMenuActivity.RESULT_CODE_DELETE: // 删除消息
				conversation.removeMessage(contextMenuMessage.getMsgId());
				messageList.refresh();
				break;
			default:
				break;
			}
		}
		if(resultCode == Activity.RESULT_OK){
			if(requestCode == REQUEST_CODE_EVAL){
				messageList.refresh();
			}else if(requestCode == REQUEST_CODE_SHORTCUT){
				String content = data.getStringExtra("content");
				if(!TextUtils.isEmpty(content)){
					inputMenu.setInputMessage(content);
				}
			}else if(requestCode == REQUEST_CODE_SELECT_FILE){
				if (data != null) {
					Uri uri = data.getData();
					if (uri != null) {
						sendFileByUri(uri);
					}
				}
			}
		}

	}

	@Override
	public void onSetMessageAttributes(EMMessage message) {
		// 设置消息扩展属性
		
		//设置用户信息（昵称，qq等）
		setUserInfoAttribute(message);
		
		
		//设置VisitorInfo 传递的信息将在iframe中显示
//		setVisitorInfoSrc(message);
		//指向某个技能组，技能组（客服分组）内将自动分配客服
//		pointToSkillGroup(message, groupName);
		switch (messageToIndex) {
		case Constant.MESSAGE_TO_PRE_SALES:
			pointToSkillGroup(message, "shouqian");
			break;
		case Constant.MESSAGE_TO_AFTER_SALES:
			pointToSkillGroup(message, "shouhou");
			break;
		default:
			break;
		}
		
		//指向某个客服 , 当会话同时指定了客服和技能组时，以指定客服为准，指定技能组失效。
//		pointToAgentUser(message, "ceshia@qq.com");
	}

	@Override
	public void onEnterToChatDetails() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAvatarClick(String username) {
		// 头像点击事件
//		Intent intent = new Intent(getActivity(), UserProfileActivity.class);
//		intent.putExtra("username", username);
//		startActivity(intent);
	}

	@Override
	public boolean onMessageBubbleClick(EMMessage message) {
		// 消息框点击事件，demo这里不做覆盖，如需覆盖，return true
		return false;
	}

	@Override
	public void onMessageBubbleLongClick(EMMessage message) {
		// 消息框长按
		startActivityForResult((new Intent(getActivity(), ContextMenuActivity.class)).putExtra("message", message),
				REQUEST_CODE_CONTEXT_MENU);
	}

	@Override
	public boolean onExtendMenuItemClick(int itemId, View view) {
		switch(itemId){
			case ITEM_FILE:
				//一般文件
				//demo这里是通过系统api选择文件，实际app中最好是做成qq那种选择发送文件
				selectFileFromLocal();
				break;
			case ITEM_SHORT_CUT_MESSAGE:
				Intent intent = new Intent(getActivity(), ShortCutMsgActivity.class);
				startActivityForResult(intent, REQUEST_CODE_SHORTCUT);
				getActivity().overridePendingTransition(R.anim.activity_open, 0);
				break;

			default:break;
		}
		//不覆盖已有的点击事件
		return false;
	}

	/**
	 * 选择文件
	 * @return
	 */
	protected void selectFileFromLocal(){
		Intent intent = null;
		if (Build.VERSION.SDK_INT < 19) { //19以后这个api不可用，demo这里简单处理成图库选择图片
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("*/*");
			intent.addCategory(Intent.CATEGORY_OPENABLE);

		} else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
	}

	@Override
	public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
		// 设置自定义listview item提供者
		return new CustomChatRowProvider();
	}

	/**
	 * chat row provider
	 * 
	 */
	private final class CustomChatRowProvider implements EaseCustomChatRowProvider {
		@Override
		public int getCustomChatRowTypeCount() {
			//此处返回的数目为getCustomChatRowType 中的布局的个数
			return 8;
		}

		@Override
		public int getCustomChatRowType(EMMessage message) {
			if (message.getType() == EMMessage.Type.TXT) {
				if (DemoHelper.getInstance().isRobotMenuMessage(message)) {
					// 机器人 列表菜单
					return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_ROBOT_MENU
							: MESSAGE_TYPE_SENT_ROBOT_MENU;
				} else if (DemoHelper.getInstance().isEvalMessage(message)) {
					// 满意度评价
					return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_EVAL : MESSAGE_TYPE_SENT_EVAL;
				} else if (DemoHelper.getInstance().isPictureTxtMessage(message)) {
					// 订单图文组合
					return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_PICTURE_TXT
							: MESSAGE_TYPE_SENT_PICTURE_TXT;
				} else if(DemoHelper.getInstance().isTransferToKefuMsg(message)){
					//转人工消息
					return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TRANSFER_TO_KEFU
							: MESSAGE_TYPE_SENT_TRANSFER_TO_KEFU;
				}
			}
			return 0;
		}

		@Override
		public EaseChatRow getCustomChatRow(EMMessage message, int position, BaseAdapter adapter) {
			if (message.getType() == EMMessage.Type.TXT) {
				if (DemoHelper.getInstance().isRobotMenuMessage(message)) {
					return new ChatRowRobotMenu(getActivity(), message, position, adapter);
				} else if (DemoHelper.getInstance().isEvalMessage(message)) {
					return new ChatRowEvaluation(getActivity(), message, position, adapter);
				} else if (DemoHelper.getInstance().isPictureTxtMessage(message)) {
					return new ChatRowPictureText(getActivity(), message, position, adapter);
				}else if (DemoHelper.getInstance().isTransferToKefuMsg(message)){
					return new ChatRowTransferToKefu(getActivity(), message, position, adapter);
				}
			}
			return null;
		}
	}


	/**
	 * 设置用户的属性，
	 * 通过消息的扩展，传递客服系统用户的属性信息
	 * @param message
	 */
	private void setUserInfoAttribute(EMMessage message) {
		if (TextUtils.isEmpty(currentUserNick)) {
			currentUserNick = EMChatManager.getInstance().getCurrentUser();
		}
		JSONObject weichatJson = getWeichatJSONObject(message);
		try {
			JSONObject visitorJson = new JSONObject();
			visitorJson.put("userNickname", currentUserNick);
			visitorJson.put("trueName", currentUserNick);
			visitorJson.put("qq", "10000");
			visitorJson.put("phone", "13512345678");
			visitorJson.put("companyName", "环信");
			visitorJson.put("description", "");
			visitorJson.put("email", "abc@123.com");
			weichatJson.put("visitor", visitorJson);

			message.setAttribute("weichat", weichatJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void setVisitorInfoSrc(EMMessage message){
		//传递用户的属性到自定义的iframe界面
		String strName = "name-test from hxid:" + EMChatManager.getInstance().getCurrentUser();
		JSONObject cmdJson = new JSONObject();
		try {
			JSONObject updateVisitorInfosrcJson = new JSONObject();
			JSONObject paramsJson = new JSONObject();
			paramsJson.put("name", strName);
			updateVisitorInfosrcJson.put("params", paramsJson);
			cmdJson.put("updateVisitorInfoSrc", updateVisitorInfosrcJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		message.setAttribute("cmd", cmdJson);
	}
	
	/**
	 * 获取消息中的扩展 weichat是否存在并返回jsonObject
	 * @param message
	 * @return
	 */
	private JSONObject getWeichatJSONObject(EMMessage message){
		JSONObject weichatJson = null;
		try {
			String weichatString = message.getStringAttribute("weichat", null);
			if(weichatString == null){
				weichatJson = new JSONObject();
			}else{
				weichatJson = new JSONObject(weichatString);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return weichatJson;
	}
	
	/**
	 * 指向某个具体客服，
	 * @param message 消息
	 * @param agentUsername 客服的登录账号
	 */
	private void pointToAgentUser(EMMessage message,String agentUsername){
		try {
			JSONObject weichatJson = getWeichatJSONObject(message);
			weichatJson.put("agentUsername", agentUsername);
			message.setAttribute("weichat", weichatJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 技能组（客服分组）发消息发到某个组
	 * @param message 消息
	 * @param groupName 分组名称
	 */
	private void pointToSkillGroup(EMMessage message,String groupName){
		try {
			JSONObject weichatJson = getWeichatJSONObject(message);
			weichatJson.put("queueName", groupName);
			message.setAttribute("weichat", weichatJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 演示功能：
	 * <p/>
	 * 从商品详情界面进入会话，自动发一条订单或轨迹消息。
	 *
	 * @param selectedImgIndex 选中的某个商品，用户要按照自己的需求传递。
	 */
	private void sendPictureTxtMessage(int selectedImgIndex) {
		if (selectedImgIndex == Constant.INTENT_CODE_IMG_SELECTED_DEFAULT) {
			return;
		}
		EMMessage message = EMMessage.createTxtSendMessage("客服图文混排消息", toChatUsername);
		JSONObject jsonMsgType = MessageHelper.getMessageExtFromPicture(selectedImgIndex);
		imgSelectedIndex = Constant.INTENT_CODE_IMG_SELECTED_DEFAULT;
		if (jsonMsgType != null) {
			message.setAttribute("msgtype", jsonMsgType);
			sendMessage(message);
		}
	}

	public void sendRobotMessage(String content, String menuId) {
		EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
		if (!TextUtils.isEmpty(menuId)) {
			JSONObject msgTypeJson = new JSONObject();
			try {
				JSONObject choiceJson = new JSONObject();
				choiceJson.put("menuid", menuId);
				msgTypeJson.put("choice", choiceJson);
			} catch (Exception e) {
			}
			message.setAttribute("msgtype", msgTypeJson);
		}
		sendMessage(message);
	}
}
