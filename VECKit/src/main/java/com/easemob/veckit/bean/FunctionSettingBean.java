package com.easemob.veckit.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.hyphenate.helpdesk.model.Content;

public class FunctionSettingBean {
    private boolean visitorCameraOff;
    private boolean skipWaitingPage;

    public FunctionSettingBean(){

    }

    public static FunctionSettingBean create(){
        return new FunctionSettingBean(false, false);
    }

    public FunctionSettingBean(boolean visitorCameraOff, boolean skipWaitingPage){
        this.visitorCameraOff = visitorCameraOff;
        this.skipWaitingPage = skipWaitingPage;
    }


    public boolean isVisitorCameraOff() {
        return visitorCameraOff;
    }

    public void setVisitorCameraOff(boolean visitorCameraOff) {
        this.visitorCameraOff = visitorCameraOff;
    }

    public boolean isSkipWaitingPage() {
        return skipWaitingPage;
    }

    public void setSkipWaitingPage(boolean skipWaitingPage) {
        this.skipWaitingPage = skipWaitingPage;
    }

    @Override
    public String toString() {
        return "{" +
                "visitorCameraOff=" + visitorCameraOff +
                ", skipWaitingPage=" + skipWaitingPage +
                '}';
    }
}
