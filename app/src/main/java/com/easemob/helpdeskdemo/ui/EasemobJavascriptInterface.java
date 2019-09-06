package com.easemob.helpdeskdemo.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.easemob.helpdeskdemo.R;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.easeui.widget.ToastHelper;

/**
 * Created by liyuzhao on 15/12/2016.
 */


@SuppressLint("JavascriptInterface")
public class EasemobJavascriptInterface {

    private Context mContext;

    public EasemobJavascriptInterface(Context context) {
        this.mContext = context;
    }

    //webview中调用toast原生组件
    @JavascriptInterface
    public void showToast(String toast) {
        ToastHelper.show(mContext, toast);
    }

    @JavascriptInterface
    public void closeWindow() {
        if (mContext instanceof Activity) {
            ((Activity) mContext).finish();
            ((Activity) mContext).overridePendingTransition(0, R.anim.em_activity_close);
        }
    }

    @JavascriptInterface
    public String imToken(){
        return ChatClient.getInstance().accessToken();
    }
}
