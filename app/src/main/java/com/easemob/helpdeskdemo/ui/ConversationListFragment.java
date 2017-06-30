package com.easemob.helpdeskdemo.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.easemob.helpdeskdemo.MessageHelper;
import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.utils.GlideCircleTransform;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.Conversation;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.chat.OfficialAccount;
import com.hyphenate.helpdesk.easeui.util.IntentBuilder;
import com.hyphenate.helpdesk.easeui.util.SmileUtils;
import com.hyphenate.util.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by liyuzhao on 07/06/2017.
 */

public class ConversationListFragment extends Fragment {

	private ConversationAdapter adapter;
	private final List<Conversation> conversationList = new ArrayList<>();

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.em_chat_fragment, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ListView mListView = (ListView) getView().findViewById(R.id.listview);

		mListView.setAdapter(adapter = new ConversationAdapter(getContext(), 1, conversationList));
		loadConversationList();
		adapter.notifyDataSetChanged();
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Conversation conversation = (Conversation) parent.getItemAtPosition(position);
				// 进入主页面
				Intent intent = new IntentBuilder(getContext())
						.setTargetClass(ChatActivity.class)
						.setVisitorInfo(MessageHelper.createVisitorInfo())
						.setServiceIMNumber(conversation.conversationId())
						.setTitleName(conversation.getOfficialAccount().getName())
						.setShowUserNick(true)
						.build();
				startActivity(intent);

			}
		});

	}


	class ConversationAdapter extends ArrayAdapter<Conversation> {

		 ConversationAdapter(Context context, int resource, List<Conversation> objects) {
			super(context, resource, objects);
		}

		@NonNull
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null){
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.em_row_conversation, null);
				viewHolder = new ViewHolder();
				viewHolder.ivAvatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
				viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
				viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
				viewHolder.tvMessage = (TextView) convertView.findViewById(R.id.tv_message);
				viewHolder.tvUnreadCount = (TextView) convertView.findViewById(R.id.tv_unread);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}

			Conversation conversation = getItem(position);
			if (conversation == null){
				return convertView;
			}
			if (conversation.getUnreadMsgCount() > 0){
				viewHolder.tvUnreadCount.setVisibility(View.VISIBLE);
				viewHolder.tvUnreadCount.setText(String.valueOf(conversation.getUnreadMsgCount()));
			}else{
				viewHolder.tvUnreadCount.setVisibility(View.GONE);
			}
			Message lastMessage = conversation.getLastMessage();

			if (lastMessage != null){
				if (lastMessage.getType() == Message.Type.TXT){
					EMTextMessageBody body = (EMTextMessageBody)lastMessage.getBody();
					viewHolder.tvMessage.setText(SmileUtils.getSmiledText(getContext(), body.getMessage()));
				}else if (lastMessage.getType() == Message.Type.IMAGE){
					viewHolder.tvMessage.setText("[图片]");
				}else if (lastMessage.getType() == Message.Type.VOICE){
					viewHolder.tvMessage.setText("[语音]");
				}else if (lastMessage.getType() == Message.Type.VIDEO){
					viewHolder.tvMessage.setText("[视频]");
				}else if (lastMessage.getType() == Message.Type.LOCATION){
					viewHolder.tvMessage.setText("[位置]");
				}else if (lastMessage.getType() == Message.Type.FILE){
					viewHolder.tvMessage.setText("[文件]");
				}
				viewHolder.tvTime.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));

			}else{
				viewHolder.tvMessage.setText("");
			}
			OfficialAccount officialAccount = conversation.getOfficialAccount();
			if (officialAccount == null){
				return convertView;
			}
			viewHolder.tvName.setText(officialAccount.getName());
			String imgUrl = officialAccount.getImg();
			if (imgUrl != null && imgUrl.startsWith("//")){
				imgUrl = "http:" + imgUrl;
			}
			Glide.with(getContext()).load(imgUrl).error(R.drawable.hd_default_avatar).transform(new GlideCircleTransform(getContext())).into(viewHolder.ivAvatar);
			return convertView;
		}

		class ViewHolder{
			ImageView ivAvatar;
			TextView tvName;
			TextView tvTime;
			TextView tvMessage;
			TextView tvUnreadCount;
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		refresh();
	}

	public void refresh(){
		loadConversationList();
		adapter.notifyDataSetChanged();

	}

	private void loadConversationList(){
		Hashtable<String, Conversation> allConversations =
				ChatClient.getInstance().chatManager().getAllConversations();
		synchronized (conversationList){
			conversationList.clear();
			for (String conversationId : allConversations.keySet()){
				Conversation item = allConversations.get(conversationId);
				if (item.getOfficialAccount() != null){
					conversationList.add(item);
				}
			}
		}



	}



}
