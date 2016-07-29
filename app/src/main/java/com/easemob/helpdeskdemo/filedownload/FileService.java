package com.easemob.helpdeskdemo.filedownload;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Retrofit文件下载接口
 */
public interface FileService {
    /**
     * 下载数据库\资源
     * @return
     */
    @GET
    Call<ResponseBody> loadFile(@Url String remoteUrl);
}
