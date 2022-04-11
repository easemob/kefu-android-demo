package com.easemob.helpdeskdemo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import com.easemob.helpdeskdemo.ui.CallActivity;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.util.Log;
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
        if ("calling.state".equals(action)){
            // 防止正在通话中，又新发来视频请求，isOnLine代表是否接通通话中
            mIsOnLine = intent.getBooleanExtra("state", false);
        }else {
            //call type
            String type = intent.getStringExtra("type");
            //call to
            Parcelable zuoXiSendRequestObj = intent.getParcelableExtra("zuoXiSendRequestObj");

            if ("video".equals(type)){// video call
                if (!mIsOnLine){
                    context.startActivity(new Intent(context, CallActivity.class)
                            .putExtra("zuoXiSendRequestObj", zuoXiSendRequestObj)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }
        }

    }
}
