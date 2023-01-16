package com.easemob.veckit.bean;


import android.content.Context;


public class VideoStyleBean{
    private ChannelBean channel;
    private FunctionSettingBean functionSettings;
    private StyleSettingBean styleSettings;

    public static VideoStyleBean create(Context content){
        return new VideoStyleBean(FunctionSettingBean.create(), StyleSettingBean.create(content));
    }

    public VideoStyleBean(){

    }

    public VideoStyleBean(FunctionSettingBean functionSettings, StyleSettingBean styleSettingBean){
        this.functionSettings = functionSettings;
        this.styleSettings = styleSettingBean;
    }

    public FunctionSettingBean getFunctionSettings() {
        return functionSettings;
    }

    public void setFunctionSettings(FunctionSettingBean functionSettings) {
        this.functionSettings = functionSettings;
    }

    public StyleSettingBean getStyleSettings() {
        return styleSettings;
    }

    public void setStyleSettings(StyleSettingBean styleSettings) {
        this.styleSettings = styleSettings;
    }

    @Override
    public String toString() {
        return "VideoStyleBean{" +
                "channel=" + channel +
                ", functionSettings=" + functionSettings +
                ", styleSettings=" + styleSettings +
                '}';
    }
}
