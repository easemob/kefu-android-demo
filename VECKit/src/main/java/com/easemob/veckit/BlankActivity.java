package com.easemob.veckit;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.easemob.veckit.utils.CloudCallbackUtils;
import com.easemob.veckit.utils.VecChatViewUtils;
import com.hyphenate.util.UriUtils;
import com.hyphenate.util.VersionUtils;

public class BlankActivity extends AppCompatActivity {
    // ChatFragment
    private Handler mHandler;
    public final static int IMAGE_REQUEST = 201;
    public final static int BIG_IMAGE_REQUEST = 202;
    private int mType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        Intent intent = getIntent();
        mType = intent.getIntExtra(VecChatViewUtils.TYPE_BLANK_KEY, IMAGE_REQUEST);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CloudCallbackUtils.newCloudCallbackUtils().notifyShow();
            }
        }, 600);



        if (mType == IMAGE_REQUEST){
            // 选择本地图片库
            selectPicFromLocal();
        }else if (mType == BIG_IMAGE_REQUEST){
            // 查看大图
            Uri imgUri = intent.getParcelableExtra(VecChatViewUtils.TYPE_BLANK_PARCELABLE_KEY);
            String msgId = intent.getStringExtra(VecChatViewUtils.TYPE_BLANK_MSG_ID_key);
            String fileName = intent.getStringExtra(VecChatViewUtils.TYPE_BLANK_FILE_NAME_KEY);
            showBigImage(imgUri, msgId, fileName);
        }

    }

    private void showBigImage(Uri imgUri, String msgId, String fileName){
        // ShowBigImageActivity.class
        Intent intent = new Intent(this, ShowBigImageActivity.class);
        if (UriUtils.isFileExistByUri(this, imgUri)) {
            intent.putExtra("uri", imgUri);
        } else {
            // The local full size pic does not exist yet.
            // ShowBigImage needs to download it from the server
            // first
            intent.putExtra("messageId", msgId);
            intent.putExtra("filename", fileName);
        }
        /*if (UriUtils.isFileExistByUri(this, imgUri)) {
            intent.putExtra("uri", imgUri);
        } else {
            // The local full size pic does not exist yet.
            // ShowBigImage needs to download it from the server
            // first
            intent.putExtra("messageId", msgId);
            intent.putExtra("filename", fileName);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
        startActivityForResult(intent, BIG_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == IMAGE_REQUEST) { // 发送本地图片
        }else if (requestCode == BIG_IMAGE_REQUEST){
        }*/
        CloudCallbackUtils.newCloudCallbackUtils().notifyUri(mType, 0, data);
        clear();
        finish();
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
        intent.setType("image/*");
        // intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        startActivityForResult(intent, IMAGE_REQUEST);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CloudCallbackUtils.newCloudCallbackUtils().notifyUri(mType, 0, "");
        clear();
    }

    private void clear(){
        if (mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }
}
