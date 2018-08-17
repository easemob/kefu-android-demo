package com.easemob.kefu_remote.conference;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.easemob.kefu_remote.sdk.EMConferenceManager;

/**
 * 检测通话状态变化的广播接收器
 */
public class CallPhoneReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 如果是来电
        TelephonyManager tManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        //电话的状态
        switch (tManager.getCallState()) {
        case TelephonyManager.CALL_STATE_RINGING:
            //等待接听状态
            String phoneNumber = intent.getStringExtra("incoming_number");
            break;
        case TelephonyManager.CALL_STATE_OFFHOOK:
            EMConferenceManager.getInstance().closeVoiceTransfer();
            EMConferenceManager.getInstance().closeVideoTransfer();
            break;
        case TelephonyManager.CALL_STATE_IDLE:
            EMConferenceManager.getInstance().openVoiceTransfer();
            EMConferenceManager.getInstance().openVideoTransfer();
            break;
        }
    }
}
