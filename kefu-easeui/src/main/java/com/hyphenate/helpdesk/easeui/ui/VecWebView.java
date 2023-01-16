package com.hyphenate.helpdesk.easeui.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class VecWebView extends WebView {
    private String APP_CACHE_DIRNAME;
    private int mHeight;
    private float mDownX;
    private float mDownY;
    private float mMoveX;
    private float mMoveY;
    private boolean mIsDown;
    private boolean mIsRun;

    public VecWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VecWebView(Context context) {
        super(context);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
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
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        setWebViewClient(new TestWebViewClient());

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setOnScrollChangeListener(new OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    float currentHeight=(v.getHeight() + v.getScrollY());
                    Log.e("ttttttttttt","scrollY = "+scrollY);
                    Log.e("ttttttttttt","oldScrollY = "+oldScrollY);
                    Log.e("ttttttttttt","getScrollY aaaaaaa = "+v.getScrollY());

                    Log.e("ttttttttttt","v instanceof WebView = "+(v instanceof WebView));

                    WebView view = (WebView) v;
                    Log.e("ttttttttttt","Math.ceil(getContentHeight() * getScale()) = "+Math.ceil(view.getContentHeight() * view.getScale()));
                    Log.e("ttttttttttt","currentHeight = "+currentHeight);
                    Log.e("ttttttttttt"," Math.floor(getContentHeight() * getScale()) = "+ Math.floor(view.getContentHeight() * view.getScale()));
                }
            });
        }*/


        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ViewParent parent = getParent();
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }
                Log.e("ttttttttttt","parent = "+parent);
                /*if ((int) (getContentHeight() * getScale()) >= (getHeight() + getScrollY())) {
                    //滑动到底部，你要做的事·····
                    Log.e("ttttttttttt","qqqqqqqqq");
                }

                if (getScrollY() <= 0) {
                    //滑动到顶部，你要做的事····
                    Log.e("ttttttttttt","aaaaaaaaa");
                }*/

                mIsRun = false;
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    mDownX = event.getX();
                    mDownY = event.getY();
                }else if (event.getAction() == MotionEvent.ACTION_MOVE){
                    mMoveX = event.getX();
                    mMoveY = event.getY();

                    if (Math.abs(mMoveY - mDownY) > Math.abs(mMoveX - mDownX)){
                        if (Math.abs((mMoveY - mDownY)) > 2){
                            mIsRun = true;
                            // 正 --》向下滑动
                            mIsDown = mMoveY - mDownY > 0;
                        }
                    }

                    mDownX = mMoveX;
                    mDownY = mMoveY;
                }
                float currentHeight=(getHeight() + getScrollY());
                Log.e("ttttttttttt","Math.ceil(getContentHeight() * getScale()) = "+Math.ceil(getContentHeight() * getScale()));
                Log.e("ttttttttttt","currentHeight = "+currentHeight);
                Log.e("ttttttttttt"," Math.floor(getContentHeight() * getScale()) = "+ Math.floor(getContentHeight() * getScale()));
                /*if (mIsRun){
                    if (mIsDown){
                        if (getScrollY() == 0){
                            Log.e("ttttttttttt","顶部");
                            if (parent != null) {
                                parent.requestDisallowInterceptTouchEvent(false);
                            }
                        }
                    }else {

                        if(currentHeight <= Math.ceil(getContentHeight() * getScale())
                                && currentHeight >= Math.floor(getContentHeight() * getScale() - 6)){
                            Log.e("ttttttttttt","底部");
                            if (parent != null) {
                                parent.requestDisallowInterceptTouchEvent(false);
                            }
                        }
                    }
                }*/
                return false;
            }
        });
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //WebView的总高度
        float webViewContentHeight=getContentHeight() * getScale();
        //WebView的现高度
        float currentHeight=(getHeight() + getScrollY());
        /*Log.e("ttttttttttt","t = "+t);
        Log.e("ttttttttttt","oldt = "+oldt);
        Log.e("ttttttttttt","webViewContentHeight = "+webViewContentHeight);
        Log.e("ttttttttttt","currentHeight = "+currentHeight);*/
        /*if ((webViewContentHeight-currentHeight) == 0) {
            Log.e("ttttttttttt","WebView滑动到了底端");
        }*/

        /*if (t == 0){
            ViewParent parent = getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(false);
            }
        }else if (currentHeight == mHeight){
            ViewParent parent = getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(false);
            }
        }*/

    }




    private class TestWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
        @Override
        public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

}
