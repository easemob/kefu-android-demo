package com.hyphenate.helpdesk.easeui.ui;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.easeui.util.CommonUtils;
import com.hyphenate.helpdesk.easeui.widget.ToastHelper;

import java.io.File;

public class ShowNormalFileActivity extends BaseActivity {
    private ProgressBar progressBar;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hd_activity_show_file);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        final String msgId = getIntent().getStringExtra("messageId");

        Message message = ChatClient.getInstance().chatManager().getMessage(msgId);
        if (message == null){
            finish();
            return;
        }

        EMFileMessageBody messageBody = (EMFileMessageBody) message.body();
        file = new File(messageBody.getLocalUrl());
        message.setMessageStatusCallback(new Callback() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        FileUtils.openFile(file, ShowNormalFileActivity.this);
                        openFile(file);
                        finish();
                    }
                });
            }

            @Override
            public void onError(int error, final String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (file != null && file.exists() && file.isFile()){
                            file.delete();
                        }
                        String str4 = getResources().getString(R.string.Failed_to_download_file);
                        ToastHelper.show(getBaseContext(), str4 + errorMsg);
                        finish();
                    }
                });

            }

            @Override
            public void onProgress(final int progress, String s) {
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


    private void openFile(File file) {
        if (file != null && file.exists()) {
            String suffix = "";
            try {
                String fileName = file.getName();
                suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            } catch (Exception e) {
            }
            try{
                CommonUtils.openFileEx(file, CommonUtils.getMap(suffix), ShowNormalFileActivity.this);
            }catch (Exception e){
                ToastHelper.show(getBaseContext(), "未安装能打开此文件的软件");
            }
        }
    }
}
