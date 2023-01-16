package com.easemob.helpdeskdemo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.utils.Calling;
import com.easemob.helpdeskdemo.widget.chatrow.ChatRowEvaluation;
import com.easemob.helpdeskdemo.widget.chatrow.ChatRowForm;
import com.easemob.helpdeskdemo.widget.chatrow.ChatRowLocation;
import com.easemob.helpdeskdemo.widget.chatrow.ChatRowOrder;
import com.easemob.helpdeskdemo.widget.chatrow.ChatRowTrack;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.AgoraMessage;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.ChatManager;
import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.chat.VecConfig;
import com.hyphenate.helpdesk.callback.ValueCallBack;
import com.hyphenate.helpdesk.easeui.provider.CustomChatRowProvider;
import com.hyphenate.helpdesk.easeui.recorder.MediaManager;
import com.hyphenate.helpdesk.easeui.ui.ChatFragment;
import com.hyphenate.helpdesk.easeui.util.CommonUtils;
import com.hyphenate.helpdesk.easeui.widget.AlertDialogFragment;
import com.hyphenate.helpdesk.easeui.widget.MessageList;
import com.hyphenate.helpdesk.easeui.widget.ToastHelper;
import com.hyphenate.helpdesk.easeui.widget.chatrow.ChatRow;
import com.hyphenate.helpdesk.model.MessageHelper;
import com.hyphenate.helpdesk.util.Log;
import com.hyphenate.util.EMLog;



public class CustomChatFragment extends ChatFragment implements ChatFragment.EaseChatFragmentListener {

    //避免和基类定义的常量可能发生冲突,常量从11开始定义
    private static final int ITEM_MAP = 11;
    private static final int ITEM_LEAVE_MSG = 12;//ITEM_SHORTCUT = 12;
    private static final int ITEM_VIDEO = 13;
    private static final int ITEM_EVALUATION = 14;

    private static final int REQUEST_CODE_SELECT_MAP = 11;
    private static final int REQUEST_CODE_SHORTCUT = 12;

    public static final int REQUEST_CODE_CONTEXT_MENU = 13;

    //message type 需要从1开始
    public static final int MESSAGE_TYPE_SENT_MAP = 1;
    public static final int MESSAGE_TYPE_RECV_MAP = 2;
    public static final int MESSAGE_TYPE_SENT_ORDER = 3;
    public static final int MESSAGE_TYPE_RECV_ORDER = 4;
    public static final int MESSAGE_TYPE_SENT_EVAL = 5;
    public static final int MESSAGE_TYPE_RECV_EVAL = 6;
    public static final int MESSAGE_TYPE_SENT_TRACK = 7;
    public static final int MESSAGE_TYPE_RECV_TRACK = 8;
    public static final int MESSAGE_TYPE_SENT_FORM = 9;
    public static final int MESSAGE_TYPE_RECV_FORM = 10;


    //message type 最大值
    public static final int MESSAGE_TYPE_COUNT = 13;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void setUpView() {
        //这是新添加的扩展点击事件
        setChatFragmentListener(this);
        super.setUpView();
        //可以在此处设置titleBar(标题栏)的属性
//        titleBar.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        titleBar.setLeftImageResource(R.drawable.hd_icon_title_back);
        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isSingleActivity(getActivity())) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }
                getActivity().finish();
            }
        });
        titleBar.setRightImageResource(R.drawable.hd_chat_delete_icon);
        titleBar.setRightLayoutClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
