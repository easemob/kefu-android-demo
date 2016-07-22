package com.easemob.helpdeskdemo.retrofit;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by liyuzhao on 16/7/21.
 */
public class DownloadProgressInterceptor implements Interceptor {

    private ProgressListener listener;

    public DownloadProgressInterceptor(ProgressListener listener) {
        this.listener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        return originalResponse.newBuilder().body(new ProgressResponseBody(originalResponse.body(), listener)).build();
    }
}
