package com.easemob.helpdeskdemo.activity;

import java.lang.ref.SoftReference;

import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.R.layout;
import com.easemob.helpdeskdemo.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ShopDetailsActivity extends Activity {
	private ImageView mImageView;
	private RelativeLayout rl;
	private String stnumber,stprice;
	private ImageButton mImageButton;
	private SoftReference<Bitmap> softBitmap=null;
	private Bitmap mBitmap = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_details);
		stnumber = getIntent().getStringExtra("image");
		stprice = getIntent().getStringExtra("price");
		
		rl = (RelativeLayout) findViewById(R.id.rl_tochat);
		mImageButton = (ImageButton) findViewById(R.id.ib_shop_back);
		mImageView = (ImageView) findViewById(R.id.iv_buy);
		mImageView.setScaleType(ScaleType.CENTER_INSIDE);
		softBitmap = ShopFragment.imageCache.get("shop_image_details");
		if(softBitmap==null||softBitmap.get()==null){
			Options opts= new Options();
			opts.inSampleSize =2;
			mBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.shop_image_details);
			ShopFragment.imageCache.put("shop_image_details", new SoftReference<Bitmap>(mBitmap));
			mImageView.setImageBitmap(mBitmap);
		}else{
			mImageView.setImageBitmap(softBitmap.get());
		}
		
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
//				BitmapDrawable bd = (BitmapDrawable) mImageView.getBackground();
//				if(bd.getBitmap()!=null){
//					bd.getBitmap().recycle();
//				}
				
//				bitmapDrawable = (BitmapDrawable) mImageView.getDrawable();
//				Bitmap bitmap = bitmapDrawable.getBitmap();
//			
				
				
				
				
				//如果图片还未回收，先强制回收该图片
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		if(softBitmap!=null&&!softBitmap.get().isRecycled()){
//			softBitmap.get().recycle();
//		}
//		if(mBitmap!=null&&!mBitmap.isRecycled()){
//			mBitmap.recycle();
//			mBitmap = null;
//		}
	}
	
}
