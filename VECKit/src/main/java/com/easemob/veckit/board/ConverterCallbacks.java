package com.easemob.veckit.board;

public interface ConverterCallbacks {
    void onProgress(Double progress, ConversionInfo convertInfo);

    void onFinish(ConvertedFiles ppt, ConversionInfo convertInfo, FileConverter.QueryInfo queryInfo);

    void onFailure(ConvertException e);

    void onFailure(int code, String error);
}