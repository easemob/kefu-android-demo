package com.easemob.helpdeskdemo.activity;

import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.R.layout;
import com.easemob.helpdeskdemo.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ShopDetailsActivity extends Activity {
	private ImageView mImageView;
	private RelativeLayout rl;
	private String stnumber,stprice;
	private ImageButton mImageButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_details);
		stnumber = getIntent().getStringExtra("image");
		stprice = getIntent().getStringExtra("price");
		
		rl = (RelativeLayout) findViewById(R.id.rl_tochat);
		mImageButton = (ImageButton) findViewById(R.id.ib_shop_back);
		mImageView = (ImageView) findViewById(R.id.iv_buy);
		mImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		mImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShopDetailsActivity.this.finish();
			}
		});
		rl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
//				Bundle bundle = new Bundle();
//				bundle.putString("image", stnumber);
//				bundle.putString("price", stprice);
//				intent.putExtras(bundle);
				intent.putExtra("image", stnumber);
				intent.putExtra("price", stprice);
				intent.setClass(ShopDetailsActivity.this, LoginActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_shop_details, menu);
		return true;
	}

}
