package com.easemob.helpdeskdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.easemob.helpdeskdemo.R;

public class ShopFragment extends Fragment implements OnClickListener{
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.shop_fragment, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().findViewById(R.id.ib_shop_imageone).setOnClickListener(this);
		getActivity().findViewById(R.id.ib_shop_imagetwo).setOnClickListener(this);
		getActivity().findViewById(R.id.ib_shop_imagethree).setOnClickListener(this);
		getActivity().findViewById(R.id.ib_shop_imagefour).setOnClickListener(this);
		
		getActivity().findViewById(
				R.id.textview_customer).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), LoginActivity.class);
				startActivity(intent);
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClass(getActivity(), ShopDetailsActivity.class);
		switch (v.getId()) {
		case R.id.ib_shop_imageone:
			intent.putExtra("image", "2015早春新款高腰复古牛仔裙");
			intent.putExtra("price", "￥128");
			break;
		case R.id.ib_shop_imagetwo:
			intent.putExtra("image", "露肩名媛范套装");
			intent.putExtra("price", "￥518");
			break;
		case R.id.ib_shop_imagethree:
			intent.putExtra("image", "假两件衬衣+V领毛衣上衣");
			intent.putExtra("price", "￥235");
			break;
		case R.id.ib_shop_imagefour:
			intent.putExtra("image", "插肩棒球衫外套");
			intent.putExtra("price", "￥162");
			break;
		}
		startActivity(intent);
	}

}
