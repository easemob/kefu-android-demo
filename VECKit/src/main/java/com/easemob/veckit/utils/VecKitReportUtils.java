package com.easemob.veckit.utils;


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

public class VecKitReportUtils {
    private final static String TAG = "VecKitReportDataUtils";
    private static VecKitReportUtils sVecKitReportUtils;
    private final ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private volatile boolean mIsStart = false;
    private volatile long mTime = 5;
    private VecKitReportUtils(){}

    public static VecKitReportUtils getVecKitReportUtils() {
        if (sVecKitReportUtils == null){
            synchronized (VecKitReportUtils.class){
                if (sVecKitReportUtils == null){
                    sVecKitReportUtils = new VecKitReportUtils();
                }
            }
        }
        return sVecKitReportUtils;
    }


    private volatile String mVecImServiceNumber;
    public void startReport(String vecImServiceNumber){
        try{
            Log.e(TAG,"VecConfig.newVecConfig().isEnableReport() = "+VecConfig.newVecConfig().isEnableReport());
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
                                    start(mVecImServiceNumber);
                                }else {
                                    mIsStart = false;
                                    mIsNeedReport = false;
                                    Log.e(TAG,"value = false");
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        mIsStart = false;
                        mIsNeedReport = false;
                        Log.e(TAG,"error");
                    }
                });
                return;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        start(vecImServiceNumber);
    }


    private void start(String vecImServiceNumber){
        Log.e(TAG,"vec report start mIsStart = "+mIsStart);
        if (mIsStart){
            return;
        }
        this.mVecImServiceNumber = vecImServiceNumber;
        EMLog.e(TAG,"vec startReport mVecImServiceNumber = "+mVecImServiceNumber);
        Log.e("VecKitReportDataUtils","vec startReport mVecImServiceNumber = "+mVecImServiceNumber);
        try{
            mTime = ChatClient.getInstance().getReportTimer();
        }catch (Exception e){
            e.printStackTrace();
            EMLog.e(TAG,"vec ChatClient.getInstance().getReportTimer() is error = "+e.getMessage());
        }
        mIsStart = true;
        mIsNeedReport = true;
        mExecutorService.execute(() -> {
            Log.e("VecKitReportDataUtils","vec startReport 执行线程 mVecImServiceNumber = "+mVecImServiceNumber+", mIsStart = "+mIsStart);
            while (mIsStart){
                // 调用接口
                sendReport();
                SystemClock.sleep(mTime * 1000);
                if (!mIsStart){
                    return;
                }
            }
        });
        Log.e(TAG,"vec startReport");
    }


    public void closeReport(){
        Log.e(TAG,"vec closeReport");
        mIsStart = false;
        mIsNeedReport = false;
        sendVecOfflineReport();
    }

    private volatile boolean mIsNeedReport;
    // 前台上报
    public void onPageForegroundReport(){
        Log.e(TAG,"vec onPageForegroundReport mIsNeedReport = "+mIsNeedReport);
        if (mIsNeedReport && !mIsStart){
            Log.e(TAG,"vec onPageForegroundReport mIsNeedReport = "+mIsNeedReport+", mIsStart = "+mIsStart);
            startReport(mVecImServiceNumber);
        }
    }

    // 后台上报
    public void onPageBackgroundReport(){
        Log.e(TAG,"vec onPageBackgroundReport mIsNeedReport = "+mIsNeedReport);
        if (mIsNeedReport){
            mIsStart = false;
            Log.e(TAG,"vec onPageBackgroundReport mIsNeedReport = "+mIsNeedReport+", mIsStart = "+mIsStart);
            sendVecOfflineReport();
        }
    }


    // 视频页面 进入前台上报
    public void acceptVecVideoForegroundReport(){
        startReport(mVecImServiceNumber);
    }

    // 视频页面 进入后台上报
    public void acceptVecVideoBackgroundReport(){
        mIsStart = false;
        sendVecOfflineReport();
    }


    private void sendReport(){
        Log.e(TAG,"vec sendReport");
        ChatClient.getInstance().chatManager().sendVecReport(mVecImServiceNumber);
    }

    private void sendVecOfflineReport(){
        Log.e(TAG,"vec 离线");
        try{
            ChatClient.getInstance().chatManager().asyncVecOfflineReport(ChatClient.getInstance().tenantId(),
                    ChatClient.getInstance().appKey().concat("#").concat(mVecImServiceNumber), ChatClient.getInstance().currentUserName(), new ValueCallBack<String>() {
                        @Override
                        public void onSuccess(String value) {
                            Log.e(TAG,"vec asyncVecOfflineReport onSuccess = "+value);
                        }

                        @Override
                        public void onError(int error, String errorMsg) {
                            Log.e(TAG,"vec asyncVecOfflineReport error = "+errorMsg);
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
