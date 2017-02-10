package com.hyphenate.helpdesk.easeui.widget.chatrow;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.model.ContentFactory;
import com.hyphenate.helpdesk.model.MessageHelper;
import com.hyphenate.helpdesk.model.RobotMenuInfo;
import com.hyphenate.util.DensityUtil;

import java.util.Collection;

public class ChatRowRobotMenu extends ChatRow{

    TextView tvTitle;
    LinearLayout tvList;


    public ChatRowRobotMenu(Context context, Message message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        if (MessageHelper.getRobotMenu(message) != null) {
            inflater.inflate(message.direct() == Message.Direct.RECEIVE ? R.layout.ease_row_received_menu
                    : R.layout.ease_row_sent_message, this);
        }
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
        RobotMenuInfo info;
        if((info = MessageHelper.getRobotMenu(message)) != null){
            tvTitle.setText(info.getTitle());
            setRobotMenuMessageLayout(tvList, info);
        }
    }

    @Override
    protected void onBubbleClick() {
    }

    private void setRobotMenuMessageLayout(LinearLayout parentView, final RobotMenuInfo menuInfo){

        parentView.removeAllViews();

        if (menuInfo.getItems() != null && !menuInfo.getItems().isEmpty()){
            Collection<RobotMenuInfo.Item> items = menuInfo.getItems();
            for(RobotMenuInfo.Item item : items) {
                final String content = item.getName();
                final String menuId = item.getId();
                final TextView textView = new TextView(context);
                textView.setText(content);
                textView.setTextSize(15);
                textView.setTextColor(context.getResources().getColorStateList(R.color.ease_menu_msg_text_color));
                textView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Message sendMessage = Message.createTxtSendMessage(content, message.getFrom());
                        //存在上下文的机器人菜单消息
                        sendMessage.addContent(ContentFactory.createRobotMenuIdInfo(null).setMenuId(menuId));
                        ChatClient.getInstance().chatManager().sendMessage(sendMessage);
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
                textView.setTextColor(context.getResources().getColorStateList(R.color.ease_menu_msg_text_color));
                textView.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        Message sendMessage = Message.createTxtSendMessage(content, message.getFrom());
                        ChatClient.getInstance().getChat().sendMessage(sendMessage);
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