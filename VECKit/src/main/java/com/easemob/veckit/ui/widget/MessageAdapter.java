package com.easemob.veckit.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.easemob.veckit.ui.widget.row.ChatRowArticle;
import com.easemob.veckit.ui.widget.row.ChatRowBigExpression;
import com.easemob.veckit.ui.widget.row.ChatRowCustomEmoji;
import com.easemob.veckit.ui.widget.row.ChatRowFile;
import com.easemob.veckit.ui.widget.row.ChatRowImage;
import com.easemob.veckit.ui.widget.row.ChatRowRobotMenu;
import com.easemob.veckit.ui.widget.row.ChatRowText;
import com.easemob.veckit.ui.widget.row.ChatRowTextCommentInvite;
import com.easemob.veckit.ui.widget.row.ChatRowTransferGuideMenu;
import com.easemob.veckit.ui.widget.row.ChatRowTransferToKefu;
import com.easemob.veckit.ui.widget.row.ChatRowVideo;
import com.easemob.veckit.ui.widget.row.ChatRowVoice;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.Conversation;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.model.MessageHelper;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

public class MessageAdapter extends BaseAdapter {
	private final static String TAG = "msg";

	private Context context;
	
	private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;
	private static final int HANDLER_MESSAGE_SELECT_LAST = 1;
    private static final int HANDLER_MESSAGE_SEEK_TO = 2;

	private static final int MESSAGE_TYPE_RECV_TXT = 0;
	private static final int MESSAGE_TYPE_SENT_TXT = 1;
	private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
	private static final int MESSAGE_TYPE_RECV_IMAGE = 3;
	private static final int MESSAGE_TYPE_SENT_VOICE = 4;
	private static final int MESSAGE_TYPE_RECV_VOICE = 5;
	private static final int MESSAGE_TYPE_SENT_VIDEO = 6;
	private static final int MESSAGE_TYPE_RECV_VIDEO = 7;
	private static final int MESSAGE_TYPE_SENT_FILE = 8;
	private static final int MESSAGE_TYPE_RECV_FILE = 9;
	private static final int MESSAGE_TYPE_SENT_EXPRESSION = 10;
	private static final int MESSAGE_TYPE_RECV_EXPRESSION = 11;
	private static final int MESSAGE_TYPE_RECV_EVALUATION = 12;
	private static final int MESSAGE_TYPE_RECV_ROBOT_MENU =13;
	private static final int MESSAGE_TYPE_SENT_TRANSFER_TO_KEFU = 14;
	private static final int MESSAGE_TYPE_RECV_TRANSFER_TO_KEFU = 15;
	private static final int MESSAGE_TYPE_RECV_ARTICLES = 16;
	private static final int MESSAGE_TYPE_RECV_CUSTOMEMOJI = 17;
	private static final int MESSAGE_TYPE_SENT_CUSTOMEMOJI = 18;
	private static final int MESSAGE_TYPE_COMMENT_INVITE = 19;
	private static final int MESSAGE_TYPE_RECV_HTML_WEBVIEW = 20;
	private static final int MESSAGE_TYPE_SENT_HTML_WEBVIEW = 21;


	private static final int MESSAGE_TYPE_COUNT = 20;
	
	
	// reference to conversation object in chatsdk
	private Conversation conversation;
	Message[] messages = null;
	
    private String toChatUsername;

    private MessageList.MessageListItemClickListener itemClickListener;
    private CustomChatRowProvider customRowProvider;
    
    private boolean showUserNick;
    private boolean showAvatar;
    private Drawable myBubbleBg;
    private Drawable otherBuddleBg;
	public View animView;
	public View currentPlayView;
    private ListView listView;
	public int mMinItemWidth;
	public int mMaxItemWidth;

	/**
	 * 弱引用刷新UI
	 */
	private WeakHandler handler;

	private static class WeakHandler extends android.os.Handler {
		WeakReference<MessageAdapter> weakReference;

		public WeakHandler(MessageAdapter adapter) {
			this.weakReference = new WeakReference<MessageAdapter>(adapter);
		}

		private void refreshList() {
			MessageAdapter messageAdapter = weakReference.get();
			if (messageAdapter != null && messageAdapter.conversation != null) {
				List<Message> list = messageAdapter.conversation.getAllMessages();
				synchronized (list) {
					Collections.sort(list);
				}
				messageAdapter.messages = list.toArray(new Message[list.size()]);
				messageAdapter.conversation.markAllMessagesAsRead();
				messageAdapter.notifyDataSetChanged();
			}
		}

