package com.easemob.veckit.help;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.graphics.Outline;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;


import com.easemob.veckit.R;
import com.easemob.veckit.ui.VecVebView;
import com.easemob.veckit.ui.webview.JsCall;
import com.hyphenate.chat.AgoraMessage;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.VecConfig;
import com.hyphenate.helpdesk.callback.ValueCallBack;
import com.hyphenate.helpdesk.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class PushMessageLink implements JsCall.IJsCallback {

    private VecVebView mVecVebView;
    private Handler mHandler = new Handler();
    private ProgressBar mProgressBar;
    private ViewGroup mPushView;
    private String mFlowId;
    private View mDrawAndDrawIcon;

    public PushMessageLink() {

    }

    public void init(String msgtype, ViewGroup pushView, View drawAndDrawIcon, Application context, int height) throws JSONException{
        mDrawAndDrawIcon = drawAndDrawIcon;
        mProgressBar = pushView.findViewById(R.id.progressBar);
        showAndHidden(mProgressBar, true);
        if (mDrawAndDrawIcon != null){
            showAndHidden(mDrawAndDrawIcon, false);
        }
        // 标识业务处理类型：LinkMessagePush 信息推送
        JSONObject msg = new JSONObject(msgtype);

        JSONObject linkMessagePush = msg.getJSONObject("infopush");

        // infopush_start
        String action = linkMessagePush.getString("action");

        mFlowId = linkMessagePush.getString("flowId");

        JSONObject content = linkMessagePush.getJSONObject("content");
        // 判断是否为url类型，富文本
        String type = content.getString("type");
        String title = content.getString("title");
        String url = content.getString("content");
        double heightRatio = content.getDouble("heightRatio");





        /*JSONObject linkMessagePush = msg.getJSONObject("infopush");

        String action = linkMessagePush.getString("action");
        String type = linkMessagePush.getString("type");
        mFlowId = linkMessagePush.getString("flowId");
        JSONObject content = linkMessagePush.getJSONObject("content");

        String title = content.getString("title");
        String url = content.getString("url");
        double heightRatio = content.getDouble("heightRatio");*/

        // 显示信息确认，webView
        mVecVebView = new VecVebView(context);
        clipToOutline(mVecVebView);
        mVecVebView.addJavascriptInterface(new JsCall(this),"closeMessagePush");
        setWebChromeClient(mVecVebView);
        Log.e("eeeeeeeeeee","地址 url = "+url);
        mVecVebView.loadUrl(url);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (height * heightRatio));
        layoutParams.gravity = Gravity.BOTTOM;
        mVecVebView.setLayoutParams(layoutParams);
        mPushView = pushView;
        mPushView.addView(mVecVebView, 0);
        showAndHidden(pushView, true);

    }

    private void setWebChromeClient(VecVebView vecVebView) {
        vecVebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showAndHidden(mProgressBar, false);
                    }
                });
            }

            /**
             * 这里进行无网络或错误处理，具体可以根据errorCode的值进行判断，做跟详细的处理。
             */
            // 旧版本，会在新版本中也可能被调用，所以加上一个判断，防止重复显示
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                //Log.e(TAG, "onReceivedError: ----url:" + error.getDescription());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    return;
                }
                // 在这里显示自定义错误页
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showAndHidden(mProgressBar, false);
                    }
                });
            }

            // 新版本，只会在Android6及以上调用
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showAndHidden(mProgressBar, false);
                    }
                });

                int errorCode = error.getErrorCode();
                // 断网或者网络连接超时
                if (errorCode == ERROR_HOST_LOOKUP || errorCode == ERROR_CONNECT || errorCode == ERROR_TIMEOUT) {
                    showDialog();
                }
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)//171016 处理404错误
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request,
                                            WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                // 这个方法在6.0才出现
                int statusCode = errorResponse.getStatusCode();
                if (404 == statusCode || 500 == statusCode) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showAndHidden(mProgressBar, false);
                        }
                    });
                    // view.loadUrl("about:blank");// 避免出现默认的错误界面
                    showDialog();
                }
            }
        });


        vecVebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                // 171016 处理404错误 android 6.0 以下通过title获取
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showAndHidden(mProgressBar, false);
                        }
                    });
                    if (title.contains("404") || title.contains("500") || title.contains("Error")) {
                        // view.loadUrl("about:blank");// 避免出现默认的错误界面
                        showDialog();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(String args) {
        JSONObject resultObj = new JSONObject();
        try {
            resultObj.put("flowId",mFlowId);
            resultObj.put("action","infopush_end");
            resultObj.put("content",args);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AgoraMessage.getAsyncVisitorIdAndVecSessionId(AgoraMessage.newAgoraMessage().getCurrentChatUsername(), new ValueCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                AgoraMessage.resultReporting(ChatClient.getInstance().tenantId(),
                        value, "infopush", resultObj, new ValueCallBack<String>() {
                            @Override
                            public void onSuccess(String value) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        clear();
                                    }
                                });
                            }

                            @Override
                            public void onError(int error, String errorMsg) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        clear();
                                    }
                                });
                            }
                        });
            }

            @Override
            public void onError(int error, String errorMsg) {
                AgoraMessage.resultReporting(ChatClient.getInstance().tenantId(),
                        "", "infopush", resultObj, new ValueCallBack<String>() {
                            @Override
                            public void onSuccess(String value) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        clear();
                                    }
                                });
                            }

                            @Override
                            public void onError(int error, String errorMsg) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        clear();
                                    }
                                });
                            }
                        });
            }

        });
    }

    private void showDialog(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    public void clear(){
        mIPushErrorCallback = null;
        if (mVecVebView != null){
            mVecVebView.destroy();
        }

        if (mPushView != null){
            mPushView.removeViewAt(0);
            showAndHidden(mPushView, false);
        }

        if (mProgressBar != null){
            showAndHidden(mProgressBar, false);
        }

        if (mDrawAndDrawIcon != null){
            showAndHidden(mDrawAndDrawIcon, true);
        }

        if (mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
        mDrawAndDrawIcon = null;
        mPushView = null;
        mVecVebView = null;
        mHandler = null;
    }

    private void showAndHidden(View view, boolean isShow) {
        if (view == null){
            return;
        }

        if (isShow && view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
            return;
        }

        if (!isShow && view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        }
    }

    private void clipToOutline(View surfaceView){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            surfaceView.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    Rect rect = new Rect();
                    view.getGlobalVisibleRect(rect);
                    int leftMargin = 0;
                    int topMargin = 0;
                    Rect selfRect = new Rect(leftMargin, topMargin,
                            rect.right - rect.left - leftMargin,
                            rect.bottom - rect.top - topMargin);
                    outline.setRoundRect(selfRect, dp2px(10, surfaceView.getContext()));
                }
            });
            surfaceView.setClipToOutline(true);
        }
    }

    private int dp2px(float dpValue, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    private IPushErrorCallback mIPushErrorCallback;

    public void setIPushErrorCallback(IPushErrorCallback callback){
        this.mIPushErrorCallback = callback;
    }

    interface IPushErrorCallback{
        void onPushError();
    }
}
