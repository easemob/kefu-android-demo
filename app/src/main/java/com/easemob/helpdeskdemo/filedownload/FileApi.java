package com.easemob.helpdeskdemo.filedownload;

import java.io.IOException;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Retrofit文件下载API
 */
public class FileApi {

    private static final int DEFAULT_TIMEOUT = 5;
    private Retrofit retrofit;
    private FileService fileService;
    private volatile static FileApi instance;
    private static Call<ResponseBody> call;

    private static Hashtable<String, FileApi> mFileApiTable;

    static {
        mFileApiTable = new Hashtable<>();
    }

    private FileApi(String baseUrl){
        retrofit = new Retrofit.Builder().client(initOkHttpClient())
                .baseUrl(baseUrl)
                .build();
        fileService = retrofit.create(FileService.class);
    }

    /**
     * 获取实例
     *
     * @param baseUrl
     * @return
     */
    public static FileApi getInstance(String baseUrl) {
        instance = mFileApiTable.get(baseUrl);
        if (instance == null) {
            synchronized (FileApi.class) {
                if (instance == null) {
                    instance = new FileApi(baseUrl);
                    mFileApiTable.put(baseUrl, instance);
                }
            }
        }
        return instance;
    }

    /**
     * 下载文件
     *
     * @param remoteUrl
     * @param callback
     */
    public void loadFileByRemoteUrl(String remoteUrl, FileCallback callback) {
        call = fileService.loadFile(remoteUrl);
        call.enqueue(callback);
    }

    /**
     * 取消下载
     */
    public static void cancelLoading() {
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
    }

    /**
     * 初始化OkHttpClient
     *
     * @return
     */
    private OkHttpClient initOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse
                        .newBuilder()
                        .body(new FileResponseBody(originalResponse.body()))
                        .build();
            }
        });
        return builder.build();
    }






}
