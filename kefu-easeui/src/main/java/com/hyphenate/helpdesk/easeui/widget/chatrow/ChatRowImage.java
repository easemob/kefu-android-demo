package com.hyphenate.helpdesk.easeui.widget.chatrow;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.easeui.ImageCache;
import com.hyphenate.helpdesk.easeui.adapter.MessageAdapter;
import com.hyphenate.helpdesk.easeui.ui.ShowBigImageActivity;
import com.hyphenate.util.DensityUtil;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.ImageUtils;
import com.hyphenate.util.UriUtils;

import java.io.File;
import java.io.IOException;

public class ChatRowImage extends ChatRowFile{

    protected ImageView imageView;
    private EMImageMessageBody imgBody;
    private static final String TAG = ChatRowImage.class.getSimpleName();


    public ChatRowImage(Context context, Message message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct() == Message.Direct.RECEIVE ? R.layout.hd_row_received_picture : R.layout.hd_row_sent_picture, this);
    }

    @Override
    protected void onFindViewById() {
        percentageView = (TextView) findViewById(R.id.percentage);
        imageView = (ImageView) findViewById(R.id.image);
    }


    @Override
    protected void onSetUpView() {
        imgBody = (EMImageMessageBody) message.body();
        // 接收方向的消息
        if (message.direct() == Message.Direct.RECEIVE) {
            if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
                imageView.setImageResource(R.drawable.hd_default_image);
                setMessageReceiveCallback();
            } else {
                progressBar.setVisibility(View.GONE);
                percentageView.setVisibility(View.GONE);
                imageView.setImageResource(R.drawable.hd_default_image);

                Uri filePath = imgBody.getLocalUri();
                Uri thumbnailUrl = imgBody.thumbnailLocalUri();
                showImageView(thumbnailUrl, filePath, message);
            }
            return;
        }

        Uri filePath = imgBody.getLocalUri();
        Uri thumbnailUrl = imgBody.thumbnailLocalUri();
        showImageView(thumbnailUrl, filePath, message);

        handleSendMessage();
    }

    @Override
    protected void onUpdateView() {
        //super.onUpdateView();
        if (adapter instanceof MessageAdapter) {
            ((MessageAdapter) adapter).refreshSelectLast();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onBubbleClick() {
        Intent intent = new Intent(context, ShowBigImageActivity.class);
        Uri imgUri = imgBody.getLocalUri();
        if(UriUtils.isFileExistByUri(getContext(), imgUri)) {
            intent.putExtra("uri", imgUri);
        } else{
            // The local full size pic does not exist yet.
            // ShowBigImage needs to download it from the server
            // first
            String msgId = message.getMsgId();
            intent.putExtra("messageId", msgId);
            intent.putExtra("filename", imgBody.getFileName());
        }
        context.startActivity(intent);
    }

    /**
     * load image into image view
     *
     */
    @SuppressLint("StaticFieldLeak")
    private void showImageView(final Uri thumbernailPath, final Uri localFullSizePath, final Message message) {
        // first check if the thumbnail image already loaded into cache s
        Bitmap bitmap = ImageCache.getInstance().get(thumbernailPath.toString());

        if (bitmap != null) {
            // thumbnail image is already loaded, reuse the drawable
            imageView.setImageBitmap(bitmap);
        } else {
            final int width = DensityUtil.dip2px(getContext(), 70);
            new AsyncTask<Object, Void, Bitmap>() {

                @Override
                protected Bitmap doInBackground(Object... args) {
                    if (UriUtils.isFileExistByUri(context, thumbernailPath)) {
                        return getCacheBitmap(thumbernailPath);
                    } else if(UriUtils.isFileExistByUri(context, localFullSizePath)) {
                        return getCacheBitmap(localFullSizePath);
                    } else {
                        if (message.direct() == Message.Direct.SEND) {
                            if (UriUtils.isFileExistByUri(context, localFullSizePath)) {
                                String filePath = UriUtils.getFilePath(context, localFullSizePath);
                                if(!TextUtils.isEmpty(filePath)) {
                                    return ImageUtils.decodeScaleImage(filePath, width, width);
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    try {
                                        return ImageUtils.decodeScaleImage(context, localFullSizePath, width, width);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        return null;
                                    }
                                }
                            }
                            return null;
                        }
                        return null;
                    }
                }

                protected void onPostExecute(Bitmap image) {
                    if (image != null) {
                        EMLog.d("img", "bitmap width = "+image.getWidth() + " height = "+image.getHeight());
                        imageView.setImageBitmap(image);
                        ImageCache.getInstance().put(thumbernailPath.toString(), image);
                    }
                }

                private Bitmap getCacheBitmap(Uri fileUri) {
                    String filePath = UriUtils.getFilePath(context, fileUri);
                    EMLog.d(TAG, "fileUri = "+fileUri);
                    if(!TextUtils.isEmpty(filePath) && new File(filePath).exists()) {
                        return ImageUtils.decodeScaleImage(filePath, width, width);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        try {
                            return ImageUtils.decodeScaleImage(context, fileUri, width, width);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
                }
            }.execute();
        }
    }

}