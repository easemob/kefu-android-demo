package com.hyphenate.helpdesk.easeui.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.util.Log;
import com.hyphenate.util.PathUtil;

import java.io.File;

/**
 * 展示视频内容
 *
 */
public class ShowVideoActivity extends BaseActivity {
    private static final String TAG = "ShowVideoActivity";

    private RelativeLayout loadingLayout;
    private ProgressBar progressBar;
    private String localFilePath;

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
        localFilePath = messageBody.getLocalUrl();
        if (localFilePath != null && new File(localFilePath).exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(localFilePath)),
                    "video/mp4");
            startActivity(intent);
            finish();
        } else {
            downloadVideo(message);
        }
    }

    public String getLocalFilePath(String remoteUrl) {
        String localPath;
        if (remoteUrl.contains("/")) {
            localPath = PathUtil.getInstance().getVideoPath().getAbsolutePath()
                    + "/" + remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1)
                    + ".mp4";
        } else {
            localPath = PathUtil.getInstance().getVideoPath().getAbsolutePath()
                    + "/" + remoteUrl + ".mp4";
        }
        return localPath;
    }

    /**
     * 播放本地视频
     *
     * @param localPath 视频路径
     */
    private void showLocalVideo(String localPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(localPath)),
                "video/mp4");
        startActivity(intent);
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
                        showLocalVideo(localFilePath);
                    }
                });
            }

            @Override
            public void onError(int code, String error) {
                Log.e(TAG, "offline file transfer error:" + error);
                File file = new File(localFilePath);
                if (file.exists()) {
                    file.delete();
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
