package com.easemob.helpdeskdemo.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.easeui.widget.chatrow.EaseChatRow;
import com.easemob.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.easemob.easeuix.ui.EaseChatFragmentX;
import com.easemob.easeuix.ui.EaseChatFragmentX.EaseChatFragmentListener;
import com.easemob.easeuix.widget.chatrow.ChatRowEvaluation;
import com.easemob.easeuix.widget.chatrow.ChatRowPictureText;
import com.easemob.easeuix.widget.chatrow.ChatRowRobotMenu;
import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.DemoHelper;


public class ChatFragment extends EaseChatFragmentX implements EaseChatFragmentListener {

	// 避免和基类定义的常量可能发生的冲突，常量从11开始定义

	public static final int REQUEST_CODE_CONTEXT_MENU = 14;

	private static final int MESSAGE_TYPE_SENT_PICTURE_TXT = 1;
	private static final int MESSAGE_TYPE_RECV_PICTURE_TXT = 2;
	private static final int MESSAGE_TYPE_SENT_ROBOT_MENU = 3;
	private static final int MESSAGE_TYPE_RECV_ROBOT_MENU = 4;

	// evaluation
	private static final int MESSAGE_TYPE_SENT_EVAL = 5;
	private static final int MESSAGE_TYPE_RECV_EVAL = 6;
	
	//EVALUATION
	public static final int REQUEST_CODE_EVAL = 26;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected void setUpView() {
		setChatFragmentListener(this);
		super.setUpView();
	}

	@Override
	protected void registerExtendMenuItem() {
		// demo这里不覆盖基类已经注册的item,item点击listener沿用基类的
		super.registerExtendMenuItem();
		// 增加扩展item
		// inputMenu.registerExtendMenuItem(R.string.attach_video,
		// R.drawable.em_chat_video_selector, ITEM_VIDEO,
		// extendMenuItemClickListener);
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
		// TODO Auto-generated method stub
		return false;
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
			return 6;
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
				}
			}
			return null;
		}
	}

	@Override
	public boolean onExtendPromptItemClick(int itemId, View view) {
		return false;
	}

	private void setUserInfoAttribute(EMMessage message) {
		if(TextUtils.isEmpty(currentUserNick)){
			currentUserNick = EMChatManager.getInstance().getCurrentUser();
		}
		message.setAttribute("weichat", setWeChatUserInfo(currentUserNick, "10000", "13512345678", "环信", currentUserNick, "", "abc@123.com"));
	}
	
	private void setVisitorInfoSrc(EMMessage message){
		String strName = "name-test from hxid:" + EMChatManager.getInstance().getCurrentUser();
		message.setAttribute("cmd", updateVisitorInfoSrc(strName));
	}
	private JSONObject setWeChatUserInfo(String trueName, String qq, String phone, String companyName,
			String userNickname, String description, String email) {
		JSONObject weiJson = new JSONObject();
		try {
			JSONObject visitorJson = new JSONObject();
			if (trueName != null)
				visitorJson.put("trueName", trueName);
			if (qq != null)
				visitorJson.put("qq", qq);
			if (phone != null)
				visitorJson.put("phone", phone);
			if (companyName != null)
				visitorJson.put("companyName", companyName);
			if (userNickname != null)
				visitorJson.put("userNickname", userNickname);
			if (description != null)
				visitorJson.put("description", description);
			if (email != null)
				visitorJson.put("email", email);
			weiJson.put("visitor", visitorJson);
			
			//=============Set SkillGroup start=================
			switch (messageToIndex) {
			case Constant.MESSAGE_TO_PRE_SALES:
				weiJson.put("queueName", "shouqian");
				break;
			case Constant.MESSAGE_TO_AFTER_SALES:
				weiJson.put("queueName", "shouhou");
				break;
			default:
				break;
			}
			//=============Set SkillGroup end====================
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return weiJson;
	}
	
	private JSONObject updateVisitorInfoSrc(String name){
		JSONObject cmdJson = new JSONObject();
		try {
			JSONObject updateVisitorInfosrcJson = new JSONObject();
			JSONObject paramsJson = new JSONObject();
			if(name != null){
				paramsJson.put("name", name);
			}
			updateVisitorInfosrcJson.put("params", paramsJson);
			cmdJson.put("updateVisitorInfoSrc", updateVisitorInfosrcJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return cmdJson;
	}
	
	
	
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
	
}
