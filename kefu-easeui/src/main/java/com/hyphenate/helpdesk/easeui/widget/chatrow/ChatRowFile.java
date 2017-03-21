package com.hyphenate.helpdesk.easeui.widget.chatrow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMNormalFileMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.easeui.UIProvider;
import com.hyphenate.helpdesk.easeui.adapter.MessageAdapter;
import com.hyphenate.helpdesk.easeui.ui.ShowNormalFileActivity;
import com.hyphenate.util.FileUtils;
import com.hyphenate.util.TextFormater;

import java.io.File;

public class ChatRowFile extends ChatRow{

    protected TextView fileNameView;
    protected TextView fileSizeView;
    protected TextView fileStateView;

    private EMNormalFileMessageBody fileMessageBody;

    public ChatRowFile(Context context, Message message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct() == Message.Direct.RECEIVE ?
                R.layout.ease_row_received_file : R.layout.ease_row_sent_file, this);
    }

    @Override
    protected void onFindViewById() {
        fileNameView = (TextView) findViewById(R.id.tv_file_name);
        fileSizeView = (TextView) findViewById(R.id.tv_file_size);
        fileStateView = (TextView) findViewById(R.id.tv_file_state);
        percentageView = (TextView) findViewById(R.id.percentage);
    }


    @Override
    protected void onSetUpView() {
        fileMessageBody = (EMNormalFileMessageBody) message.getBody();
        String filePath = fileMessageBody.getLocalUrl();
        fileNameView.setText(fileMessageBody.getFileName());
        fileSizeView.setText(TextFormater.getDataSize(fileMessageBody.getFileSize()));
        if (message.direct() == Message.Direct.RECEIVE) { // 接收的消息
            File file = new File(filePath);
            if (file != null && file.exists()) {
                fileStateView.setText(R.string.Have_downloaded);
            } else {
                fileStateView.setText(R.string.Did_not_download);
            }
            return;
        }

        // until here, deal with send voice msg
        handleSendMessage();
    }

    /**
     * 处理发送消息
     */
    protected void handleSendMessage() {
        setMessageSendCallback();
        switch (message.getStatus()) {
            case SUCCESS:
                progressBar.setVisibility(View.INVISIBLE);
                if(percentageView != null)
                    percentageView.setVisibility(View.INVISIBLE);
                statusView.setVisibility(View.INVISIBLE);
                break;
            case FAIL:
                progressBar.setVisibility(View.INVISIBLE);
                if(percentageView != null)
                    percentageView.setVisibility(View.INVISIBLE);
                statusView.setVisibility(View.VISIBLE);
                break;
            case INPROGRESS:
                if (UIProvider.getInstance().isShowProgress())
                    progressBar.setVisibility(View.VISIBLE);
                if(percentageView != null){
                    percentageView.setVisibility(View.VISIBLE);
                    try {
                        int process = (int) percentageView.getTag();
                        percentageView.setText(process + "%");
                    }catch (Exception e){
                        percentageView.setText("");
                    }
                }
                statusView.setVisibility(View.INVISIBLE);
                break;
            default:
                progressBar.setVisibility(View.INVISIBLE);
                if(percentageView != null)
                    percentageView.setVisibility(View.INVISIBLE);
                statusView.setVisibility(View.VISIBLE);
                break;
        }
    }


    @Override
    protected void onUpdateView() {
        if (adapter instanceof MessageAdapter) {
            ((MessageAdapter) adapter).refresh();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onBubbleClick() {
        String filePath = fileMessageBody.getLocalUrl();
        File file = new File(filePath);
        if (file != null && file.exists()) {
            // 文件存在，直接打开
            FileUtils.openFile(file, (Activity) context);
        } else {
            // 下载
//            context.startActivity(new Intent(context, ShowNormalFileActivity.class).putExtra("msgbody", message.getBody()));
            context.startActivity(new Intent(context, ShowNormalFileActivity.class).putExtra("messageId", message.getMsgId()));
        }

    }
}