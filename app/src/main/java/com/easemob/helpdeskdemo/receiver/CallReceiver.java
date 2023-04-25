package com.easemob.helpdeskdemo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.easemob.veckit.VECKitCalling;
import com.hyphenate.chat.AgoraMessage;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.VecConfig;
import com.hyphenate.helpdesk.videokit.ui.Calling;
import com.hyphenate.util.EMLog;


/**
 * Created by liyuzhao on 11/01/2017.
 */

public class CallReceiver extends BroadcastReceiver {
    boolean mIsOnLine;

    private final static String TAG = "CallReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!ChatClient.getInstance().isLoggedInBefore()){
            mIsOnLine = false;
            return;
        }
        String action = intent.getAction();
        EMLog.e(TAG,"广播接收 onReceive action = "+action + "， mIsOnLine = "+mIsOnLine);
        if ("calling.state".equals(action)){
            // 防止正在通话中，又新发来视频请求，isOnLine代表是否接通通话中
            mIsOnLine = intent.getBooleanExtra("state", false);
        }else {
            //call type
            String type = intent.getStringExtra("type");
            EMLog.e(TAG,"广播接收 onReceive type = "+type + "， mIsOnLine = "+mIsOnLine);
            boolean isVecVideo = intent.getBooleanExtra("isVecVideo", false);
            // isVecVideo = true;
            Log.e("ppppppppppppp","isVecVideo = "+isVecVideo);
            if ("video".equals(type)){// video call
                if (!mIsOnLine){
                    // 新版vec视频客服
                    if (isVecVideo/*VecConfig.newVecConfig().isVecVideo()*/){
                        EMLog.e(TAG,"onReceive 广播接收 新版vec");
                        Log.e(TAG,"onReceive 广播接收 新版vec");
                        VECKitCalling.callingResponse(context, intent);
                    }else {
                        // 旧版在线视频
                        EMLog.e(TAG,"onReceive 广播接收 旧版在线视频");
                        Log.e(TAG,"onReceive 广播接收 旧版在线视频");
                        Calling.callingResponse(context, intent);
                    }
                }
            }else if (AgoraMessage.TYPE_ENQUIRYINVITE.equalsIgnoreCase(type)){
                // 满意度评价
                VECKitCalling.callingEvaluation(context, intent.getStringExtra("content"));
            }

        }

    }
}
