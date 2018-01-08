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
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.R;

import java.util.Locale;

public class ShopDetailsActivity extends DemoBaseActivity {
	private RelativeLayout rl_tochat;
	private RelativeLayout mImageButton;
	private ImageView iv_buy_1;
	private ImageView iv_buy_2;
	private ImageView iv_buy_3;
	private int index = Constant.INTENT_CODE_IMG_SELECTED_DEFAULT;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.em_activity_shop_details);
		index = getIntent().getIntExtra(Constant.INTENT_CODE_IMG_SELECTED_KEY, Constant.INTENT_CODE_IMG_SELECTED_DEFAULT);
		
		rl_tochat = $(R.id.rl_tochat);
		mImageButton = $(R.id.rl_back);
		iv_buy_1 = $(R.id.iv_buy_part1);
		iv_buy_2 = $(R.id.iv_buy_part2);
		iv_buy_3 = $(R.id.iv_buy_part3);

		//长图分图显示
		setLongPicRes();

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

	private void setLongPicRes() {
		int iImageView1;
		int iImageView2;
		int iImageView3;
		String sImageView1;
		String sImageView2;
		String sImageView3;

		sImageView1 = String.format(Locale.getDefault(),"product_details_%d_a", index);
		sImageView2 = String.format(Locale.getDefault(),"product_details_%d_b", index);
		sImageView3 = String.format(Locale.getDefault(),"product_details_%d_c", index);

		iImageView1 = getResources().getIdentifier(sImageView1, "drawable",getPackageName());
		iImageView2 = getResources().getIdentifier(sImageView2, "drawable",getPackageName());
		iImageView3 = getResources().getIdentifier(sImageView3, "drawable",getPackageName());

		if (iImageView1 != 0) {
			iv_buy_1.setImageResource(iImageView1);
		}
		if (iImageView2 != 0) {
			iv_buy_2.setImageResource(iImageView2);
		}
		if (iImageView3 != 0) {
			iv_buy_3.setImageResource(iImageView3);
		}
	}

	@Override
	protected void onDestroy() {
		//释放长图资源避免OOM
		iv_buy_1.setImageDrawable(null);
		iv_buy_2.setImageDrawable(null);
		iv_buy_3.setImageDrawable(null);
		System.gc();
		super.onDestroy();
	}
}
