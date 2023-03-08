package com.hyphenate.helpdesk.videokit.board;

public enum ConverterStatus {
    Idle,
    Created,
    CreateFail,
    Checking,
    WaitingForNextCheck,
    Timeout,
    CheckingFail,
    GetDynamicFail,
    Success,
    Fail,
}