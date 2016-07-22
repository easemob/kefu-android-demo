package com.easemob.helpdeskdemo.retrofit;

/**
 * Created by liyuzhao on 16/7/21.
 */
public interface ProgressListener {
    /**
     *
     * @param progress 已经下载或者上传字节数
     * @param total  总字节数
     * @param done  是否完成
     */
    void onProgress(long progress, long total, boolean done);
}
