package com.hyphenate.helpdesk.easeui.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;

/**
 * Created by tiancruyff on 2017/5/11.
 */

public class ImageGridActivity extends BaseActivity {
	private static final String TAG = "ImageGridActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
			final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.add(android.R.id.content, new ImageGridFragment(), TAG);
			ft.commit();
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

}
