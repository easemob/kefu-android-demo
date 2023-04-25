package com.hyphenate.helpdesk.easeui.util;

import android.app.Application;

public class EaseUiStateUtils implements EaseUiStateCallback.IEaseUiStateCallback {
    private static EaseUiStateUtils sEaseUiStateUtils;
    public static EaseUiStateUtils getEaseUiStateUtils() {
        if (sEaseUiStateUtils == null){
            synchronized (EaseUiStateUtils.class){
                if (sEaseUiStateUtils == null){
                    sEaseUiStateUtils = new EaseUiStateUtils();
                }
            }
        }
        return sEaseUiStateUtils;
    }


    public void init(Application context){
        EaseUiStateCallback.init(context);
        EaseUiStateCallback.getEaseUiStateCallback().registerIAppStateEaseUiCallback(this);
    }

    @Override
    public void onAppForeground() {
        EaseUiReportUtils.getEaseUiReportUtils().onPageForegroundReport();
    }

    @Override
    public void onAppBackground() {
        EaseUiReportUtils.getEaseUiReportUtils().onPageBackgroundReport();
    }
}
