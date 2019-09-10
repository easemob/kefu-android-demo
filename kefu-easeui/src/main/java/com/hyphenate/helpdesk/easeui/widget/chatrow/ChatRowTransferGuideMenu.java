package com.hyphenate.helpdesk.easeui.widget.chatrow;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.easeui.widget.MessageList;
import com.hyphenate.helpdesk.model.ContentFactory;
import com.hyphenate.helpdesk.model.MessageHelper;
import com.hyphenate.helpdesk.model.RobotMenuInfo;
import com.hyphenate.helpdesk.model.TransferGuideMenuInfo;
import com.hyphenate.util.DensityUtil;
import com.hyphenate.util.EMLog;

import org.json.JSONObject;

import java.util.Collection;

public class ChatRowTransferGuideMenu extends ChatRow {

    TextView tvTitle;
    LinearLayout tvList;
    Context mContext;


    public ChatRowTransferGuideMenu(Context context, Message message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
        mContext = context;
    }

    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct() == Message.Direct.RECEIVE ? R.layout.hd_row_received_menu
                : R.layout.hd_row_sent_message, this);
    }

    @Override
    protected void onFindViewById() {
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvList = (LinearLayout) findViewById(R.id.ll_layout);
    }

    @Override
    protected void onUpdateView() {

    }

    @Override
    protected void onSetUpView() {
        TransferGuideMenuInfo info;
        if((info = MessageHelper.getTransferGuideMenu(message)) != null){
            tvTitle.setText(info.getTitle());
            setTransferGuideMenuMessageLayout(tvList, info);
        }
    }

    @Override
    protected void onBubbleClick() {
    }

    private void setTransferGuideMenuMessageLayout(LinearLayout parentView, final TransferGuideMenuInfo menuInfo){

        parentView.removeAllViews();

        if (menuInfo.getItems() != null && !menuInfo.getItems().isEmpty()){
            Collection<TransferGuideMenuInfo.Item> items = menuInfo.getItems();
            for(final TransferGuideMenuInfo.Item item : items) {
                final String content = item.getName();
                final TextView textView = new TextView(context);
                textView.setText(content);
                textView.setTextSize(15);
                textView.setTextColor(context.getResources().getColorStateList(R.color.hd_menu_msg_text_color));
                textView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        //存在上下文的机器人菜单消息
                        if (MessageHelper.isTransferGuideMenu(message)) {
                            EMLog.d(TAG, "isTransferGuideMenu");
                            if (item.isLeaveMessage()) {
                                EMLog.d(TAG, "item clicked");
                                if (itemClickListener != null) {
                                    itemClickListener.onMessageItemClick(message, MessageList.ItemAction.ITEM_TO_NOTE);
                                }
                            } else {
                                Message sendMessage = Message.createSendMessageForMenu(item, message.from());
                                if (sendMessage != null) {
                                    ChatClient.getInstance().chatManager().sendMessage(sendMessage);
                                }
                            }

                        } else {
                            EMLog.d(TAG, "unknow message");
                        }
                    }
                });
                LinearLayout.LayoutParams llLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                llLp.bottomMargin = DensityUtil.dip2px(context, 3);
                llLp.topMargin = DensityUtil.dip2px(context, 3);
                parentView.addView(textView, llLp);
            }
        }else if (menuInfo.getList() != null && !menuInfo.getList().isEmpty()){
            Collection<String> items = menuInfo.getList();
            for (final String content : items){
                final TextView textView = new TextView(context);
                textView.setText(content);
                textView.setTextSize(15);
                textView.setTextColor(context.getResources().getColorStateList(R.color.hd_menu_msg_text_color));
                textView.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        Message sendMessage = Message.createTxtSendMessage(content, message.from());
                        ChatClient.getInstance().chatManager().sendMessage(sendMessage);
                    }
                });
                LinearLayout.LayoutParams llLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                llLp.bottomMargin = DensityUtil.dip2px(context, 3);
                llLp.topMargin = DensityUtil.dip2px(context, 3);
                parentView.addView(textView, llLp);
            }
        }
    }
}
