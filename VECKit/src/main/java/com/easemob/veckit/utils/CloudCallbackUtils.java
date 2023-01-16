package com.easemob.veckit.utils;

import android.content.Intent;

import com.hyphenate.chat.Message;

public class CloudCallbackUtils {
    private static CloudCallbackUtils sCloudCallbackUtils;
    private ICloudCallback mICloudCallback;
    public static CloudCallbackUtils newCloudCallbackUtils(){
        if (sCloudCallbackUtils == null){
            synchronized (CloudCallbackUtils.class){
                if (sCloudCallbackUtils == null){
                    sCloudCallbackUtils = new CloudCallbackUtils();
                }
            }
        }

        return sCloudCallbackUtils;
    }


    public void addICloudCallback(ICloudCallback iCloudCallback){
        this.mICloudCallback = iCloudCallback;
    }

    public void notifyUri(int requestCode, int resultCode, String path){
        if (mICloudCallback != null){
            mICloudCallback.onActivityResult(requestCode, resultCode, path);
        }
    }

    public void notifyUri(int requestCode, int resultCode, Intent data){
        if (mICloudCallback != null){
            mICloudCallback.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void notifyShow(){
        if (mICloudCallback != null){
            mICloudCallback.onShowCloudActivity();
        }
    }

    public void updateNav(int height, boolean isBack){
        if (mICloudCallback != null){
            mICloudCallback.onUpdateNav(height, isBack);
        }
    }

    public void removeICloudCallback(){
        mICloudCallback = null;
    }

    public interface ICloudCallback{
        void onUpdateNav(int height, boolean isBack);
        void onShowCloudActivity();
        void onActivityResult(int requestCode, int resultCode, String path);
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }
}
