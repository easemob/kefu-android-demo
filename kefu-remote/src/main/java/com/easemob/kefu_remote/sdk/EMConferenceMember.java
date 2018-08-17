package com.easemob.kefu_remote.sdk;

import com.superrtc.mediamanager.EMediaEntities;

/**
 * Created by lzan13 on 2018/5/14.
 */
public class EMConferenceMember {
    private String memberName;
    private String memberId;
    private String extension;

    public EMConferenceMember(EMediaEntities.EMediaMember member) {
        this.memberName = member.memberName;
        this.memberId = member.memberId;
        this.extension = member.extension;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getExtension() {
        return extension;
    }
}
