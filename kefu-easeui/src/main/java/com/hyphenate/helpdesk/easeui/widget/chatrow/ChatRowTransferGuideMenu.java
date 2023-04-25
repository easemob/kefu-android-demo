package com.hyphenate.helpdesk.easeui.widget.chatrow;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.easeui.widget.MessageList;
import com.hyphenate.helpdesk.model.ContentFactory;
import com.hyphenate.helpdesk.model.MessageHelper;
import com.hyphenate.helpdesk.model.RobotMenuInfo;
import com.hyphenate.helpdesk.model.TransferGuideMenuInfo;
import com.hyphenate.helpdesk.util.Log;
import com.hyphenate.util.DensityUtil;
import com.hyphenate.util.EMLog;

import org.json.JSONObject;

import java.util.Collection;

public class ChatRowTransferGuideMenu extends ChatRow {

    TextView tvTitle;
    LinearLayout tvList;
    Context mContext;


    public   ChatRowTransferGuideMenu(Context context, Message message, int position, BaseAdapter adapter) {
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
                        /*if (MessageHelper.isTransferGuideMenu(message)) {
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
                        }*/

                        //存在上下文的机器人菜单消息
                        if (MessageHelper.isTransferGuideMenu(message)) {
                            EMLog.d(TAG, "isTransferGuideMenu");
                            if (item.isLeaveMessage()) {
                                EMLog.d(TAG, "item clicked");
                                if (itemClickListener != null) {
                                    itemClickListener.onMessageItemClick(message, MessageList.ItemAction.ITEM_TO_NOTE);
                                }
                            } else {
                                // 原先代码
                                /*Message sendMessage = Message.createSendMessageForMenu(item, message.from());
                                if (sendMessage != null) {
                                    ChatClient.getInstance().chatManager().sendMessage(sendMessage);
                                }*/

                                Message sendMessage = Message.createSendMessageForMenu(item, message.from());
                                if (sendMessage != null) {
                                    if (item.getQueueType().equalsIgnoreCase("video")){
                                        ChatClient.getInstance().chatManager().sendMessage(sendMessage, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                // TODO 点击机器人菜单条目，修改后代码
                                                onClickTransferGuideMenuItem(item);
                                            }

                                            @Override
                                            public void onError(int code, String error) {
                                                // TODO 点击机器人菜单条目，修改后代码
                                                onClickTransferGuideMenuItem(item);
                                            }

                                            @Override
                                            public void onProgress(int progress, String status) {

                                            }
                                        });
                                    }else if (item.getQueueType().equalsIgnoreCase("independentVideo")){
                                        onClickTransferGuideMenuItem(item);
                                    }else {
                                        ChatClient.getInstance().chatManager().sendMessage(sendMessage);
                                    }
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



    public void onClickTransferGuideMenuItem(TransferGuideMenuInfo.Item item) {
        if (mClickTransferGuideMenuItem != null){
            mClickTransferGuideMenuItem.onClickGuideMenuItemCallVideo(item);
        }

        String sessionId = "";
        try {
            sessionId = ChatClient.getInstance().chatManager().getSessionIdFromMessage(message);
        }catch (Exception e){
            e.printStackTrace();
        }

        // 发广播
        try {

            String vecImServiceNumber = getVecImServiceNumber(item);
            String configId = getConfigId(item);
            String cecImServiceNumber = message.from();

            Gson gson = new Gson();
            String json = gson.toJson(item);
            Intent intent = new Intent("guide.menu.item.action");
            intent.putExtra("data",json);
            intent.putExtra("vecImServiceNumber",vecImServiceNumber);
            intent.putExtra("configId",configId);
            intent.putExtra("cecImServiceNumber",cecImServiceNumber);
            intent.putExtra("sessionId",sessionId);
            mContext.getApplicationContext().sendBroadcast(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String getVecImServiceNumber(TransferGuideMenuInfo.Item item){
        try {
            /*Log.e("qqqqqqqqqqqq","item = "+item.getJsonObj());
            JSONObject msgtype = message.getJSONObjectAttribute("msgtype");
            Log.e("qqqqqqqqqqqq","msgtype = "+msgtype);*/

            JSONObject jsonObj = item.getJsonObj();
            if (jsonObj.has("pluginConfig")){
                JSONObject pluginConfig = jsonObj.getJSONObject("pluginConfig");
                if (pluginConfig.has("appConfig")){
                    JSONObject appConfig = pluginConfig.getJSONObject("appConfig");
                    JSONObject configJson = appConfig.getJSONObject("configJson");
                    JSONObject channel = configJson.getJSONObject("channel");
                    return channel.getString("to");
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    private String getConfigId(TransferGuideMenuInfo.Item item){
        try {
            JSONObject jsonObj = item.getJsonObj();
            if (jsonObj.has("pluginConfig")){
                JSONObject pluginConfig = jsonObj.getJSONObject("pluginConfig");
                if (pluginConfig.has("appConfig")){
                    JSONObject appConfig = pluginConfig.getJSONObject("appConfig");
                    return appConfig.getString("configId");
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    private OnClickTransferGuideMenuItemCallVideo mClickTransferGuideMenuItem;
    public void setOnClickTransferGuideMenuItemCallVideo(OnClickTransferGuideMenuItemCallVideo item){
        this.mClickTransferGuideMenuItem = item;
    }
    public interface OnClickTransferGuideMenuItemCallVideo {
        void onClickGuideMenuItemCallVideo(TransferGuideMenuInfo.Item item/*, String vecImServiceNumber, String configId, String cecImServiceNumber*/);
    }
}
