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

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.easeui.ImageCache;
import com.hyphenate.helpdesk.easeui.photoview.PhotoView;
import com.hyphenate.helpdesk.easeui.util.LoadLocalBigImgTask;
import com.hyphenate.helpdesk.util.Log;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.ImageUtils;

import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 * 下载显示大图
 */
public class ShowBigImageActivity extends BaseActivity {
    private static final String TAG = "ShowBigImage";
    private ProgressDialog pd;
    private PhotoView image;
    private int default_res = R.drawable.ease_default_image;
    private Bitmap bitmap;
    private boolean isDownloaded;
    private ProgressBar loadLocalPb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.ease_activity_show_big_image);
        super.onCreate(savedInstanceState);

        image = (PhotoView) findViewById(R.id.image);
        loadLocalPb = (ProgressBar) findViewById(R.id.pb_load_local);
        default_res = getIntent().getIntExtra("default_image", R.drawable.ease_default_image);
        Uri uri = getIntent().getParcelableExtra("uri");
        String msgId = getIntent().getExtras().getString("messageId");
        EMLog.d(TAG, "show big msgId:" + msgId);

        // show the image if it exist in local path
        if (uri != null && new File(uri.getPath()).exists()) {
            Log.d(TAG, "showbigimage file exists. directly show it");
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            // int screenWidth = metrics.widthPixels;
            // int screenHeight =metrics.heightPixels;
            bitmap = ImageCache.getInstance().get(uri.getPath());
            if (bitmap == null) {
                LoadLocalBigImgTask task = new LoadLocalBigImgTask(this, uri.getPath(), image, loadLocalPb, ImageUtils.SCALE_IMAGE_WIDTH,
                        ImageUtils.SCALE_IMAGE_HEIGHT);
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    task.execute();
                }
            } else {
                image.setImageBitmap(bitmap);
            }
        } else if (msgId != null) { //去服务器下载图片
            downloadImage(msgId);
        } else {
            image.setImageResource(default_res);
        }

        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void downloadImage(final String msgId){
        EMLog.e(TAG, "download with messageId:" + msgId);
        String str1 = getResources().getString(R.string.Download_the_pictures);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(str1);
        pd.show();
        Message msg = ChatClient.getInstance().getChat().getMessage(msgId);
        EMImageMessageBody imgBody = (EMImageMessageBody) msg.getBody();
        final String localPath = imgBody.getLocalUrl();
        final File localFile = new File(localPath);
        final Callback callback = new Callback() {
            @Override
            public void onSuccess() {
                EMLog.d(TAG, "onSuccess");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (localFile != null && localFile.length() < 614400){
                            bitmap = BitmapFactory.decodeFile(localPath);
                        }else{
                            DisplayMetrics metrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(metrics);
                            int screenWidth = metrics.widthPixels;
                            int screenHeight = metrics.heightPixels;
                            bitmap = ImageUtils.decodeScaleImage(localPath, screenWidth, screenHeight);
                        }
                        if (bitmap == null) {
                            image.setImageResource(default_res);
                        } else {
                            image.setImageBitmap(bitmap);
                            ImageCache.getInstance().put(localPath, bitmap);
                            isDownloaded = true;
                        }
                        if (isFinishing()){
                            return;
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            if (isDestroyed()){
                                return;
                            }
                        }

                        if (pd != null) {
                            pd.dismiss();
                        }


                    }
                });


            }

            @Override
            public void onError(int i, String error) {
                EMLog.d(TAG, "offline file transfer error:" + error);
                if (localFile.exists() && localFile.isFile()) {
                    localFile.delete();
                }
                if (isFinishing()) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        image.setImageResource(default_res);
                        pd.dismiss();
                    }
                });
            }

            @Override
            public void onProgress(final int progress, String status) {
                Log.d(TAG, "Progress: " + progress);
                if (isFinishing()) {
                    return;
                }
                final String str2 = getResources().getString(R.string.Download_the_pictures_new);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.setMessage(str2 + progress + "%");
                    }
                });
            }
        };
        msg.setMessageStatusCallback(callback);
        ChatClient.getInstance().getChat().downloadAttachment(msg);
    }

    @Override
    public void onBackPressed() {
        if (isDownloaded)
            setResult(RESULT_OK);
        finish();
    }
}
