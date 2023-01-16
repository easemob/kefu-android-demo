package com.easemob.helpdeskdemo.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class GCMPushBroadCast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("info", "gcmpush onreceive");
		String alert = intent.getStringExtra("alert");
		sendNotification(context, alert, true);
	}

	protected NotificationManager notificationManager = null;

	protected static int notifyID = 525; // start notification id
	protected static int foregroundNotifyID = 555;

	public void sendNotification(Context context, String message, boolean isForeground){

		if (notificationManager == null){
			notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		}

		try {
			PackageManager packageManager = context.getPackageManager();
			//notification title
			String contentTitle = (String) packageManager.getApplicationLabel(context.getApplicationInfo());
			String packageName = context.getApplicationInfo().packageName;

			Uri defaultSoundUrlUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			//create and send notification
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
					.setSmallIcon(context.getApplicationInfo().icon)
					.setSound(defaultSoundUrlUri)
					.setWhen(System.currentTimeMillis()).setAutoCancel(true);

			Intent msgIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);

			PendingIntent pendingIntent = PendingIntent.getActivity(context, notifyID, msgIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			mBuilder.setContentTitle(contentTitle);
			mBuilder.setTicker(message);
			mBuilder.setContentText(message);
			mBuilder.setContentIntent(pendingIntent);
			Notification notification = mBuilder.build();
			notificationManager.notify(notifyID, notification);
		}catch (Exception e){
			e.printStackTrace();
		}

	}

}
