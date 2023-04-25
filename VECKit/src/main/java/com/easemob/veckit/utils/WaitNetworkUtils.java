package com.easemob.veckit.utils;

import android.os.SystemClock;

import com.hyphenate.chat.AgoraMessage;
import com.hyphenate.helpdesk.callback.ValueCallBack;
import com.hyphenate.helpdesk.util.Log;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WaitNetworkUtils {
    private static WaitNetworkUtils sWaitNetworkUtils;
    private ExecutorService mSendThreadPool;
    private volatile boolean mIsRun;
    private WaitNetworkUtils(){}
    public static WaitNetworkUtils newWaitNetworkUtils(){
        if (sWaitNetworkUtils == null){
            synchronized (WaitNetworkUtils.class){
                if (sWaitNetworkUtils == null){
                    sWaitNetworkUtils = new WaitNetworkUtils();
                }
            }
        }

        return sWaitNetworkUtils;
    }

    public void execute(String rtcSessionId, String tenantId, IWaitCallBack callBack){
        if (mSendThreadPool == null){
            mSendThreadPool = Executors.newSingleThreadExecutor();
        }
        if (!mIsRun){
            mIsRun = true;
            mSendThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    while (mIsRun){
                        if (!mIsRun){
                            break;
                        }
                        getWaitNumber(rtcSessionId, tenantId, callBack);
                        if (mIsRun){
                            SystemClock.sleep(3000);
                        }else {
                            break;
                        }
                    }
                    mIsRun = false;
                }
            });
        }

    }

    private void getWaitNumber(String rtcSessionId, String tenantId, IWaitCallBack callBack){
        AgoraMessage.getWaitNumber(tenantId, rtcSessionId, new ValueCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                // {"status":"OK","entity":{"visitorWaitingNumber":"您目前排在第1位。","waitingFlag":"true","visitorWaitingTimestamp":"1657090678951"}}
                try {
                    if (callBack != null){
                        JSONObject jsonObject = new JSONObject(value);
                        JSONObject entity = jsonObject.getJSONObject("entity");
                        boolean waitingFlag = entity.getBoolean("waitingFlag");
                        String visitorWaitingNumber = entity.getString("visitorWaitingNumber");
                        mIsRun = waitingFlag;
                        callBack.onWaitData(waitingFlag, visitorWaitingNumber, rtcSessionId);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    callBack.onWaitError(e.toString());
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                if (callBack != null){
                    callBack.onWaitError(errorMsg);
                }
            }
        });
    }

    public void stop(){
        mIsRun = false;
    }

    public void clear(){
        try {
            mIsRun = false;
            if (mSendThreadPool != null){
                mSendThreadPool.shutdown();
                mSendThreadPool = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public interface IWaitCallBack{
        void onWaitData(boolean waitingFlag, String visitorWaitingNumber, String session);
        void onWaitError(String errorMsg);
    }
}
