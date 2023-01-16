package com.easemob.veckit.bean;

public class OptionBean {
    /*"createDateTime":"2022-07-05 02:43:05",
    "lastUpdateDateTime":"2022-07-05 02:43:05",
    "optionId":89,
    "optionName":"EnquiryCommentEnable",
    "optionValue":"true",
    "tenantId":77561*/

    public String createDateTime;
    public String lastUpdateDateTime;
    public int optionId;
    public String optionName; // EnquiryCommentEnable，EnquiryCommentFor1Score，-- EnquiryDefaultShow5Score，EnquiryInviteMsg，EnquirySolveMsg
    public String optionValue;
    public String tenantId;

    @Override
    public String toString() {
        return "{" +
                "createDateTime: '" + createDateTime + '\'' +
                ", lastUpdateDateTime: '" + lastUpdateDateTime + '\'' +
                ", optionId: " + optionId +
                ", optionName: '" + optionName + '\'' +
                ", optionValue: '" + optionValue + '\'' +
                ", tenantId: '" + tenantId + '\'' +
                '}';
    }
}
