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
	
	public static final int INTENT_CODE_IMG_SELECTED_1 = 1;
	public static final int INTENT_CODE_IMG_SELECTED_2 = 2;
	public static final int INTENT_CODE_IMG_SELECTED_3 = 3;
	public static final int INTENT_CODE_IMG_SELECTED_4 = 4;
	
	public static final String INTENT_CODE_IMG_SELECTED_KEY = "img_selected";
	
	
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
			intent.putExtra(INTENT_CODE_IMG_SELECTED_KEY, INTENT_CODE_IMG_SELECTED_1);
			break;
		case R.id.ib_shop_imagetwo:
			intent.putExtra(INTENT_CODE_IMG_SELECTED_KEY, INTENT_CODE_IMG_SELECTED_2);
			break;
		case R.id.ib_shop_imagethree:
			intent.putExtra(INTENT_CODE_IMG_SELECTED_KEY, INTENT_CODE_IMG_SELECTED_3);
			break;
		case R.id.ib_shop_imagefour:
			intent.putExtra(INTENT_CODE_IMG_SELECTED_KEY, INTENT_CODE_IMG_SELECTED_4);
			break;
		}
		startActivity(intent);
	}

}
