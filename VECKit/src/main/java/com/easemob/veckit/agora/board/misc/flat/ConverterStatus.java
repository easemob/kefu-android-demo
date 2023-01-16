package com.easemob.veckit.agora.board.misc.flat;

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