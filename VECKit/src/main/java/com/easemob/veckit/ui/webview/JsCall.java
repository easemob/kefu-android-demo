package com.easemob.veckit.ui.webview;

import android.webkit.JavascriptInterface;

import com.hyphenate.helpdesk.util.Log;

public class JsCall {

    public JsCall(IJsCallback callback){
        this.mCallback = callback;
    }

    @JavascriptInterface
    public void postMessage(String args){
        if (mCallback != null){
            mCallback.onClick(args);
        }
    }

    private IJsCallback mCallback;

    public void clear(){
        mCallback = null;
    }

    public interface IJsCallback{
        void onClick(String args);
    }
}
