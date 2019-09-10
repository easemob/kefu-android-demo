package com.hyphenate.helpdesk.easeui.widget.chatrow;


import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.easeui.adapter.MessageAdapter;
import com.hyphenate.helpdesk.easeui.recorder.MediaManager;
import com.hyphenate.helpdesk.easeui.widget.ToastHelper;

import java.io.File;


public class ChatRowVoice extends ChatRowFile{

    private TextView voiceLengthView;
    private ImageView readStatusView;
    private int mMinItemWidth;
    private int mMaxItemWidth;


    public ChatRowVoice(Context context, Message message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
        if (adapter instanceof MessageAdapter){
            mMinItemWidth = ((MessageAdapter)adapter).mMinItemWidth;
            mMaxItemWidth = ((MessageAdapter)adapter).mMaxItemWidth;
        }

    }

    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct() == Message.Direct.RECEIVE ?
                R.layout.hd_row_received_voice : R.layout.hd_row_sent_voice, this);
    }

    @Override
    protected void onFindViewById() {
        voiceLengthView = (TextView) findViewById(R.id.tv_length);
        readStatusView = (ImageView) findViewById(R.id.iv_unread_voice);
    }

    @Override
    protected void onSetUpView() {
        EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) message.body();
        int len = voiceBody.getLength();
        if(len>0){
            voiceLengthView.setText(voiceBody.getLength() + "\"");
            voiceLengthView.setVisibility(View.VISIBLE);
        }else{
            voiceLengthView.setVisibility(View.GONE);
        }

        ViewGroup.LayoutParams layoutParams = bubbleLayout.getLayoutParams();
        layoutParams.width = (int)(mMinItemWidth + Math.min(mMaxItemWidth/180f*len, mMaxItemWidth));

        if (message.direct() == Message.Direct.RECEIVE) {
            if (message.isListened()) {
                // 隐藏语音未听标志
                readStatusView.setVisibility(View.GONE);
            } else {
                readStatusView.setVisibility(View.VISIBLE);
            }
            Log.d(TAG, "it is receive msg");
            if (voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
                progressBar.setVisibility(View.VISIBLE);
                setMessageReceiveCallback();
            } else {
                progressBar.setVisibility(View.GONE);

            }
            return;
        }
        // until here, deal with send voice msg
        handleSendMessage();
    }

    @Override
    protected void onUpdateView() {
        super.onUpdateView();
    }

    @Override
    protected void onBubbleClick() {
        EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) message.body();
        if (message.direct() == Message.Direct.SEND){
            playVoice(bubbleLayout, voiceBody.getLocalUrl(), true);
        }else{
            File file = new File(voiceBody.getLocalUrl());
            if (file.exists()){
                playVoice(bubbleLayout, voiceBody.getLocalUrl(), false);
            }else if (voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING || voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING){
                ToastHelper.show(activity, R.string.is_down_please_wait);
            }else{
                new AsyncTask<Void, Void, Void>(){

                    @Override
                    protected Void doInBackground(Void... params) {
                        ChatClient.getInstance().chatManager().downloadAttachment(message);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        if (adapter instanceof  MessageAdapter){
                            ((MessageAdapter)adapter).refresh();
                        }else{
                            adapter.notifyDataSetChanged();
                        }

                    }
                }.execute();

            }
        }
    }


    private void playVoice(View v, String localPath, final boolean isSend){


        //播放动画
        if (((MessageAdapter)adapter).animView != null){
            boolean preIsSend = (boolean) ((MessageAdapter)adapter).animView.getTag();
            if (preIsSend){
                ((MessageAdapter)adapter).animView.setBackgroundResource(R.drawable.hd_chatto_voice_playing);
            }else{
                ((MessageAdapter)adapter).animView.setBackgroundResource(R.drawable.hd_chatfrom_voice_playing);
            }
            ((MessageAdapter)adapter).animView = null;
        }
        if (((MessageAdapter)adapter).currentPlayView != null && ((MessageAdapter)adapter).currentPlayView == v){
            MediaManager.release();
            ((MessageAdapter)adapter).currentPlayView = null;
            return;
        }

        ((MessageAdapter)adapter).currentPlayView = v;
        ((MessageAdapter)adapter).animView = v.findViewById(R.id.id_recorder_anim);
        ((MessageAdapter)adapter).animView.setTag(isSend);
        if (isSend){
            ((MessageAdapter)adapter).animView.setBackgroundResource(R.drawable.hd_voice_to_icon);
        }else{
            ((MessageAdapter)adapter).animView.setBackgroundResource(R.drawable.hd_voice_from_icon);
            if (!message.isListened()){
                readStatusView.setVisibility(View.GONE);
                ChatClient.getInstance().chatManager().setMessageListened(message);
            }
        }

        AnimationDrawable anim = (AnimationDrawable) ((MessageAdapter)adapter).animView.getBackground();
        anim.start();

        //播放音频
        MediaManager.playSound(getContext(), localPath, new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                ((MessageAdapter)adapter).currentPlayView = null;
                if (isSend){
                    ((MessageAdapter)adapter).animView.setBackgroundResource(R.drawable.hd_chatto_voice_playing);
                }else{
                    ((MessageAdapter)adapter).animView.setBackgroundResource(R.drawable.hd_chatfrom_voice_playing);
                }

            }
        });

    }

}