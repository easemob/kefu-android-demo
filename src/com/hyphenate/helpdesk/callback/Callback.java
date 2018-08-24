package com.hyphenate.helpdesk.callback;

/**
 * 通用的回调函数接口
 */
public interface Callback {

    /**
     * 程序执行成功时执行回调函数。
     */
    void onSuccess();

    /**
     * 发生错误时调用的回调函数  @see Error
     *
     * @param code  错误代码
     * @param error 包含文本类型的错误描述。
     */
    void onError(int code, String error);

    /**
     * 刷新进度的回调函数
     *
     * @param progress 进度信息
     * @param status   包含文件描述的进度信息, 如果SDK没有提供，结果可能是"", 或者null。
     */
    void onProgress(int progress, String status);
}

