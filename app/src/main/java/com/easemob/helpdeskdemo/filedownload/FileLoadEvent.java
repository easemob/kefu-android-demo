package com.easemob.helpdeskdemo.filedownload;

/**
 * Created by liyuzhao on 16/7/21.
 */
public class FileLoadEvent {

    /**
     * 文件大小
     */
    long total;

    /**
     * 已下载大小
     */
    long progress;

    public long getProgress() {
        return progress;
    }

    public long getTotal() {
        return total;
    }

    public FileLoadEvent(long total, long progress) {
        this.total = total;
        this.progress = progress;
    }
}
