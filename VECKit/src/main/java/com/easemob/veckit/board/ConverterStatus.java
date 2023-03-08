package com.easemob.veckit.board;

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