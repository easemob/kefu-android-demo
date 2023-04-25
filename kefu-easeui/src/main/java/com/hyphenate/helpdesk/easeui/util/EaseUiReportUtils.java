package com.hyphenate.helpdesk.easeui.util;


import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.VecConfig;
import com.hyphenate.helpdesk.callback.ValueCallBack;
import com.hyphenate.helpdesk.util.Log;
import com.hyphenate.util.EMLog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EaseUiReportUtils {
    private final static String TAG = "CecReportDataUtils";
    private static EaseUiReportUtils sEaseUiReportDataUtils;
    private final ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private volatile boolean mIsCecStart = false;
    private volatile long mCecTime = 5;

    private volatile String mCecImServiceNumber;

    private EaseUiReportUtils(){}

    public static EaseUiReportUtils getEaseUiReportUtils() {
        if (sEaseUiReportDataUtils == null){
            synchronized (EaseUiReportUtils.class){
                if (sEaseUiReportDataUtils == null){
                    sEaseUiReportDataUtils = new EaseUiReportUtils();
                }
            }
        }

        return sEaseUiReportDataUtils;
    }


    public void startReport(String cecImServiceNumber){
        try{
            if (!VecConfig.newVecConfig().isEnableReport()){
                Log.e(TAG,"isEnableReport = false");
                ChatClient.getInstance().chatManager().asyncGetEnableReport(ChatClient.getInstance().tenantId(), new ValueCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Boolean value) {
                        Log.e(TAG,"onSuccess = "+value);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (value){
                                    start(cecImServiceNumber);
                                }else {
                                    mIsCecStart = false;
                                    mIsCecNeedReport = false;
                                    Log.e(TAG,"value = false");
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        mIsCecStart = false;
                        mIsCecNeedReport = false;
                        Log.e(TAG,"error");
                    }
                });
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        start(cecImServiceNumber);

    }


    private void start(String cecImServiceNumber){
        if (mIsCecStart){
            return;
        }
        EMLog.e(TAG,"cec startReport cecImServiceNumber = "+cecImServiceNumber);
        try{
            mCecTime = ChatClient.getInstance().getReportTimer();
        }catch (Exception e){
            e.printStackTrace();
            EMLog.e(TAG,"cec ChatClient.getInstance().getReportTimer() is error = "+e.getMessage());
        }
        this.mCecImServiceNumber = cecImServiceNumber;
        mIsCecStart = true;
        mIsCecNeedReport = true;
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                while (mIsCecStart){
                    sendReport();
                    SystemClock.sleep(mCecTime * 1000);
                    if (!mIsCecStart){
                        return;
                    }
                }
            }
        });
        Log.e(TAG,"startReport");
    }



    public void closeReport(){
        mIsCecStart = false;
        mIsCecNeedReport = false;
        sendCecOfflineReport();
    }


    private volatile boolean mIsCecNeedReport;
    // 进入到前台触发
    public void onPageForegroundReport(){
        if (mIsCecNeedReport && !mIsCecStart){
            startReport(mCecImServiceNumber);
        }
    }

    // 进入到后台触发
    public void onPageBackgroundReport(){
        if (mIsCecNeedReport){
            mIsCecStart = false;
            sendCecOfflineReport();
        }
    }


    private void sendCecOfflineReport(){
        Log.e(TAG,"sendCecOfflineReport");
        asyncCecOfflineReport(mCecImServiceNumber);
    }

    private void sendReport(){
        // 调用接口
        Log.e(TAG,"调用接口上报 mCecImServiceNumber = "+mCecImServiceNumber);
        ChatClient.getInstance().chatManager().sendCecReport(mCecImServiceNumber);

    }


    private void asyncCecOfflineReport(String imServiceNumber){
        try{
            ChatClient.getInstance().chatManager().asyncCecOfflineReport(ChatClient.getInstance().tenantId(),
                    ChatClient.getInstance().appKey().concat("#").concat(imServiceNumber), ChatClient.getInstance().currentUserName(), new ValueCallBack<String>() {
                        @Override
                        public void onSuccess(String value) {
                            Log.e(TAG,"cec asyncVecOfflineReport onSuccess = "+value);
                        }

                        @Override
                        public void onError(int error, String errorMsg) {
                            Log.e(TAG,"cec asyncVecOfflineReport error = "+errorMsg);
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
