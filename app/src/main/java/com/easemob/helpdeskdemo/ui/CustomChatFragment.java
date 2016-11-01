package com.easemob.helpdeskdemo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.widget.chatrow.ChatRowEvaluation;
import com.easemob.helpdeskdemo.widget.chatrow.ChatRowLocation;
import com.easemob.helpdeskdemo.widget.chatrow.ChatRowOrder;
import com.easemob.helpdeskdemo.widget.chatrow.ChatRowTrack;
import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.easeui.provider.CustomChatRowProvider;
import com.hyphenate.helpdesk.easeui.ui.ChatFragment;
import com.hyphenate.helpdesk.easeui.util.CommonUtils;
import com.hyphenate.helpdesk.easeui.widget.chatrow.ChatRow;
import com.hyphenate.helpdesk.model.MessageHelper;

public class CustomChatFragment extends ChatFragment implements ChatFragment.EaseChatFragmentListener {

    //避免和基类定义的常量可能发生冲突,常量从11开始定义
    private static final int ITEM_MAP = 11;
    private static final int ITEM_SHORTCUT = 12;

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
        titleBar.setLeftImageResource(R.drawable.em_btn_back);
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
        titleBar.setRightImageResource(R.drawable.em_icon_comment);
        titleBar.setRightLayoutClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewLeaveMessageActivity.class);
                startActivity(intent);
            }
        });

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
            EMLocationMessageBody locBody = (EMLocationMessageBody) message.getBody();
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
    public boolean onExtendMenuItemClick(int itemId, View view) {
        switch (itemId) {
            case ITEM_MAP: //地图
                startActivityForResult(new Intent(getActivity(), BaiduMapActivity.class), REQUEST_CODE_SELECT_MAP);
                break;
            case ITEM_SHORTCUT:
                Intent intent = new Intent(getActivity(), ShortCutMsgActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SHORTCUT);
                getActivity().overridePendingTransition(R.anim.em_activity_open, 0);
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

    @Override
    public CustomChatRowProvider onSetCustomChatRowProvider() {
        return new DemoCustomChatRowProvider();
    }

    @Override
    protected void registerExtendMenuItem() {
        //demo 这里不覆盖基类已经注册的item, item点击listener沿用基类的
        super.registerExtendMenuItem();
        //增加扩展的item
        inputMenu.registerExtendMenuItem(R.string.attach_location, R.drawable.ease_chat_location_selector, ITEM_MAP, extendMenuItemClickListener);
        inputMenu.registerExtendMenuItem(R.string.attach_short_cut_message, R.drawable.em_icon_answer, ITEM_SHORTCUT, extendMenuItemClickListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
            switch (resultCode) {
                case ContextMenuActivity.RESULT_CODE_COPY: // 复制消息
                    String string = ((EMTextMessageBody) contextMenuMessage.getBody()).getMessage();
                    clipboard.setText(string);
                    break;
                case ContextMenuActivity.RESULT_CODE_DELETE: // 删除消息
                    conversation.removeMessage(contextMenuMessage.getMsgId());
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
                    Toast.makeText(getActivity(), R.string.unable_to_get_loaction, Toast.LENGTH_SHORT).show();
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
            return 8;
        }

        @Override
        public int getCustomChatRowType(Message message) {
            //此处内部有用到,必须写否则可能会出现错位
            if (message.getType() == Message.Type.LOCATION){
                return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_MAP : MESSAGE_TYPE_SENT_MAP;
            }else if (message.getType() == Message.Type.TXT){
                if (MessageHelper.getEvalRequest(message) != null){
                    return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_EVAL : MESSAGE_TYPE_SENT_EVAL;
                }else if (MessageHelper.getOrderInfo(message) != null){
                    return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_ORDER : MESSAGE_TYPE_SENT_ORDER;
                }else if (MessageHelper.getVisitorTrack(message) != null){
                    return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TRACK : MESSAGE_TYPE_SENT_TRACK;
                }
            }

            return -1;
        }

        @Override
        public ChatRow getCustomChatRow(Message message, int position, BaseAdapter adapter) {
            if (message.getType() == Message.Type.LOCATION) {
                return new ChatRowLocation(getActivity(), message, position, adapter);
            } else if (message.getType() == Message.Type.TXT) {
                if (MessageHelper.getEvalRequest(message) != null) {
                    return new ChatRowEvaluation(getActivity(), message, position, adapter);
                } else if (MessageHelper.getOrderInfo(message) != null) {
                    return new ChatRowOrder(getActivity(), message, position, adapter);
                }else if (MessageHelper.getVisitorTrack(message) != null) {
                    return new ChatRowTrack(getActivity(), message, position, adapter);
                }
            }
            return null;
        }
    }



}
