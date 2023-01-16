package com.easemob.helpdeskdemo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.easemob.helpdeskdemo.utils.Calling;
import com.easemob.veckit.VECKitCalling;
import com.hyphenate.chat.AgoraMessage;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.VecConfig;
import com.hyphenate.util.EMLog;


/**
 * Created by liyuzhao on 11/01/2017.
 */

public class CallReceiver extends BroadcastReceiver {
    boolean mIsOnLine;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!ChatClient.getInstance().isLoggedInBefore()){
            return;
        }
        String action = intent.getAction();
        EMLog.e("VECVideo","广播接收 onReceive action = "+action + "， mIsOnLine = "+mIsOnLine);
        if ("calling.state".equals(action)){
            // 防止正在通话中，又新发来视频请求，isOnLine代表是否接通通话中
            mIsOnLine = intent.getBooleanExtra("state", false);
        }else {
            //call type
            String type = intent.getStringExtra("type");
            EMLog.e("VECVideo","广播接收 onReceive type = "+type + "， mIsOnLine = "+mIsOnLine);
            if ("video".equals(type)){// video call
                if (!mIsOnLine){
                    // 新版vec视频客服
                    if (VecConfig.newVecConfig().isVecVideo()){
                        EMLog.e("VECVideo","广播接收 新版vec");
                        VECKitCalling.callingResponse(context, intent);
                    }else {
                        // 旧版在线视频
                        EMLog.e("VECVideo","广播接收 旧版在线视频");
                        Calling.callingResponse(context, intent);
                    }
                }
            }else if (AgoraMessage.TYPE_ENQUIRYINVITE.equalsIgnoreCase(type)){
                // 满意度评价
                VECKitCalling.callingRetry(context, intent.getStringExtra("content"));
            }

        }

    }
}
