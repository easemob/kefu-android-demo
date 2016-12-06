package com.hyphenate.helpdesk.easeui.ui;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.util.FileUtils;

import java.io.File;

public class ShowNormalFileActivity extends BaseActivity {
    private ProgressBar progressBar;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ease_activity_show_file);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        final String  msgId = getIntent().getStringExtra("messageId");

        Message message = ChatClient.getInstance().getChat().getMessage(msgId);
        if (message == null){
            finish();
            return;
        }
        EMFileMessageBody messageBody = (EMFileMessageBody) message.getBody();
        file = new File(messageBody.getLocalUrl());
        message.setMessageStatusCallback(new Callback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            FileUtils.openFile(file, ShowNormalFileActivity.this);
                            finish();
                        }
                    });
                }

                @Override
                public void onError(final int error, final String errorMsg) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (file != null && file.exists() && file.isFile())
                                file.delete();
                            String str4 = getResources().getString(R.string.Failed_to_download_file);
                            Toast.makeText(ShowNormalFileActivity.this, str4 + errorMsg, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }

                @Override
                public void onProgress(final int progress, String s) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progress);
                        }
                    });
                }
            });
            ChatClient.getInstance().getChat().downloadAttachment(message);

//        //set head map
//        final Map<String, String> maps = new HashMap<String, String>();
//        if (!TextUtils.isEmpty(messageBody.getSecret())) {
//            maps.put("share-secret", messageBody.getSecret());
//        }
//
//        //下载文件
//        ChatClient.getInstance().getChat().downloadFile(messageBody.getRemoteUrl(), messageBody.getLocalUrl(), maps, new Callback() {
//            @Override
//            public void onSuccess() {
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        FileUtils.openFile(file, ShowNormalFileActivity.this);
//                        finish();
//                    }
//                });
//            }
//
//            @Override
//            public void onError(int code, final String error) {
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        if (file != null && file.exists() && file.isFile())
//                            file.delete();
//                        String str4 = getResources().getString(R.string.Failed_to_download_file);
//                        Toast.makeText(ShowNormalFileActivity.this, str4 + error, Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//                });
//            }
//
//            @Override
//            public void onProgress(final int progress, String status) {
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        progressBar.setProgress(progress);
//                    }
//                });
//            }
//        });


    }
}
