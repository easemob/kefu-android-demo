package com.easemob.veckit.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.WebMessage;
import android.webkit.WebSettings;
import android.webkit.WebView;


public class VecVebView extends WebView {
    private String APP_CACHE_DIRNAME;

    public VecVebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VecVebView(Context context) {
        super(context);
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init(){
        /*getSettings().setMediaPlaybackRequiresUserGesture(false);
        loadUrl("file:///android_asset/whiteboard/index.html");
        setWebChromeClient(new WebChromeClient());*/

        APP_CACHE_DIRNAME = getContext().getFilesDir().getAbsolutePath() + "/webcache";
        WebSettings settings = getSettings();
        settings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        /*//容许h5使用javascript
        webView.getSettings().setJavaScriptEnabled(true);
        //容许android调用javascript
        webView.getSettings().setDomStorageEnabled(true);*/

        settings.setAllowFileAccess(false);
        settings.setAppCacheEnabled(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setAppCachePath(APP_CACHE_DIRNAME);
        settings.setUseWideViewPort(true);
    }

    @Override
    public void postWebMessage(WebMessage message, Uri targetOrigin) {
        super.postWebMessage(message, targetOrigin);

    }

}
