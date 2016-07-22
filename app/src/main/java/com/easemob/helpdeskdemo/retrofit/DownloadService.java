package com.easemob.helpdeskdemo.retrofit;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by liyuzhao on 16/7/21.
 */
public interface DownloadService {

    @Streaming
    @GET
    Call<ProgressResponseBody> download(@Url String url);
}