//        ((Button)inputMenu.getButtonSend()).setBackgroundResource(R.color.top_bar_normal_bg);

        //Message.testCmd(AgoraMessage.newAgoraMessage().getCurrentChatUsername());

    }



    private void showAlertDialog() {
        FragmentTransaction mFragTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        String fragmentTag = "dialogFragment";
        Fragment fragment =  getActivity().getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if(fragment!=null){
            //为了不重复显示dialog，在显示对话框之前移除正在显示的对话框
            mFragTransaction.remove(fragment);
        }
        final AlertDialogFragment dialogFragment = new AlertDialogFragment();
        dialogFragment.setTitleText(getString(R.string.prompt));
        dialogFragment.setContentText(getString(R.string.Whether_to_empty_all_chats));
        dialogFragment.setupLeftButton(null, null);
        dialogFragment.setupRightBtn(null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatClient.getInstance().chatManager().clearConversation(toChatUsername);
                messageList.refresh();
                dialogFragment.dismiss();
                MediaManager.release();
            }
        });
        dialogFragment.show(mFragTransaction, fragmentTag);
    }

    @Override
    public void onAvatarClick(String username) {
        //头像点击事情
//        startActivity(new Intent(getActivity(), ...class));
    }

    @Override
    public boolean onMessageBubbleClick(Message message) {
        //消息框点击事件,return true
        if (message.getType() == Message.Type.LOCATION) {
            EMLocationMessageBody locBody = (EMLocationMessageBody) message.body();
            Intent intent = new Intent(getActivity(), BaiduMapActivity.class);
            intent.putExtra("latitude", locBody.getLatitude());
            intent.putExtra("longitude", locBody.getLongitude());
            intent.putExtra("address", locBody.getAddress());
            startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    public void onMessageBubbleLongClick(Message message) {
        //消息框长按
        startActivityForResult(new Intent(getActivity(), ContextMenuActivity.class).putExtra("message", message), REQUEST_CODE_CONTEXT_MENU);
    }

    @Override
    public  void onMessageItemClick(Message message, MessageList.ItemAction action)
    {
        switch (action) {
            case ITEM_TO_NOTE:
                Intent intent = new Intent(getActivity(), NewLeaveMessageActivity.class);
                startActivity(intent);
                break;
            case ITEM_RESOLVED:
                ChatManager.getInstance().postRobotQuality(message, true, null, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        EMLog.d(TAG, "robot comment sucess");
                        MessageHelper.createCommentSuccessMsg(message,"");
                        messageList.refresh();
                    }

                    @Override
                    public void onError(int i, String s) {
                        EMLog.e(TAG, "robot comment fail: " + i + "reason: " + s);
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
                break;
            case ITEM_UNSOLVED:
                Intent tagsIntent = new Intent(getActivity(), RobotCommentTagsActivity.class);
                tagsIntent.putExtra("msgId", message.messageId());
                startActivity(tagsIntent);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {
        switch (itemId) {
            case ITEM_MAP: //地图
                startActivityForResult(new Intent(getActivity(), BaiduMapActivity.class), REQUEST_CODE_SELECT_MAP);
                break;
	        case ITEM_LEAVE_MSG://ITEM_SHORTCUT:
		        Intent intent = new Intent(getActivity(), NewLeaveMessageActivity.class);
		        startActivity(intent);
		        getActivity().finish();
                break;

            case ITEM_VIDEO:
                // TODO 这里简单处理下权限
                // VideoCallWindowService.show(getContext());
                // CallActivity.show(getContext());
                Calling.callingRequest(getContext(), AgoraMessage.newAgoraMessage().getCurrentChatUsername());
                startVideoCall();
                break;
            case ITEM_EVALUATION:


                ChatManager.getInstance().getCurrentSessionId(toChatUsername, new ValueCallBack<String>() {
                    @Override
                    public void onSuccess(String value) {
                        ChatClient.getInstance().chatManager().asyncSendInviteEvaluationMessage(toChatUsername, value, null);
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        Log.e(TAG, "errorMsg = "+errorMsg);
                        ChatClient.getInstance().chatManager().asyncSendInviteEvaluationMessage(toChatUsername, "", null);
                    }
                });
               /* String serviceSessionId = "";
                ChatClient.getInstance().chatManager().asyncSendInviteEvaluationMessage(toChatUsername, serviceSessionId, null);*/
                break;
//            case ITEM_FILE:
//                //如果需要覆盖内部的,可以return true
//                //demo中通过系统API选择文件,实际app中最好是做成qq那种选择发送文件
//                return true;
            default:
                break;
        }
        //不覆盖已有的点击事件
        return false;
    }


    private void startVideoCall(){
        inputMenu.hideExtendMenuContainer();
        /*Message message = Message.createVideoInviteSendMessage(getString(R.string.em_chat_invite_video_call), toChatUsername);
        ChatClient.getInstance().chatManager().sendMessage(message);*/
        ChatClient.getInstance().callManager().callVideo(getString(R.string.em_chat_invite_video_call), toChatUsername);
    }



    @Override
    public CustomChatRowProvider onSetCustomChatRowProvider() {
        return new DemoCustomChatRowProvider();
    }

    @Override
    protected void registerExtendMenuItem() {
        //demo 这里不覆盖基类已经注册的item, item点击listener沿用基类的
        super.registerExtendMenuItem();
        //增加扩展的item
        inputMenu.registerExtendMenuItem(R.string.attach_location, R.drawable.hd_chat_location_selector, ITEM_MAP, R.id.chat_menu_map, extendMenuItemClickListener);
        inputMenu.registerExtendMenuItem(R.string.leave_title, R.drawable.em_chat_phrase_selector, ITEM_LEAVE_MSG, R.id.chat_menu_leave_msg, extendMenuItemClickListener);
        if (VecConfig.newVecConfig().isOldVideo()){
            inputMenu.registerExtendMenuItem(R.string.attach_call_video, R.drawable.em_chat_video_selector, ITEM_VIDEO, R.id.chat_menu_video_call, extendMenuItemClickListener);
        }

        inputMenu.registerExtendMenuItem(R.string.attach_evaluation, R.drawable.em_chat_evaluation_selector, ITEM_EVALUATION, R.id.chat_menu_evaluation, extendMenuItemClickListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
            switch (resultCode) {
                case ContextMenuActivity.RESULT_CODE_COPY: // 复制消息
                    String string = ((EMTextMessageBody) contextMenuMessage.body()).getMessage();
                    clipboard.setText(string);
                    break;
                case ContextMenuActivity.RESULT_CODE_DELETE: // 删除消息
                    if (contextMenuMessage.getType() == Message.Type.VOICE){
                        EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) contextMenuMessage.body();
                        String voicePath = voiceBody.getLocalUrl();
                        MediaManager.release(voicePath);
                    }
                    conversation.removeMessage(contextMenuMessage.messageId());
                    messageList.refresh();
                    break;
                default:
                    break;
            }
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_SELECT_MAP) {
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                String locationAddress = data.getStringExtra("address");
                if (locationAddress != null && !locationAddress.equals("")) {
                    sendLocationMessage(latitude, longitude, locationAddress, toChatUsername);
                } else {
                    ToastHelper.show(getActivity(), R.string.unable_to_get_loaction);
                }
            } else if (requestCode == REQUEST_CODE_SHORTCUT) {
                String content = data.getStringExtra("content");
                if (!TextUtils.isEmpty(content)) {
                    inputMenu.setInputMessage(content);
                }
            } else if (requestCode == REQUEST_CODE_EVAL) {
                messageList.refresh();
            }
        }

    }

    @Override
    public void onMessageSent() {
        messageList.refreshSelectLast();
    }

    /**
     * chat row provider
     */
    private final class DemoCustomChatRowProvider implements CustomChatRowProvider {

        @Override
        public int getCustomChatRowTypeCount() {
            //地图 和 满意度 发送接收 共4种
            //订单 和 轨迹 发送接收共4种
            // form 发送接收2种
            return MESSAGE_TYPE_COUNT;
        }

        @Override
        public int getCustomChatRowType(Message message) {
            //此处内部有用到,必须写否则可能会出现错位
            if (message.getType() == Message.Type.LOCATION){
                return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_MAP : MESSAGE_TYPE_SENT_MAP;
            }else if (message.getType() == Message.Type.TXT){
                switch (MessageHelper.getMessageExtType(message)) {
                    case EvaluationMsg:
                        return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_EVAL : MESSAGE_TYPE_SENT_EVAL;
                    case OrderMsg:
                        return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_ORDER : MESSAGE_TYPE_SENT_ORDER;
                    case TrackMsg:
                        return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TRACK : MESSAGE_TYPE_SENT_TRACK;
                    case FormMsg:
                        return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_FORM : MESSAGE_TYPE_SENT_FORM;
                }
            }

            return -1;
        }

        @Override
        public ChatRow getCustomChatRow(Message message, int position, BaseAdapter adapter) {
            if (message.getType() == Message.Type.LOCATION) {
                return new ChatRowLocation(getActivity(), message, position, adapter);
            } else if (message.getType() == Message.Type.TXT) {
                switch (MessageHelper.getMessageExtType(message)) {
                    case EvaluationMsg:
                        return new ChatRowEvaluation(getActivity(), message, position, adapter);
                    case OrderMsg:
                        return new ChatRowOrder(getActivity(), message, position, adapter);
                    case TrackMsg:
                        return new ChatRowTrack(getActivity(), message, position, adapter);
                    case FormMsg:
                        return new ChatRowForm(getActivity(), message, position, adapter);
                }
            }
            return null;
        }
    }

}
