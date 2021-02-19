/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.helpdesk.easeui.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.easeui.ImageCache;
import com.hyphenate.helpdesk.easeui.photoview.PhotoView;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.ImageUtils;
import com.hyphenate.util.UriUtils;

import java.io.IOException;

/**
 * 下载显示大图
 */
public class ShowBigImageActivity extends BaseActivity {
    private static final String TAG = "ShowBigImage";
    private ProgressDialog pd;
    private PhotoView image;
    private int default_res = R.drawable.hd_default_image;
    private String filename;
    private Bitmap bitmap;
    private boolean isDownloaded;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.hd_activity_show_big_image);
        super.onCreate(savedInstanceState);

        image = (PhotoView) findViewById(R.id.image);
        ProgressBar loadLocalPb = (ProgressBar) findViewById(R.id.pb_load_local);
        default_res = getIntent().getIntExtra("default_image", R.drawable.hd_default_avatar);
        Uri uri = getIntent().getParcelableExtra("uri");
        filename = getIntent().getExtras().getString("filename");
        String msgId = getIntent().getExtras().getString("messageId");
        EMLog.d(TAG, "show big msgId:" + msgId );

        //show the image if it exist in local path
        if (UriUtils.isFileExistByUri(this, uri)) {
            Glide.with(this).load(uri).into(image);
        } else if(msgId != null) {
            downloadImage(msgId);
        }else {
            image.setImageResource(default_res);
        }

        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * download image
     *
     * @param msgId
     */
    @SuppressLint("NewApi")
    private void downloadImage(final String msgId) {
        EMLog.e(TAG, "download with messageId: " + msgId);
        String str1 = getResources().getString(R.string.Download_the_pictures);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(str1);
        pd.show();

        final Message msg = ChatClient.getInstance().chatManager().getMessage(msgId);
        final Callback callback = new Callback() {
            public void onSuccess() {
                EMLog.e(TAG, "onSuccess" );
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing() && !isDestroyed()) {
                            if (pd != null) {
                                pd.dismiss();
                            }
                            isDownloaded = true;
                            Uri localUrlUri = ((EMImageMessageBody) msg.getBody()).getLocalUri();
                            Glide.with(ShowBigImageActivity.this)
                                    .load(localUrlUri)
                                    .apply(new RequestOptions().error(default_res))
                                    .into(image);
                        }
                    }
                });
            }

            public void onError(final int error, String message) {
                EMLog.e(TAG, "offline file transfer error:" + message);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (ShowBigImageActivity.this.isFinishing() || ShowBigImageActivity.this.isDestroyed()) {
                            return;
                        }
                        image.setImageResource(default_res);
                        pd.dismiss();
                    }
                });
            }

            public void onProgress(final int progress, String status) {
                EMLog.d(TAG, "Progress: " + progress);
                final String str2 = getResources().getString(R.string.Download_the_pictures_new);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (ShowBigImageActivity.this.isFinishing() || ShowBigImageActivity.this.isDestroyed()) {
                            return;
                        }
                        pd.setMessage(str2 + progress + "%");
                    }
                });
            }
        };


        msg.setMessageStatusCallback(callback);
        ChatClient.getInstance().chatManager().downloadAttachment(msg);
    }

    @Override
    public void onBackPressed() {
        if (isDownloaded)
            setResult(RESULT_OK);
        finish();
    }
}
