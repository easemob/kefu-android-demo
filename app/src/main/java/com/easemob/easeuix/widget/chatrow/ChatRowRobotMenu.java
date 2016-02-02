package com.easemob.easeuix.widget.chatrow;

import android.content.Context;
import android.text.Spannable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.TextMessageBody;
import com.easemob.easeui.utils.EaseSmileUtils;
import com.easemob.easeui.widget.chatrow.EaseChatRow;
import com.easemob.exceptions.EaseMobException;
import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.DemoHelper;
import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.ui.ChatActivity;
import com.easemob.util.DensityUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChatRowRobotMenu extends EaseChatRow {

	TextView tvTitle;
	LinearLayout tvList;
	private TextView contentView;

	public ChatRowRobotMenu(Context context, EMMessage message, int position, BaseAdapter adapter) {
		super(context, message, position, adapter);
	}

	@Override
	protected void onInflatView() {
		if (DemoHelper.getInstance().isRobotMenuMessage(message)) {
			inflater.inflate(message.direct == EMMessage.Direct.RECEIVE ? R.layout.em_row_received_menu
					: R.layout.ease_row_sent_message, this);
		}
	}

	@Override
	protected void onFindViewById() {
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvList = (LinearLayout) findViewById(R.id.ll_layout);
		contentView = (TextView) findViewById(R.id.tv_chatcontent);
	}

	@Override
	protected void onUpdateView() {
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onSetUpView() {
		if (message.direct == EMMessage.Direct.RECEIVE) {
			try {
				JSONObject jsonObj = message.getJSONObjectAttribute(Constant.MESSAGE_ATTR_MSGTYPE);
				if (jsonObj.has("choice")) {
					JSONObject jsonChoice = jsonObj.getJSONObject("choice");
					if (jsonChoice.has("title")) {
						String title = jsonChoice.getString("title");
						tvTitle.setText(title);
					}
					if (jsonChoice.has("items")) {
						setRobotMenuListMessageLayout(tvList, jsonChoice.getJSONArray("items"));
					}else if(jsonChoice.has("list")){
						setRobotMenuMessagesLayout(tvList, jsonChoice.getJSONArray("list"));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			TextMessageBody txtBody = (TextMessageBody) message.getBody();
			Spannable span = EaseSmileUtils.getSmiledText(context, txtBody.getMessage());
			// 设置内容
			contentView.setText(span, BufferType.SPANNABLE);
			handleTextMessage();
		}

	}

	protected void handleTextMessage() {
		if (message.direct == EMMessage.Direct.SEND) {
			setMessageSendCallback();
			switch (message.status) {
			case CREATE:
				progressBar.setVisibility(View.VISIBLE);
				statusView.setVisibility(View.GONE);
				break;
			case SUCCESS: // 发送成功
				progressBar.setVisibility(View.GONE);
				statusView.setVisibility(View.GONE);
				break;
			case FAIL: // 发送失败
				progressBar.setVisibility(View.GONE);
				statusView.setVisibility(View.VISIBLE);
				break;
			case INPROGRESS: // 发送中
				progressBar.setVisibility(View.VISIBLE);
				statusView.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		} else {
			if (!message.isAcked() && message.getChatType() == ChatType.Chat) {
				try {
					EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
					message.isAcked = true;
				} catch (EaseMobException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void onBubbleClick() {

	}

	private void setRobotMenuMessagesLayout(LinearLayout parentView, JSONArray jsonArr){
		try {
			parentView.removeAllViews();
			for (int i = 0; i < jsonArr.length(); i++) {
				final String itemStr = jsonArr.getString(i);
				final TextView textView = new TextView(context);
				textView.setText(itemStr);
				textView.setTextSize(15);
				textView.setTextColor(getResources().getColorStateList(R.color.em_menu_msg_text_color));
				textView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						((ChatActivity) context).sendRobotMessage(itemStr, null);
					}
				});
				LayoutParams llLp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				llLp.bottomMargin = DensityUtil.dip2px(context, 3);
				llLp.topMargin = DensityUtil.dip2px(context, 3);
				parentView.addView(textView, llLp);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void setRobotMenuListMessageLayout(LinearLayout parentView, JSONArray jsonArr) {
		try {
			parentView.removeAllViews();
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONObject itemJson = jsonArr.getJSONObject(i);
				final String itemStr = itemJson.getString("name");
				final String itemId = itemJson.getString("id");
				final TextView textView = new TextView(context);
				textView.setText(itemStr);
				textView.setTextSize(15);
				textView.setTextColor(getResources().getColorStateList(R.color.em_menu_msg_text_color));
				textView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						((ChatActivity) context).sendRobotMessage(itemStr, itemId);
					}
				});
				LayoutParams llLp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				llLp.bottomMargin = DensityUtil.dip2px(context, 3);
				llLp.topMargin = DensityUtil.dip2px(context, 3);
				parentView.addView(textView, llLp);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