		private void selectLast() {
			MessageAdapter messageAdapter = weakReference.get();
			if (messageAdapter != null && messageAdapter.messages != null) {
				if (messageAdapter.messages.length > 0) {
					messageAdapter.listView.setSelection(messageAdapter.messages.length - 1);
				}
			}
		}

		private void seekTo(int position) {
			MessageAdapter messageAdapter = weakReference.get();
			if (messageAdapter != null && messageAdapter.listView != null) {
				messageAdapter.listView.setSelection(position);
			}
		}

		@Override
		public void handleMessage(android.os.Message message) {
			switch (message.what) {
				case HANDLER_MESSAGE_REFRESH_LIST:
					refreshList();
					break;
				case HANDLER_MESSAGE_SELECT_LAST:
					selectLast();
					break;
				case HANDLER_MESSAGE_SEEK_TO:
					int position = message.arg1;
					seekTo(position);
					break;
				default:
					break;
			}
		}
	}

	public MessageAdapter(Context context, String username, ListView listView) {
		this.context = context;
		this.listView = listView;
		toChatUsername = username;
		this.conversation = ChatClient.getInstance().chatManager().getConversation(username);
		handler = new WeakHandler(this);

		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		assert wm != null;
		wm.getDefaultDisplay().getMetrics(displayMetrics);
		mMaxItemWidth = (int)(displayMetrics.widthPixels * 0.4f);
		mMinItemWidth = (int)(displayMetrics.widthPixels * 0.15f);
	}

