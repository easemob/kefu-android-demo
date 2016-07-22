package com.easemob.helpdeskdemo.filedownload;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by liyuzhao on 16/7/21.
 */
public abstract class FileCallback implements Callback<ResponseBody> {

    /**
     * 订阅下载进度
     */
    private CompositeSubscription rxSubscriptions = new CompositeSubscription();


    public FileCallback() {
        subscribeLoadProgress();
    }

    public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
        unsubscribe();
    }

    public abstract void progress(long progress, long total);

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        onSuccess(call, response);
    }

    /**
     * 订阅文件下载进度
     */
    private void subscribeLoadProgress() {
        rxSubscriptions.add(RxBus.getInstance()
                .toObservable(FileLoadEvent.class)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<FileLoadEvent>() {
                    @Override
                    public void call(FileLoadEvent fileLoadEvent) {
                        progress(fileLoadEvent.getProgress(), fileLoadEvent.getTotal());
                    }
                }));
    }


    /**
     * 取消订阅，防止内存泄漏
     */
    private void unsubscribe() {
        if (!rxSubscriptions.isUnsubscribed()) {
            rxSubscriptions.unsubscribe();
        }
    }


}
