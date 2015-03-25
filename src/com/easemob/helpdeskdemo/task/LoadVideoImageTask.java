package com.easemob.helpdeskdemo.task;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.VideoMessageBody;
//import com.easemob.chatuidemo.activity.ShowVideoActivity;
import com.easemob.helpdeskdemo.utils.CommonUtils;
import com.easemob.helpdeskdemo.utils.ImageCache;
import com.easemob.util.ImageUtils;

public class LoadVideoImageTask extends AsyncTask<Object, Void, Bitmap> {

	private ImageView iv = null;
	String thumbnailPath = null;
	String thumbnailUrl = null;
	Activity activity;
	EMMessage message;
	BaseAdapter adapter;

	@Override
	protected Bitmap doInBackground(Object... params) {
		thumbnailPath = (String) params[0];
		thumbnailUrl = (String) params[1];
		iv = (ImageView) params[2];
		activity = (Activity) params[3];
		message = (EMMessage) params[4];
		adapter = (BaseAdapter) params[5];
		if (new File(thumbnailPath).exists()) {
			return ImageUtils.decodeScaleImage(thumbnailPath, 120, 120);
		} else {
			return null;
		}
	}

}
