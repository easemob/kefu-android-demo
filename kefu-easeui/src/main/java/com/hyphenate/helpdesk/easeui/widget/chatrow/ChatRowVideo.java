package com.hyphenate.helpdesk.easeui.widget.chatrow;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.R;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.easeui.ImageCache;
import com.hyphenate.helpdesk.easeui.ui.ShowVideoActivity;
import com.hyphenate.helpdesk.easeui.util.CommonUtils;
import com.hyphenate.helpdesk.util.Log;
import com.hyphenate.util.DateUtils;
import com.hyphenate.util.ImageUtils;
import com.hyphenate.util.TextFormater;

import java.io.File;

public class ChatRowVideo extends ChatRowFile {

    private ImageView imageView;
    private TextView sizeView;
    private TextView timeLengthView;
    private ImageView playView;

    public ChatRowVideo(Context context, Message message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct() == Message.Direct.RECEIVE ?
                R.layout.hd_row_received_video : R.layout.hd_row_sent_video, this);
    }

    @Override
    protected void onFindViewById() {
        imageView = ((ImageView) findViewById(R.id.chatting_content_iv));
        sizeView = (TextView) findViewById(R.id.chatting_size_iv);
        timeLengthView = (TextView) findViewById(R.id.chatting_length_iv);
        playView = (ImageView) findViewById(R.id.chatting_status_btn);
        percentageView = (TextView) findViewById(R.id.percentage);
    }

    @Override
    protected void onSetUpView() {
        EMVideoMessageBody videoBody = (EMVideoMessageBody) message.body();
        // final File image=new File(PathUtil.getInstance().getVideoPath(),
        // videoBody.getFileName());
        String localThumb = videoBody.getLocalThumb();

        if (localThumb != null) {

            showVideoThumbView(localThumb, imageView, videoBody.getThumbnailUrl(), message);
        }
        if (videoBody.getDuration() > 0) {
            String time = DateUtils.toTime(videoBody.getDuration());
            timeLengthView.setText(time);
        }
//        playView.setImageResource(R.drawable.video_play_btn_small_nor);

        if (message.direct() == Message.Direct.RECEIVE) {
            if (videoBody.getVideoFileLength() > 0) {
                String size = TextFormater.getDataSize(videoBody.getVideoFileLength());
                sizeView.setText(size);
            }
        } else {
            if (videoBody.getLocalUrl() != null && new File(videoBody.getLocalUrl()).exists()) {
                String size = TextFormater.getDataSize(new File(videoBody.getLocalUrl()).length());
                sizeView.setText(size);
            }
        }

        Log.d(TAG, "video thumbnailStatus:" + videoBody.thumbnailDownloadStatus());
        if (message.direct() == Message.Direct.RECEIVE) {
            if (videoBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    videoBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
                imageView.setImageResource(R.drawable.hd_default_image);
                setMessageReceiveCallback();
            } else {
                // System.err.println("!!!! not back receive, show image directly");
                imageView.setImageResource(R.drawable.hd_default_image);
                if (localThumb != null) {
                    showVideoThumbView(localThumb, imageView, videoBody.getThumbnailUrl(), message);
                }

            }

            return;
        }
        //处理发送方消息
        handleSendMessage();
    }

    @Override
    protected void onBubbleClick() {
        EMVideoMessageBody videoBody = (EMVideoMessageBody) message.body();
        Log.d(TAG, "video view is on click");
        Intent intent = new Intent(context, ShowVideoActivity.class);
        intent.putExtra("msg", message);
        activity.startActivity(intent);
    }

    /**
     * 展示视频缩略图
     *
     * @param localThumb   本地缩略图路径
     * @param iv
     * @param thumbnailUrl 远程缩略图路径
     * @param message
     */
    private void showVideoThumbView(final String localThumb, final ImageView iv, String thumbnailUrl, final Message message) {
        // first check if the thumbnail image already loaded into cache
        Bitmap bitmap = ImageCache.getInstance().get(localThumb);
        if (bitmap != null) {
            // thumbnail image is already loaded, reuse the drawable
            iv.setImageBitmap(bitmap);

        } else {
            new AsyncTask<Void, Void, Bitmap>() {

                @Override
                protected Bitmap doInBackground(Void... params) {
                    if (new File(localThumb).exists()) {
                        return ImageUtils.decodeScaleImage(localThumb, 160, 160);
                    } else {
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Bitmap result) {
                    super.onPostExecute(result);
                    if (result != null) {
                        ImageCache.getInstance().put(localThumb, result);
                        iv.setImageBitmap(result);

                    } else {
                        if (message.status() == Message.Status.SUCCESS) {
                            if (CommonUtils.isNetWorkConnected(activity)) {
                                ChatClient.getInstance().chatManager().downloadThumbnail(message);
                            }
                        }

                    }
                }
            }.execute();
        }

    }


}