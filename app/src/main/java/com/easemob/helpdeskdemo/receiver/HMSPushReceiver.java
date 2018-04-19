package com.easemob.helpdeskdemo.receiver;

import android.content.Context;
import android.text.TextUtils;

import com.huawei.hms.support.api.push.PushReceiver;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.util.EMLog;

/**
 * 接收华为推送的token，并为环信提供华为token，用于接收离线推送
 */

public class HMSPushReceiver extends PushReceiver{

	@Override
	public void onToken(Context context, String token) {
		//没有失败回调，假定token失败时token为null
		if (!TextUtils.isEmpty(token)){
			EMLog.d("HWHMSPush", "register huawei hms push token success token:" + token);
			ChatClient.getInstance().sendHMSPushTokenToServer("10663060", token);
		}else{
			EMLog.e("HWHMSPush", "register huawei hms push token fail!");
		}

	}
}
