package com.easemob.helpdeskdemo.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.filedownload.FileApi;
import com.easemob.helpdeskdemo.filedownload.FileCallback;
import com.easemob.helpdeskdemo.utils.CommonUtils;
import com.easemob.util.PathUtil;

import java.io.File;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 */
public class FileDownloadActivity extends BaseActivity {

    private static final String TAG = FileDownloadActivity.class.getSimpleName();

    private ImageButton ibBack;
    private NumberProgressBar numberProgressBar;
    private String remoteUrl;
    private String localName;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_file_download);
        Intent intent = getIntent();
        remoteUrl = intent.getStringExtra("remoteUrl");
        localName = intent.getStringExtra("localName");
        ibBack = $(R.id.ib_back);
        numberProgressBar = $(R.id.number_progress_bar);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FileApi.getInstance("http://kefu.easemob.com/").loadFileByRemoteUrl(remoteUrl, new FileCallback(PathUtil.getInstance().getFilePath().getAbsolutePath(), localName) {

            @Override
            public void onSuccess(File file) {
                super.onSuccess(file);
                openFile(file);
            }

            @Override
            public void progress(long progress, long total) {
                if (total > 0) {
                    int percent = (int) (progress * 100 / total);
                    numberProgressBar.setProgress(percent);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();
                Log.e(TAG, "onFailure:" + t.getMessage());
                Toast.makeText(getApplicationContext(), "文件下载失败!", Toast.LENGTH_SHORT).show();

            }
        });

    }

    /**
     * 打开文件
     * @param file
     */
    private void openFile(File file){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        //获取文件file的MIME类型
        String type = CommonUtils.getMIMEType(file);
        //设置intent的data和Type属性。
        intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
        //跳转
        try{
            startActivity(intent); //这里最好try一下，有可能会报错。 //比如说你的MIME类型是打开邮箱，但是你手机里面没装邮箱客户端，就会报错。
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "文件无法打开", Toast.LENGTH_SHORT).show();
        }


    }


}