	/**
	 * 刷新页面
	 */
	public void refresh() {
		if (handler.hasMessages(HANDLER_MESSAGE_REFRESH_LIST)) {
			return;
		}
		android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST);
		handler.sendMessage(msg);
	}
	
	/**
     * 刷新页面, 选择最后一个
     */
    public void refreshSelectLast() {
        // avoid refresh too frequently when receiving large amount offline messages
        final int TIME_DELAY_REFRESH_SELECT_LAST = 100;
        handler.removeMessages(HANDLER_MESSAGE_REFRESH_LIST);
        handler.removeMessages(HANDLER_MESSAGE_SELECT_LAST);
        handler.sendEmptyMessageDelayed(HANDLER_MESSAGE_REFRESH_LIST, TIME_DELAY_REFRESH_SELECT_LAST);
        handler.sendEmptyMessageDelayed(HANDLER_MESSAGE_SELECT_LAST, TIME_DELAY_REFRESH_SELECT_LAST);
    }
    
    /**
     * 刷新页面, 选择Position
     */
    public void refreshSeekTo(int position) {
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_SEEK_TO);
        msg.arg1 = position;
        handler.sendMessage(msg);
    }
	

	public Message getItem(int position) {
		if (messages != null && position < messages.length) {
			return messages[position];
		}
		return null;
	}

	public long getItemId(int position) {
		return position;
	}
	
	/**
     * 获取item数
     */
    public int getCount() {
        return messages == null ? 0 : messages.length;
    }
	
	/**
	 * 获取item类型数
	 */
	public int getViewTypeCount() {
	    if(customRowProvider != null && customRowProvider.getCustomChatRowTypeCount() > 0){
	        return customRowProvider.getCustomChatRowTypeCount() + MESSAGE_TYPE_COUNT;
	    }
        return MESSAGE_TYPE_COUNT;
    }
	

	/**
	 * 获取item类型
	 */
	public int getItemViewType(int position) {
		Message message = getItem(position);
		if (message == null) {
			return -1;
		}

		if(customRowProvider != null && customRowProvider.getCustomChatRowType(message) > 0){
		    return customRowProvider.getCustomChatRowType(message) + MESSAGE_TYPE_COUNT;
		}
		
		if (message.getType() == Message.Type.TXT) {
			switch (MessageHelper.getMessageExtType(message)) {
				case RobotMenuMsg:
				case TransferGuideMenuMsg:
					//机器人列表菜单
					return MESSAGE_TYPE_RECV_ROBOT_MENU;
				case ArticlesMsg:
					//图文消息
					return MESSAGE_TYPE_RECV_ARTICLES;
				case ToCustomServiceMsg:
					//转人工消息
					return message.direct() == Message.Direct.RECEIVE ?
							MESSAGE_TYPE_RECV_TRANSFER_TO_KEFU : MESSAGE_TYPE_SENT_TRANSFER_TO_KEFU;
				case BigExpressionMsg:
					//大表情消息
					return message.direct() == Message.Direct.RECEIVE ?
							MESSAGE_TYPE_RECV_EXPRESSION : MESSAGE_TYPE_SENT_EXPRESSION;
				case CustomEmojiMsg:
					return message.direct() == Message.Direct.RECEIVE ?
							MESSAGE_TYPE_RECV_CUSTOMEMOJI : MESSAGE_TYPE_SENT_CUSTOMEMOJI;
				case RobotCommentInviteMsg:
					return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_COMMENT_INVITE: MESSAGE_TYPE_COMMENT_INVITE; // ONLY HAVE RECEIVED TYPE
				case RichText:
					return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_HTML_WEBVIEW : MESSAGE_TYPE_SENT_HTML_WEBVIEW;
				default:
					return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
			}
		}
		if (message.getType() == Message.Type.IMAGE) {
			return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE : MESSAGE_TYPE_SENT_IMAGE;
		}
		if (message.getType() == Message.Type.VOICE) {
			return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE : MESSAGE_TYPE_SENT_VOICE;
		}
		if (message.getType() == Message.Type.VIDEO) {
			return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO : MESSAGE_TYPE_SENT_VIDEO;
		}
		if (message.getType() == Message.Type.FILE) {
			return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_FILE : MESSAGE_TYPE_SENT_FILE;
		}

		return -1;// invalid
	}

	protected ChatRow createChatRow(Context context, Message message, int position) {
		ChatRow chatRow = null;
        if(customRowProvider != null && customRowProvider.getCustomChatRow(message, position, this) != null){
            return customRowProvider.getCustomChatRow(message, position, this);
        }
        switch (message.getType()) {
        case TXT:
	        switch (MessageHelper.getMessageExtType(message)){
				case TransferGuideMenuMsg:
					chatRow = new ChatRowTransferGuideMenu(context, message, position, this);
					break;
		        case RobotMenuMsg:
			        chatRow = new ChatRowRobotMenu(context, message, position, this);
			        break;
				case RobotCommentInviteMsg:
					chatRow = new ChatRowTextCommentInvite(context, message, position, this);
					break;
		        case ArticlesMsg:
			        chatRow = new ChatRowArticle(context, message, position, this);
			        break;
		        case ToCustomServiceMsg:
		        	chatRow = new ChatRowTransferToKefu(context, message, position, this);
		        	break;
		        case BigExpressionMsg:
			        chatRow = new ChatRowBigExpression(context, message, position, this);
			        break;
		        case CustomEmojiMsg:
			        chatRow = new ChatRowCustomEmoji(context, message, position, this);
			        break;
		        default:
			        chatRow = new ChatRowText(context, message, position, this);
	        }
            break;
        case FILE:
            chatRow = new ChatRowFile(context, message, position, this);
            break;
        case IMAGE:
            chatRow = new ChatRowImage(context, message, position, this);
            break;
        case VOICE:
            chatRow = new ChatRowVoice(context, message, position, this);
            break;
        case VIDEO:
            chatRow = new ChatRowVideo(context, message, position, this);
            break;
        default:
            break;
        }

        return chatRow;
    }
    

	@SuppressLint("NewApi")
	public View getView(final int position, View convertView, ViewGroup parent) {
		Message message = getItem(position);
		if(convertView == null ){
			convertView = createChatRow(context, message, position);
		}

		//缓存的view的message很可能不是当前item的，传入当前message和position更新ui
		((ChatRow)convertView).setUpView(message, position, itemClickListener);
		
		return convertView;
	}

	public String getToChatUsername(){
	    return toChatUsername;
	}
	
	
	
	public void setShowUserNick(boolean showUserNick) {
        this.showUserNick = showUserNick;
    }


    public void setShowAvatar(boolean showAvatar) {
        this.showAvatar = showAvatar;
    }


    public void setMyBubbleBg(Drawable myBubbleBg) {
        this.myBubbleBg = myBubbleBg;
    }


    public void setOtherBuddleBg(Drawable otherBuddleBg) {
        this.otherBuddleBg = otherBuddleBg;
    }


    public void setItemClickListener(MessageList.MessageListItemClickListener listener){
	    itemClickListener = listener;
	}
	
	public void setCustomChatRowProvider(CustomChatRowProvider rowProvider){
	    customRowProvider = rowProvider;
	}


    public boolean isShowUserNick() {
        return showUserNick;
    }


    public boolean isShowAvatar() {
        return showAvatar;
    }


    public Drawable getMyBubbleBg() {
        return myBubbleBg;
    }


    public Drawable getOtherBuddleBg() {
        return otherBuddleBg;
    }
	
	
}
