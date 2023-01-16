package com.easemob.helpdeskdemo.utils;


import com.hyphenate.cloud.HttpClientConfig;
import com.hyphenate.chat.ChatClient;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 *
 * demo为了演示上传附件的功能,传到了临时网站(附件单文件必须小于10M,只保留7天,超过7后删除)
 * 正常上传文件,应该上传到自己的服务器,返回URL,并发表评论
 *
 */
public class FileUploadManager {
    public static final String SERVER_URL = HttpClientConfig.getBaseUrlByAppKey() + "/";

    static Retrofit mRetrofit;

    public static Retrofit retrofit() {
        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(genericClient()) // Set the custom client when building adapter
                    .build();
        }
        return mRetrofit;
    }

    public static OkHttpClient genericClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("Authorization", "Bearer " + ChatClient.getInstance().accessToken())
                                .addHeader("restrict-access", "true")
                                .build();
                        return chain.proceed(request);
                    }
                }).build();
    }


    public interface FileUploadService {

        /**
         * 单文件上传
         * @param file
         * @return
         */
        @Multipart
        @POST("chatfiles")
        Call<ResponseBody> upload(@Part MultipartBody.Part file);
    }



}


