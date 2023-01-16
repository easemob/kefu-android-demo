package com.easemob.veckit.bean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class EnquiryOptionsBean {
    public String rtcSessionId;
    public String tenantId;
    public String visitorUserId;
    public ArrayList<OptionBean> enquiryOptions;


    public static EnquiryOptionsBean get(String json){
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.fromJson(json, EnquiryOptionsBean.class);
    }

    @Override
    public String toString() {
        return "{" +
                "rtcSessionId: '" + rtcSessionId + '\'' +
                ", tenantId: '" + tenantId + '\'' +
                ", visitorUserId: '" + visitorUserId + '\'' +
                ", enquiryOptions: " + enquiryOptions +
                '}';
    }
}
