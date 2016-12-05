package com.easemob.helpdeskdemo.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.filedownload.FileApi;
import com.easemob.helpdeskdemo.filedownload.FileCallback;
import com.easemob.helpdeskdemo.utils.CommonUtils;
import com.hyphenate.helpdesk.easeui.ui.BaseActivity;
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

    private ImageButton ibBack;
    private NumberProgressBar numberProgressBar;
    private String remoteUrl;
    private String localName;
    private String fileType;


    private void initView(){
        ibBack = $(R.id.ib_back);
        numberProgressBar = $(R.id.number_progress_bar);
        ibBack.setOnClickListener(new View.OnClickListener() {
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
                            Toast.makeText(getApplicationContext(), "语音下载成功", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getApplicationContext(), "文件下载失败!", Toast.LENGTH_SHORT).show();
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
            } catch (IOException e) {
            }
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
            }
        }

    }


    /**
     * 打开文件
     *
     * @param file
     */
    private void openFile(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        //获取文件file的MIME类型
        String type = CommonUtils.getMIMEType(file);
        //设置intent的data和Type属性。
        intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
        //跳转
        try {
            startActivity(intent); //这里最好try一下，有可能会报错。 //比如说你的MIME类型是打开邮箱，但是你手机里面没装邮箱客户端，就会报错。
            finish();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "文件无法打开", Toast.LENGTH_SHORT).show();
        }
    }


}
