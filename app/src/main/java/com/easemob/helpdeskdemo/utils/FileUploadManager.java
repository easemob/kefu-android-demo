package com.easemob.helpdeskdemo.utils;

import com.easemob.chat.EMChatManager;
import com.easemob.cloud.HttpClientConfig;

import java.io.IOException;
import java.util.List;

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
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("Authorization", "Bearer " + EMChatManager.getInstance().getAccessToken())
                                .addHeader("restrict-access", "true")
                                .build();
                        return chain.proceed(request);
                    }
                }).build();
        return okHttpClient;
    }


    public interface FileUploadService {

        /**
         *
         * 通过List<MultipartBody.Part>传入多个part实现多文件上传
         *
         * @param parts 每个part代表一个
         * @return 状态信息
         */
        @Multipart
        @POST("chatfiles")
        Call<List<ResponseBody>> uploadFilesWithParts(
                @Part()List<MultipartBody.Part> parts
        );


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


