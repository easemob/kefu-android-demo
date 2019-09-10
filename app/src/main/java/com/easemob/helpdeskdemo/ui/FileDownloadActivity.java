package com.easemob.helpdeskdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.filedownload.FileApi;
import com.easemob.helpdeskdemo.filedownload.FileCallback;
import com.hyphenate.helpdesk.easeui.ui.BaseActivity;
import com.hyphenate.helpdesk.easeui.widget.ToastHelper;
import com.hyphenate.util.PathUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * 文件下载界面
 */
public class FileDownloadActivity extends BaseActivity {

    private static final String TAG = FileDownloadActivity.class.getSimpleName();

    private RelativeLayout rlBack;
    private NumberProgressBar numberProgressBar;
    private String remoteUrl;
    private String localName;
    private String fileType;


    private void initView(){
        rlBack = $(R.id.rl_back);
        numberProgressBar = $(R.id.number_progress_bar);
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_file_download);
        Intent intent = getIntent();
        remoteUrl = intent.getStringExtra("remoteUrl");
        localName = intent.getStringExtra("localName");
        fileType = intent.getStringExtra("type");
        initView();

        downloadFile();


    }

    /**
     * 下载文件
     */
    private void downloadFile(){
        if (TextUtils.isEmpty(remoteUrl)){
            finish();
            return;
        }
        FileApi.getInstance("http://a1.easemob.com/").loadFileByRemoteUrl(remoteUrl, new FileCallback() {
            @Override
            public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    File file = saveFile(response);
                    if (file != null) {
                        if (fileType != null && fileType.equals("audio")){
                            ToastHelper.show(getBaseContext(), R.string.audio_download_suc);
                            finish();
                        }else {
                            openFile(file);
                        }
                    } else {
                        showFailToast();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                super.onSuccess(call, response);
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
                showFailToast();
            }
        });
    }



    private void showFailToast() {
        ToastHelper.show(this, R.string.file_download_fail);
        File file = new File(PathUtil.getInstance().getFilePath(), localName);
        if (file.exists()) {
            file.delete();
        }
    }


    /**
     * 保存
     *
     * @param response
     * @return
     * @throws IOException
     */
    public File saveFile(Response<ResponseBody> response) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len;
        FileOutputStream fos = null;
        try {
            if (response.body() == null){
                return null;
            }
            is = response.body().byteStream();
            File file = new File(PathUtil.getInstance().getFilePath(), localName);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            return file;
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ignored) {
            }
            try {
                if (fos != null) fos.close();
            } catch (IOException ignored) {
            }
        }

    }


    /**
     * 打开文件
     *
     * @param file
     */
    private void openFile(File file) {
        if (file != null && file.exists()) {
            String suffix = "";
            try {
                String fileName = file.getName();
                suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            } catch (Exception e) {
            }
            try{
                com.hyphenate.helpdesk.easeui.util.CommonUtils.openFileEx(file, com.hyphenate.helpdesk.easeui.util.CommonUtils.getMap(suffix), this);
            }catch (Exception e){
                ToastHelper.show(this, "未安装能打开此文件的软件");
            }
        }
    }


}
