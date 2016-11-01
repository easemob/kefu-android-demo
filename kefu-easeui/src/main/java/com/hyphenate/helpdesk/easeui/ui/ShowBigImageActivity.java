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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.easeui.ImageCache;
import com.hyphenate.helpdesk.easeui.photoview.PhotoView;
import com.hyphenate.helpdesk.easeui.util.LoadLocalBigImgTask;
import com.hyphenate.helpdesk.util.Log;
import com.hyphenate.util.ImageUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 下载显示大图
 */
public class ShowBigImageActivity extends BaseActivity {
    private static final String TAG = "ShowBigImage";
    private ProgressDialog pd;
    private PhotoView image;
    private int default_res = R.drawable.ease_default_image;
    private String localFilePath;
    private Bitmap bitmap;
    private boolean isDownloaded;
    private ProgressBar loadLocalPb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.ease_activity_show_big_image);
        super.onCreate(savedInstanceState);

        image = (PhotoView) findViewById(R.id.image);
        loadLocalPb = (ProgressBar) findViewById(R.id.pb_load_local);
        default_res = getIntent().getIntExtra("default_image", R.drawable.ease_default_avatar);
        Uri uri = getIntent().getParcelableExtra("uri");
        String remotepath = getIntent().getExtras().getString("remotepath");
        localFilePath = getIntent().getExtras().getString("localUrl");
        String secret = getIntent().getExtras().getString("secret");
        Log.d(TAG, "show big image uri:" + uri + " remotepath:" + remotepath);

        //本地存在，直接显示本地的图片
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
        } else if (remotepath != null) { //去服务器下载图片
            Log.d(TAG, "download remote image");
            Map<String, String> maps = new HashMap<String, String>();
            if (!TextUtils.isEmpty(secret)) {
                maps.put("share-secret", secret);
            }
            downloadImage(remotepath, maps);
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

    /**
     * 下载图片
     *
     * @param remoteFilePath
     */
    private void downloadImage(final String remoteFilePath, final Map<String, String> headers) {
        String str1 = getResources().getString(R.string.Download_the_pictures);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(str1);
        pd.show();
        final Callback callback = new Callback() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @SuppressLint("NewApi")
                    @Override
                    public void run() {
                        DisplayMetrics metrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(metrics);
                        int screenWidth = metrics.widthPixels;
                        int screenHeight = metrics.heightPixels;

                        bitmap = ImageUtils.decodeScaleImage(localFilePath, screenWidth, screenHeight);
                        if (bitmap == null) {
                            image.setImageResource(default_res);
                        } else {
                            image.setImageBitmap(bitmap);
                            ImageCache.getInstance().put(localFilePath, bitmap);
                            isDownloaded = true;
                        }
                        if (pd != null) {
                            pd.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onError(int code, String error) {
                Log.e(TAG, "offline file transfer error:" + error);
                File file = new File(localFilePath);
                if (file.exists() && file.isFile()) {
                    file.delete();
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
        ChatClient.getInstance().getChat().downloadFile(remoteFilePath, localFilePath, headers, callback);
    }

    @Override
    public void onBackPressed() {
        if (isDownloaded)
            setResult(RESULT_OK);
        finish();
    }
}
