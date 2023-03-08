package com.hyphenate.helpdesk.videokit.ui;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.hyphenate.chat.AgoraMessage;
import com.hyphenate.helpdesk.videokit.permission.FloatWindowManager;

public class Calling {
    // 主动
    public static void callingRequest(Context context, String toChatUserName){
        if (FloatWindowManager.getInstance().checkPermission(context)){
            VideoCallWindowService.show(context);
        }else {
            toChatUserName = TextUtils.isEmpty(toChatUserName) ? AgoraMessage.newAgoraMessage().getCurrentChatUsername() : toChatUserName;
            CallActivity.show(context, toChatUserName);
        }
    }


    // 被动
    public static void callingResponse(Context context, Intent intent){
        if (FloatWindowManager.getInstance().checkPermission(context)){
            VideoCallWindowService.show(context, intent);
        }else {
            CallActivity.show(context, intent);
        }
    }
}
