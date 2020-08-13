package com.hyphenate.helpdesk.easeui.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hyphenate.EMError;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.easeui.util.CommonUtils;
import com.hyphenate.helpdesk.easeui.widget.ToastHelper;
import com.hyphenate.helpdesk.util.Log;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.PathUtil;
import com.hyphenate.util.UriUtils;

import java.io.File;

/**
 * 展示视频内容
 *
 */
public class ShowVideoActivity extends BaseActivity {
    private static final String TAG = "ShowVideoActivity";

    private RelativeLayout loadingLayout;
    private ProgressBar progressBar;
    private Uri localFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.hd_showvideo_activity);
        loadingLayout = (RelativeLayout) findViewById(R.id.loading_layout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        final Message message = getIntent().getParcelableExtra("msg");

        EMVideoMessageBody messageBody = (EMVideoMessageBody)message.body();

        localFilePath = messageBody.getLocalUri();
        EMLog.d(TAG, "localFilePath = "+localFilePath);
        EMLog.d(TAG, "local filename = "+messageBody.getFileName());

        if(UriUtils.isFileExistByUri(this, localFilePath)) {
            LocalVideoPlayerActivity.actionStart(this, localFilePath.toString());
            finish();
        } else {
            EMLog.d(TAG, "download remote video file");
            downloadVideo(message);
        }
    }


    private void showLocalVideo(final Message message) {
        EMVideoMessageBody messageBody = (EMVideoMessageBody)message.body();

        localFilePath = messageBody.getLocalUri();
        EMLog.d(TAG, "localFilePath = "+localFilePath);
        EMLog.d(TAG, "local filename = "+messageBody.getFileName());

        if(UriUtils.isFileExistByUri(this, localFilePath)) {
            LocalVideoPlayerActivity.actionStart(this, localFilePath.toString());
        } else {
            EMLog.e(TAG, "video file does not exist");
        }

        finish();
    }

    /**
     * 下载视频文件
     */
    private void downloadVideo(final Message message) {
        loadingLayout.setVisibility(View.VISIBLE);
	    message.setMessageStatusCallback(new Callback() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        loadingLayout.setVisibility(View.GONE);
                        progressBar.setProgress(0);

                        showLocalVideo(message);
                    }
                });
            }

            @Override
            public void onError(int code, String error) {
                Log.e(TAG, "offline file transfer error:" + error);
                Uri localFilePath = ((EMVideoMessageBody) message.getBody()).getLocalUri();
                String filePath = UriUtils.getFilePath(ShowVideoActivity.this, localFilePath);
                if(TextUtils.isEmpty(filePath)) {
                    getContentResolver().delete(localFilePath, null, null);
                }else {
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }

            @Override
            public void onProgress(final int progress, String status) {
                Log.d(TAG, "video progress:" + progress);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(progress);
                    }
                });
            }
        });
        ChatClient.getInstance().chatManager().downloadAttachment(message);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
