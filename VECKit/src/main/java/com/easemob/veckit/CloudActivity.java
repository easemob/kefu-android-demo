package com.easemob.veckit;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.easemob.veckit.utils.CloudCallbackUtils;
import com.easemob.veckit.utils.GetRealFilePathFromUri;
import com.easemob.veckit.utils.Utils;
import com.hyphenate.util.UriUtils;
import com.hyphenate.util.VersionUtils;

import java.io.File;

public class CloudActivity extends AppCompatActivity implements View.OnClickListener {

    private View mPictureView;
    private View mVideoView;
    private View mVoiceView;
    private View mFileView;

    public final static int REQUEST_PICTURE = 1;
    public final static int REQUEST_VIDEO = 2;
    public final static int REQUEST_VOICE = 3;
    public final static int REQUEST_FILE = 4;
    public final static int UPLOAD_REQUEST = 200;

    // 是否为悬浮
    public final static String KEY_TYPE = "is_floating";
    private View mBack;

    private int mClickType = REQUEST_PICTURE;
    private final Handler mHandler = new Handler();
    private boolean mIsFloatingType;

    // selectedImage = content://com.android.providers.media.documents/document/document%3A14119
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vec_cloud);
        mBack = findViewById(R.id.back);
        mBack.setOnClickListener(this);
        mPictureView = findViewById(R.id.pictureView);
        mPictureView.setOnClickListener(this);
        mVideoView = findViewById(R.id.videoView);
        mVideoView.setOnClickListener(this);
        mVoiceView = findViewById(R.id.voiceView);
        mVoiceView.setOnClickListener(this);
        mFileView = findViewById(R.id.fileView);
        mFileView.setOnClickListener(this);

        Intent intent = getIntent();
        // 是否为悬浮
        mIsFloatingType = intent.getBooleanExtra(KEY_TYPE, false);
        if (mIsFloatingType){
            mHandler.postDelayed(() -> {
                if (!mIsClick){
                    CloudCallbackUtils.newCloudCallbackUtils().notifyShow();
                }
            }, 200);
        }

    }

    private boolean mIsClick;
    @Override
    public void onClick(View v) {
        mIsClick = true;
        int id = v.getId();
        if (id == R.id.pictureView){
            mClickType = REQUEST_PICTURE;
            // 上传图片
            selectPicFromLocal();
        }else if (id == R.id.videoView){
            mClickType = REQUEST_VIDEO;
            // 上传视频
            selectVideoFromLocal();
        }else if (id == R.id.voiceView){
            mClickType = REQUEST_VOICE;
            // 上传音频
            selectAudioFromLocal();
        }else if (id == R.id.fileView){
            mClickType = REQUEST_FILE;
            // 上传文档
            selectFileFromLocal();
        }else if (id == R.id.back){
            if (mIsFloatingType){
                mHandler.removeCallbacksAndMessages(null);
                mIsClick = false;
                CloudCallbackUtils.newCloudCallbackUtils().notifyUri(CloudActivity.UPLOAD_REQUEST, mClickType, mRealPathFromUri);
            }
            finish();
        }
    }

    private String mRealPathFromUri = "";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // content://media/external/images/media/1560226
        if (mIsFloatingType){
            floatingType(data);
        }else {
            notFloatingType(data);
        }
    }

    private void notFloatingType(@Nullable Intent data) {
        if (data != null){
            Uri selectedImage = data.getData();
            if (selectedImage != null){
                mRealPathFromUri = GetRealFilePathFromUri.getFileAbsolutePath(this, selectedImage);
                if (mRealPathFromUri != null){
                    /*File file = new File(mRealPathFromUri);*/
                    if (mClickType != getFileTypeByFilePath(mRealPathFromUri)){
                        Toast.makeText(this, String.format(Utils.getString(getApplicationContext(), R.string.vec_please_select_type),getFileTypeName(mClickType)), Toast.LENGTH_LONG).show();
                        return;
                    }
                }else {
                    mRealPathFromUri = UriUtils.getFilePath(this, selectedImage);
                }
            }
        }else {
            data = new Intent();
        }
        data.putExtra("path", mRealPathFromUri);
        setResult(mClickType, data);
        finish();
    }

    private void floatingType(@Nullable Intent data) {
        mRealPathFromUri = null;
        if (data != null){
            Uri selectedImage = data.getData();
            if (selectedImage != null){
                mRealPathFromUri = GetRealFilePathFromUri.getFileAbsolutePath(this, selectedImage);
                if (mRealPathFromUri != null){
                    // File file = new File(mRealPathFromUri);
                    if (mClickType != getFileTypeByFilePath(mRealPathFromUri)){
                        Toast.makeText(this, String.format(Utils.getString(getApplicationContext(), R.string.vec_please_select_type),getFileTypeName(mClickType)), Toast.LENGTH_LONG).show();
                        return;
                    }
                }else {
                    mRealPathFromUri = UriUtils.getFilePath(this, selectedImage);
                }
            }
            // data.putExtra("path", realPathFromUri);
        }
        CloudCallbackUtils.newCloudCallbackUtils().notifyUri(CloudActivity.UPLOAD_REQUEST, mClickType, mRealPathFromUri);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mIsFloatingType){
            mHandler.removeCallbacksAndMessages(null);
            mIsClick = false;
            CloudCallbackUtils.newCloudCallbackUtils().notifyUri(CloudActivity.UPLOAD_REQUEST, mClickType, mRealPathFromUri);
        }
    }

    /**
     * 从图库获取图片
     */
    protected void selectPicFromLocal() {
        Intent intent = null;
        if(VersionUtils.isTargetQ(this)) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }else {
            if (Build.VERSION.SDK_INT < 19) {
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
            } else {
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            }
        }
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        startActivityForResult(intent, REQUEST_PICTURE);
    }

    /**
     * 选择文件
     */
    protected void selectFileFromLocal() {
        Intent intent = new Intent();
        if(VersionUtils.isTargetQ(this)) {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        }else {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_FILE);
    }

    /**
     * 选择视频
     */
    private void selectVideoFromLocal(){

        Intent intent;
        if(VersionUtils.isTargetQ(this)) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }else {
            if (Build.VERSION.SDK_INT < 19) {
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
            } else {
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            }
        }
        intent.setType("video/*");

        startActivityForResult(intent, REQUEST_VIDEO);
    }

    /**
     * 选择音频
     */
    private void selectAudioFromLocal(){
        Intent intent;
        if(VersionUtils.isTargetQ(this)) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }else {
            if (Build.VERSION.SDK_INT < 19) {
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
            } else {
                // intent = new Intent(Intent.ACTION_PICK);
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            }
        }
        // MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        // MediaStore.Audio.Media.INTERNAL_CONTENT_URI
        intent.setType("audio/*");
        startActivityForResult(intent, REQUEST_VOICE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBack.setOnClickListener(null);
        mPictureView.setOnClickListener(null);
        mVideoView.setOnClickListener(null);
        mVoiceView.setOnClickListener(null);
        mFileView.setOnClickListener(null);
        if (mIsFloatingType){
            mHandler.removeCallbacksAndMessages(null);
            mIsClick = false;
        }
        mIsFloatingType = false;
    }

    public int getFileTypeByFilePath(String path) {
        File file = new File(path);
        String fileName = file.getName();
        String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
        switch (prefix) {
            case "png":
            case "webp":
            case "jpg":
                return REQUEST_PICTURE;
            case "mp4":
                return REQUEST_VIDEO;
            case "mp3":
                return REQUEST_VOICE;
            case "pptx":
            case "txt":
            case "xlsx":
            case "docx":
            case "doc":
            case "pdf":
            case "ppt":
                return REQUEST_FILE;
            default:
                return -1;
        }
    }

    private String getFileTypeName(int type){
        if (type == REQUEST_PICTURE){
            return "图片";
        }else if (type == REQUEST_VIDEO){
            return "视频";
        }else if (type == REQUEST_VOICE){
            return "音频";
        }else if (type == REQUEST_FILE){
            return "文件";
        }
        return "";
    }
}
