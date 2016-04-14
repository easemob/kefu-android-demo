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

import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.model.EaseImageCache;
import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.DemoHelper;
import com.easemob.helpdeskdemo.R;

public class ShopDetailsActivity extends BaseActivity {
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
		mImageView.setScaleType(ScaleType.CENTER_INSIDE);
		mBitmap = EaseImageCache.getInstance().get("shop_image_details");
		if(mBitmap==null){
			Options opts= new Options();
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
				intent.putExtra(Constant.MESSAGE_TO_INTENT_EXTRA, Constant.MESSAGE_TO_AFTER_SALES);
				intent.setClass(ShopDetailsActivity.this, LoginActivity.class);
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		super.onStop();

	}
}
