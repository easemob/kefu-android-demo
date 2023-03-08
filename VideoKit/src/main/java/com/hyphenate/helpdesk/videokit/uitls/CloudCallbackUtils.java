package com.hyphenate.helpdesk.videokit.uitls;

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

    public void notifyShow(){
        if (mICloudCallback != null){
            mICloudCallback.onShowCloudActivity();
        }
    }

    public void removeICloudCallback(){
        mICloudCallback = null;
    }

    public interface ICloudCallback{
        void onShowCloudActivity();
        void onActivityResult(int requestCode, int resultCode, String path);
    }
}
