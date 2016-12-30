package com.easemob.helpdeskdemo.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.easemob.helpdeskdemo.R;

/**
 * Created by liyuzhao on 15/12/2016.
 */

public class ForwardWebViewActivity extends DemoBaseActivity {

    private WebView mWebView;
    private ProgressBar mPB;
    private String targetUrl;
    private EasemobJavascriptInterface emInterface;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadMessageForAndroid5;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private final static int FILECHOOSER_RESULTCODE_FOR_ANDORID_5 = 2;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_forward);
        targetUrl = getIntent().getStringExtra("url");
        mWebView = (WebView) findViewById(R.id.webview);
        mPB = (ProgressBar) findViewById(R.id.pb);
        mPB.setVisibility(View.VISIBLE);
        emInterface = new EasemobJavascriptInterface(this);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);//滚动条风格，为0指滚动条不占用空间，直接覆盖在网页上
        WebSettings settings = mWebView.getSettings();
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDomStorageEnabled(true);
        settings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING); //支持内容重新布局
        }
        settings.setAllowFileAccess(true); //设置可以访问文件
        settings.setLoadWithOverviewMode(true); //缩放至屏幕的大小
        settings.setLoadsImagesAutomatically(true); //支持自动加载图片

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {//载入进度改变而触发
                if (newProgress == 100){
//                    handler.sendEmptyMessage(1);// 如果全部载入,隐藏进度对话框
                    mPB.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onCloseWindow(WebView window) {
                super.onCloseWindow(window);
                emInterface.closeWindow();
            }

            //扩展浏览器上传文件
            //3.0++版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType){
                openFileChooserImpl(uploadMsg);
            }
            //3.0--版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg){
                openFileChooserImpl(uploadMsg);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
                openFileChooserImpl(uploadMsg);
            }

            // For Android > 5.0
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> uploadMsg, FileChooserParams fileChooserParams){
                openFileChooserImplForAndroid5(uploadMsg);
                return true;
            }

        });

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWebView.addJavascriptInterface(emInterface, "easemob");
        mWebView.loadUrl(targetUrl);

//        mWebView.loadUrl("http://www.baidu.com");
    }

    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg){
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
    }

    private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg){
        mUploadMessageForAndroid5 = uploadMsg;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDORID_5);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == FILECHOOSER_RESULTCODE){
            if(null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }else if(requestCode == FILECHOOSER_RESULTCODE_FOR_ANDORID_5){
            if(null == mUploadMessageForAndroid5)
                return;
            Uri result = (intent == null || resultCode != RESULT_OK) ? null: intent.getData();
            if(result != null){
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            }else{
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            }
            mUploadMessageForAndroid5 = null;
        }



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.em_activity_close);

    }
}
