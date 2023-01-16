package com.hyphenate.helpdesk.easeui.widget.chatrow;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.chat.adapter.message.EMATextMessageBody;
import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.easeui.UIProvider;
import com.hyphenate.helpdesk.easeui.adapter.MessageAdapter;
import com.hyphenate.helpdesk.easeui.ui.VecVebView;
import com.hyphenate.helpdesk.easeui.ui.VecWebView;
import com.hyphenate.helpdesk.easeui.util.SmileUtils;
import com.hyphenate.helpdesk.util.Log;

import org.json.JSONObject;


public class ChatRowRichText extends ChatRow{

    private VecWebView mVecVebView;

    public ChatRowRichText(Context context, Message message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct() == Message.Direct.RECEIVE ?
                R.layout.hd_row_received_rich_text : R.layout.hd_row_sent_rich_text, this);
    }

    @Override
    protected void onFindViewById() {
        mVecVebView = findViewById(R.id.vecVebView);
    }

    @Override
    public void onSetUpView() {
        try {
            EMTextMessageBody body = (EMTextMessageBody) message.body();
            String s = body.getMessage();
            JSONObject jsonObject = new JSONObject(s);
            String content = jsonObject.getString("content");
            mVecVebView.loadUrl(content);
        }catch (Exception e){
            e.printStackTrace();
        }
        handleTextMessage();
    }

    protected void handleTextMessage() {
        boolean isShowProgress = UIProvider.getInstance().isShowProgress();
        if (message.direct() == Message.Direct.SEND) {
            setMessageSendCallback();
            switch (message.status()) {
                case CREATE:
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.VISIBLE);
                    // 发送消息
                    break;
                case SUCCESS: // 发送成功
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.GONE);
                    break;
                case FAIL: // 发送失败
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS: // 发送中
                    if (isShowProgress)
                        progressBar.setVisibility(View.VISIBLE);
                    statusView.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
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
        // TODO Auto-generated method stub

    }



}