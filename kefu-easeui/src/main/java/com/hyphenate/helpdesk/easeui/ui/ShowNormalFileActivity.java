package com.hyphenate.helpdesk.easeui.ui;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.easeui.util.CommonUtils;
import com.hyphenate.helpdesk.easeui.widget.ToastHelper;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.UriUtils;

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

        final Message message = ChatClient.getInstance().chatManager().getMessage(msgId);
        if (message == null){
            finish();
            return;
        }

        message.setMessageStatusCallback(new Callback() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String filePath = getFilePath(message);
                        if(!TextUtils.isEmpty(filePath)) {
                            CommonUtils.openFile(new File(filePath), ShowNormalFileActivity.this);
                        }
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

    private String getFilePath(Message message) {
        Uri localUrlUri = ((EMFileMessageBody) message.getBody()).getLocalUri();
        return UriUtils.getFilePath(this, localUrlUri);
    }
}
