package com.easemob.helpdeskdemo.activity;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebHistoryItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.applib.model.HistoryModel;
import com.easemob.helpdeskdemo.R;

public class ShopFragment extends Fragment implements OnClickListener{
//	private WebView webView;
	private String URL;
	private TextView mTextView;
	private ImageView mImageView1,mImageView2,mImageView3,mImageView4;

	static Map<String,SoftReference<Bitmap>> imageCache = new HashMap<String,SoftReference<Bitmap>>();
	
	private Button btnHistory;
	private List<HistoryModel> historyModels = new ArrayList<HistoryModel>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.shop_fragment, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mTextView = (TextView) getActivity().findViewById(
				R.id.textview_customer);
		mImageView1 = (ImageView) getActivity().findViewById(R.id.ib_shop_imageone);
		mImageView2 = (ImageView) getActivity().findViewById(R.id.ib_shop_imagetwo);
		mImageView3 = (ImageView) getActivity().findViewById(R.id.ib_shop_imagethree);
		mImageView4 = (ImageView) getActivity().findViewById(R.id.ib_shop_imagefour);
		mImageView1.setOnClickListener(this);
		mImageView2.setOnClickListener(this);
		mImageView3.setOnClickListener(this);
		mImageView4.setOnClickListener(this);
		
		mTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(getActivity(), LoginActivity.class);
				//对图片进行回收
//				bitmapDrawable1 = (BitmapDrawable) mImageView1.getDrawable();
//				bitmapDrawable2 = (BitmapDrawable) mImageView2.getDrawable();
//				bitmapDrawable3 = (BitmapDrawable) mImageView3.getDrawable();
//				bitmapDrawable4 = (BitmapDrawable) mImageView4.getDrawable();
				startActivity(intent);
			}
		});
	}
//		webView = (WebView) getActivity().findViewById(R.id.webView);

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.ib_shop_imageone:
			intent.putExtra("image", "2015早春新款高腰复古牛仔裙");
			intent.putExtra("price", "￥128");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(getActivity(), ShopDetailsActivity.class);
			startActivity(intent);
			break;
		case R.id.ib_shop_imagetwo:
			intent.putExtra("image", "露肩名媛范套装");
			intent.putExtra("price", "￥518");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(getActivity(), ShopDetailsActivity.class);
			startActivity(intent);
			break;
		case R.id.ib_shop_imagethree:
			intent.putExtra("image", "假两件衬衣+V领毛衣上衣");
			intent.putExtra("price", "￥235");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(getActivity(), ShopDetailsActivity.class);
			startActivity(intent);
			break;
		case R.id.ib_shop_imagefour:
			intent.putExtra("image", "插肩棒球衫外套");
			intent.putExtra("price", "￥162");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(getActivity(), ShopDetailsActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

//		webView.setFocusable(true);// 这个和下面的这个命令必须要设置了，才能监听back事件。
//		webView.setFocusableInTouchMode(true);
//		webView.setOnKeyListener(backlistener);
//
//		// 加载需要显示的网页
//		webView.loadUrl("http://www.gome.com.cn/");
//		// 设置文件支持放大缩小
//		// webView.getSettings().setSupportZoom(true);
//		webView.setWebViewClient(webViewClient);
//		WebSettings s = webView.getSettings();
//		s.setBuiltInZoomControls(true);
//		s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
//		s.setUseWideViewPort(true);
//		s.setLoadWithOverviewMode(true);
//		s.setSavePassword(true);
//		s.setSaveFormData(true);
//		s.setJavaScriptEnabled(true);
//		// enable navigator.geolocation
//		s.setGeolocationEnabled(true);
//		s.setDomStorageEnabled(true);
//		webView.requestFocus();
//		webView.setScrollBarStyle(0);
//
//		webView.setWebViewClient(new BirtMobileWebViewClient());
//		btnHistory = (Button) getView().findViewById(R.id.btnHistory);
//		btnHistory.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Toast.makeText(getActivity(), historyModels.toString(), 1)
//						.show();
//			}
//		});
//	}
//
//	WebViewClient webViewClient = new WebViewClient() {
//		public boolean shouldOverrideUrlLoading(WebView view, String url) {
//			URL = url;
//			webView.loadUrl(url);
//			return false;
//		}
//
//		public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
//			return super.shouldOverrideKeyEvent(view, event);
//		}
//
//		public void onReceivedError(WebView view, int errorCode,
//				String description, String failingUrl) {
//			handler.sendMessage(handler.obtainMessage(404, null));
//		};
//	};
//	Handler handler = new Handler() {
//		public void handleMessage(android.os.Message msg) {
//			switch (msg.what) {
//			case 404:
//				webView.loadData("<H1>404  找不带指定页面</h1>", null, "utf-8,gbk");
//				break;
//			case 100:
//				webView.loadUrl(URL);
//				break;
//			case 200:
//				URL = msg.obj.toString();
//				webView.loadUrl(URL);
//			}
//		};
//	};
//
//	private View.OnKeyListener backlistener = new View.OnKeyListener() {
//		@Override
//		public boolean onKey(View view, int i, KeyEvent keyEvent) {
//			if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
//				if (i == KeyEvent.KEYCODE_BACK) { // 表示按返回键 时的操作
//					if (i == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
//						webView.goBack();// 返回前一个页面
//						return true;
//					}
//					return false; // 已处理
//				}
//			}
//			return false;
//		}
//	}; 
//
//	private int count = 0;
//	// create a webview client that handles mailto links within the webview
//	private class BirtMobileWebViewClient extends WebViewClient {
//		@Override
//		public boolean shouldOverrideUrlLoading(WebView view, String url) {
//			return false;
//		}
//
//		@Override
//		public void onPageStarted(WebView view, String url, Bitmap favicon) {
//			//每个页面加载的时间
//		}
//
//		@Override
//		public void onPageFinished(WebView view, String url) {
//			HistoryModel historyModel = new HistoryModel();
//			// 获取浏览器history，包含id,name,url等
//			WebHistoryItem item = webView.copyBackForwardList()
//			.getCurrentItem();
//			historyModel.setName(item.getTitle());
//			historyModel.setUrl(item.getUrl());
//			historyModels.add(historyModel);
//		}
//	};
	
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		if(bitmapDrawable1!=null&&!bitmapDrawable1.getBitmap().isRecycled()){
//		    bitmapDrawable1.getBitmap().recycle();
//		}
//		if(bitmapDrawable2!=null&&!bitmapDrawable2.getBitmap().isRecycled()){
//		    bitmapDrawable2.getBitmap().recycle();
//		}
//		if(bitmapDrawable3!=null&&!bitmapDrawable3.getBitmap().isRecycled()){
//		    bitmapDrawable3.getBitmap().recycle();
//		}
//		if(bitmapDrawable4!=null&&!bitmapDrawable4.getBitmap().isRecycled()){
//		    bitmapDrawable4.getBitmap().recycle();
//		}
//	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		for (SoftReference<Bitmap> item : imageCache.values()) {
			item.get().recycle();
			item = null;
		}
		imageCache.clear();
		System.gc();
		
		
		
	}

}
