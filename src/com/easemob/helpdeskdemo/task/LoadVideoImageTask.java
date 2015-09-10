/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
