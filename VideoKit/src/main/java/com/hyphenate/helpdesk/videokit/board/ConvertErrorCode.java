package com.hyphenate.helpdesk.videokit.board;

public enum ConvertErrorCode {
    CreatedFail(20001),
    ConvertFail(20002),
    NotFound(20003),
    CheckFail(2004),
    CheckTimeout(20005),
    GetDynamicFail(20006);

    private int code;

    ConvertErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}