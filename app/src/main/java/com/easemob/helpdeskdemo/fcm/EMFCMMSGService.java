package com.easemob.helpdeskdemo.fcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hyphenate.helpdesk.easeui.UIProvider;
import com.hyphenate.util.EMLog;


public class EMFCMMSGService extends FirebaseMessagingService {
    private static final String TAG = "EMFCMMSGService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        EMLog.e(TAG, "EMFCMMSGService#onMessageReceived:" + remoteMessage.toString());
        if (remoteMessage.getData().size() > 0) {
            String message = remoteMessage.getData().get("alert");
            Log.i(TAG, "onMessageReceived: " + message);
            EMLog.e(TAG, "EMFCMMSGService#onMessageReceived");
            UIProvider.getInstance().getNotifier().sendNotification(message);
        }
    }
}
