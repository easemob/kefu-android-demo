

package com.easemob.helpdeskdemo.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.DemoMessageHelper;
import com.easemob.helpdeskdemo.R;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.Message;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.helpdesk.easeui.recorder.MediaManager;
import com.hyphenate.helpdesk.easeui.ui.BaseActivity;
import com.hyphenate.helpdesk.easeui.ui.ChatFragment;
import com.hyphenate.helpdesk.easeui.util.CommonUtils;
import com.hyphenate.helpdesk.easeui.util.Config;

import java.util.HashMap;

public class ChatActivity extends BaseActivity {

    public static ChatActivity instance = null;

    private ChatFragment chatFragment;

    String toChatUsername;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.hd_activity_chat);
        instance = this;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            //IM服务号
            toChatUsername = bundle.getString(Config.EXTRA_SERVICE_IM_NUMBER);
        //可以直接new ChatFragment使用
        String chatFragmentTAG = "chatFragment";
        chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentByTag(chatFragmentTAG);
        if (chatFragment == null){
            chatFragment = new CustomChatFragment();
            //传入参数
            chatFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment, chatFragmentTAG).commit();
            sendOrderOrTrack();
        }
    }


    /**
     * 发送订单或轨迹消息
     */
    private void sendOrderOrTrack() {
        Bundle bundle = getIntent().getBundleExtra(Config.EXTRA_BUNDLE);
        if (bundle != null) {
            //检查是否是从某个商品详情进来
            int selectedIndex = bundle.getInt(Constant.INTENT_CODE_IMG_SELECTED_KEY, Constant.INTENT_CODE_IMG_SELECTED_DEFAULT);
            switch (selectedIndex) {
                case Constant.INTENT_CODE_IMG_SELECTED_1:
                case Constant.INTENT_CODE_IMG_SELECTED_2:
                    sendOrderMessage(selectedIndex);
                    break;
                case Constant.INTENT_CODE_IMG_SELECTED_3:
                case Constant.INTENT_CODE_IMG_SELECTED_4:
                    sendTrackMessage(selectedIndex);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 发送订单消息
     *
     * 不发送则是saveMessage
     * @param selectedIndex
     */
    private void sendOrderMessage(int selectedIndex){
        HashMap<String, Object> map = new HashMap<>();
        map.put("createTicketEnable",true);
        Message message = Message.createTxtSendMessage(getMessageContent(selectedIndex), toChatUsername);
        message.addContent(DemoMessageHelper.createOrderInfo(this, selectedIndex));
        message.addMsgTypeDictionary(map);
        ChatClient.getInstance().chatManager().saveMessage(message);

        String stringAttribute = null;
        try {
            stringAttribute = message.getStringAttribute(Message.KEY_MSGTYPE);
            Log.e("ppppppppppp","msgType = " + stringAttribute);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }

    }

    /**
     * 发送轨迹消息
     * @param selectedIndex
     */
    private void sendTrackMessage(int selectedIndex) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("createTicketEnable",true);
        Message message = Message.createTxtSendMessage(getMessageContent(selectedIndex), toChatUsername);
        message.addContent(DemoMessageHelper.createVisitorTrack(this, selectedIndex));
        message.addMsgTypeDictionary(map);
        ChatClient.getInstance().chatManager().sendMessage(message);
    }


    private String getMessageContent(int selectedIndex){
        switch (selectedIndex){
            case 1:
                return getResources().getString(R.string.em_example1_text);
            case 2:
                return getResources().getString(R.string.em_example2_text);
            case 3:
                return getResources().getString(R.string.em_example3_text);
            case 4:
                return getResources().getString(R.string.em_example4_text);
        }
        // 内容自己随意定义。
        return "";
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
        instance = null;
//        ChatClient.getInstance().chatManager().cancelVideoConferences(toChatUsername, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String username = intent.getStringExtra(Config.EXTRA_SERVICE_IM_NUMBER);
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        if (chatFragment != null) {
            chatFragment.onBackPressed();
        }
        if (CommonUtils.isSingleActivity(this)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
