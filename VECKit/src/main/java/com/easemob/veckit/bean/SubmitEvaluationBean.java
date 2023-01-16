package com.easemob.veckit.bean;

import java.util.List;

public class SubmitEvaluationBean {
    public String rtcSessionId;
    public String visitorUserId;
    public int score;
    public String comment;
    public List<DegreeBean> tagData;

    public SubmitEvaluationBean(String rtcSessionId, String visitorUserId, int score, String comment, List<DegreeBean> tagData) {
        this.rtcSessionId = rtcSessionId;
        this.visitorUserId = visitorUserId;
        this.score = score;
        this.comment = comment;
        this.tagData = tagData;
    }
}
