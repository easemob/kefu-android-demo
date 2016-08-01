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
package com.easemob.helpdeskdemo.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.easeui.model.EaseImageCache;
import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.DemoHelper;
import com.easemob.helpdeskdemo.R;

import java.util.List;

public class ShopDetailsActivity extends BaseActivity implements EMEventListener{
	private ImageView mImageView;
	private RelativeLayout rl_tochat;
	private ImageButton mImageButton;
	private Bitmap mBitmap = null;
	private int index = Constant.INTENT_CODE_IMG_SELECTED_DEFAULT;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.em_activity_shop_details);
		index = getIntent().getIntExtra(Constant.INTENT_CODE_IMG_SELECTED_KEY, Constant.INTENT_CODE_IMG_SELECTED_DEFAULT);
		
		rl_tochat = (RelativeLayout) findViewById(R.id.rl_tochat);
		mImageButton = (ImageButton) findViewById(R.id.ib_shop_back);
		mImageView = (ImageView) findViewById(R.id.iv_buy);
		mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
		mBitmap = EaseImageCache.getInstance().get("shop_image_details");
		if(mBitmap==null || mBitmap.isRecycled()){
			BitmapFactory.Options opts= new BitmapFactory.Options();
			opts.inSampleSize =2;
			mBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.em_shop_image_details);
			EaseImageCache.getInstance().put("shop_image_details", mBitmap);
		}
		if(mBitmap!=null)
			mImageView.setImageBitmap(mBitmap);
		mImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShopDetailsActivity.this.finish();
			}
		});
		rl_tochat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra(Constant.INTENT_CODE_IMG_SELECTED_KEY, index);
				intent.setClass(ShopDetailsActivity.this, LoginActivity.class);
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		DemoHelper.getInstance().pushActivity(this);
		//register the event listener when enter the foreground
		EMChatManager.getInstance().registerEventListener(this,
						new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage,
								EMNotifierEvent.Event.EventOfflineMessage });
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		DemoHelper.getInstance().popActivity(this);
		EMChatManager.getInstance().unregisterEventListener(this);
	}

	@Override
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
		case EventNewMessage:
			EMMessage message = (EMMessage) event.getData();
			//提示新消息
			DemoHelper.getInstance().getNotifier().onNewMsg(message);
			break;
		case EventOfflineMessage:
			//处理离线消息
			List<EMMessage> messages = (List<EMMessage>) event.getData();
			//消息提醒或只刷新UI
			DemoHelper.getInstance().getNotifier().onNewMesg(messages);
			break;
		default:
			break;
		}
		
	}


}
