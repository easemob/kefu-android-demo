package com.easemob.veckit.ui.widget.row;


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

import com.easemob.veckit.R;
import com.easemob.veckit.ui.widget.ChatRow;
import com.easemob.veckit.ui.widget.MessageAdapter;
import com.easemob.veckit.ui.widget.utils.SmileUtils;
import com.easemob.veckit.ui.widget.utils.UIProvider;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.Message;


public class ChatRowText extends ChatRow {

    private TextView contentView;

    public ChatRowText(Context context, Message message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct() == Message.Direct.RECEIVE ?
                R.layout.vec_row_received_message : R.layout.vec_row_sent_message, this);
    }

    @Override
    protected void onFindViewById() {
        contentView = (TextView) findViewById(R.id.tv_chatcontent);
    }

    @Override
    public void onSetUpView() {
        EMTextMessageBody txtBody = (EMTextMessageBody) message.body();

        //解析html超链接
        String content = txtBody.getMessage().replace("\n", "<br />");
        //fromHtml method will ignore \n in string
        CharSequence htmpTxt;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            htmpTxt = Html.fromHtml(content.replace("<", "&lt;"), Html.FROM_HTML_MODE_LEGACY);
        } else {
            htmpTxt = Html.fromHtml(content.replace("<", "&lt;"));
        }

        String new_content = htmpTxt.toString().replace("<br />", "\n");
        //解析表情
        Spannable span = SmileUtils.getSmiledText(context, new_content);

        //给超链接添加响应
        URLSpan[] urlSpans = span.getSpans(0, htmpTxt.length(), URLSpan.class);
        for (URLSpan span1 : urlSpans) {
            int start = span.getSpanStart(span1);
            int end = span.getSpanEnd(span1);
            int flag = span.getSpanFlags(span1);
            final String link = span1.getURL();
            span.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    //打开超链接
                    if (link != null && link.startsWith("http")) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(link);
                        intent.setData(content_url);
                        getContext().startActivity(intent);
                    }
                }
            }, start, end, flag);
            span.removeSpan(span1);
        }

        contentView.setLinksClickable(true);
        contentView.setMovementMethod(LinkMovementMethod.getInstance());
        // 设置内容
        contentView.setText(span, TextView.BufferType.SPANNABLE);

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