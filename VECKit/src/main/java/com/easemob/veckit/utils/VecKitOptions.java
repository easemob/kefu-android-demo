package com.easemob.veckit.utils;

public class VecKitOptions {
    private static VecKitOptions sVecKitOptions;
    private VecKitOptions(){}

    // 点击询前引导发起视频时的 configId
    private String mGuideConfigId;
    // 在线访客id
    private String mGuideVisitorUserId;
    // 询前引导 在线会话 IM服务号
    private String mRelatedImServiceNumber;
    // 询前引导 在线会话ID
    private String mGuideSessionId;

    public static VecKitOptions getVecKitOptions() {
        if (sVecKitOptions == null){
            synchronized (VecKitOptions.class){
                if (sVecKitOptions == null){
                    sVecKitOptions = new VecKitOptions();
                }
            }
        }
        return sVecKitOptions;
    }

    public void setGuideConfigId(String guideConfigId) {
        mGuideConfigId = guideConfigId;
    }

    public String getGuideConfigId() {
        return mGuideConfigId;
    }

    public void setGuideSessionId(String guideSessionId) {
        mGuideSessionId = guideSessionId;
    }

    public String getGuideSessionId() {
        return mGuideSessionId;
    }

    public void setGuideVisitorUserId(String guideVisitorUserId) {
        mGuideVisitorUserId = guideVisitorUserId;
    }

    public String getGuideVisitorUserId() {
        return mGuideVisitorUserId;
    }

    public void setRelatedImServiceNumber(String relatedImServiceNumber) {
        mRelatedImServiceNumber = relatedImServiceNumber;
    }

    public String getRelatedImServiceNumber() {
        return mRelatedImServiceNumber;
    }
}
