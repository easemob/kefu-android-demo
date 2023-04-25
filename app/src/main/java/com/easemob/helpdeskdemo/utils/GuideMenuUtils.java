package com.easemob.helpdeskdemo.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import com.easemob.veckit.VECKitCalling;
import com.google.gson.Gson;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.callback.ValueCallBack;
import com.hyphenate.helpdesk.model.TransferGuideMenuInfo;
import com.hyphenate.helpdesk.videokit.ui.Calling;

public class GuideMenuUtils extends BroadcastReceiver {
    private final static String TAG = "GuideMenuUtils";
    private static GuideMenuUtils sGuideMenuUtils;

    public static GuideMenuUtils getGuideMenuUtils() {
        if (sGuideMenuUtils == null){
            synchronized (GuideMenuUtils.class){
                if (sGuideMenuUtils == null){
                    sGuideMenuUtils = new GuideMenuUtils();
                }
            }
        }
        return sGuideMenuUtils;
    }


    public static void sendBroadcast(Context context, TransferGuideMenuInfo.Item item,
                                     String vecImServiceNumber, String configId, String cecImServiceNumber, String sessionId){
        // TransferGuideMenuInfo.Item
        try {
            Gson gson = new Gson();
            Intent intent = new Intent("guide.menu.item.action");
            intent.putExtra("data",gson.toJson(item));
            intent.putExtra("vecImServiceNumber",vecImServiceNumber);
            intent.putExtra("configId",configId);
            intent.putExtra("cecImServiceNumber",cecImServiceNumber);
            intent.putExtra("sessionId", sessionId);
            context.getApplicationContext().sendBroadcast(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // 需要在对应的聊天页面注册广播
    public void registerReceiver(Context context){
        IntentFilter filter = new IntentFilter("guide.menu.item.action");
        context.getApplicationContext().registerReceiver(this, filter);
        Log.e(TAG,"registerReceiver");
    }

    // 需要在对应的聊天页面注销广播
    public void unregisterReceiver(Context context){
        context.getApplicationContext().unregisterReceiver(this);
        Log.e(TAG,"unregisterReceiver");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            String data = intent.getStringExtra("data");
            String cecImServiceNumber = intent.getStringExtra("cecImServiceNumber");
            Gson gson = new Gson();
            TransferGuideMenuInfo.Item item = gson.fromJson(data, TransferGuideMenuInfo.Item.class);
            Log.e(TAG,"data = "+data);
            if (item.getQueueType().equalsIgnoreCase("independentVideo")){
                closeCec(cecImServiceNumber, new ValueCallBack<String>() {
                    @Override
                    public void onSuccess(String value) {
                        sendVec(context, intent, value, cecImServiceNumber);
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        sendVec(context, intent,"","");
                    }
                });

            }else if (item.getQueueType().equalsIgnoreCase("video")){
                Calling.callingRequestFromClickGuideMenu(context, cecImServiceNumber);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendVec(Context context, Intent intent, String sessionId, String relatedImServiceNumber){
        String vecImServiceNumber = intent.getStringExtra("vecImServiceNumber");
        String configId = intent.getStringExtra("configId");
        String sid = intent.getStringExtra("sessionId");
        sessionId = TextUtils.isEmpty(sessionId) ? sid : sessionId;
        VECKitCalling.callingRequest(context, vecImServiceNumber, configId, sessionId, mVisitorId, relatedImServiceNumber);
    }

    private volatile String mVisitorId;
    public void closeCec(String imService, final ValueCallBack<String> valueCallBack){
        Log.e(TAG,"imService = "+imService);
        ChatClient.getInstance().chatManager().asyncVisitorId(imService, new ValueCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                Log.e(TAG,"asyncVisitorId = "+value);
                mVisitorId = value;
                getSessionIdFromMessage(imService, new ValueCallBack<String>() {
                    @Override
                    public void onSuccess(String value) {
                        Log.e(TAG,"getCurrentSessionId = "+value);
                        if (valueCallBack != null){
                            valueCallBack.onSuccess(value);
                        }
                        String tenantId = ChatClient.getInstance().tenantId();
                        ChatClient.getInstance().chatManager().asyncCecClose(tenantId, mVisitorId, value, new ValueCallBack<String>() {
                            @Override
                            public void onSuccess(String value) {
                                Log.e(TAG,"getSessionIdFromMessage = "+value);
                            }

                            @Override
                            public void onError(int error, String errorMsg) {
                                Log.e(TAG,"asyncCecClose errorMsg = "+errorMsg);
                            }
                        });
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        Log.e(TAG,"getCurrentSessionId errorMsg = "+errorMsg);
                        if (valueCallBack != null){
                            valueCallBack.onError(error, errorMsg);
                        }
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                Log.e(TAG,"asyncVisitorId errorMsg = "+errorMsg);
            }
        });


    }

    private void getSessionIdFromMessage(String imService, ValueCallBack<String> callBack){
        ChatClient.getInstance().chatManager().getCurrentSessionId(imService, new ValueCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                Log.e(TAG,"getCurrentSessionId = "+value);
                if (callBack != null){
                    callBack.onSuccess(value);
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                Log.e(TAG,"getCurrentSessionId errorMsg = "+errorMsg);
                if (callBack != null){
                    callBack.onError(error, errorMsg);
                }
            }
        });
    }

}
