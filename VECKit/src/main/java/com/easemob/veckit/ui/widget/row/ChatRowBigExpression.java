package com.easemob.veckit.ui.widget.row;


import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.easemob.veckit.R;
import com.easemob.veckit.agora.Constant;
import com.easemob.veckit.ui.widget.utils.UIProvider;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.emojicon.Emojicon;

/**
 * 大表情(动态表情)
 *
 */
public class ChatRowBigExpression extends ChatRowText {

    private ImageView imageView;


    public ChatRowBigExpression(Context context, Message message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct() == Message.Direct.RECEIVE ?
                R.layout.vec_row_received_bigexpression : R.layout.vec_row_sent_bigexpression, this);
    }

    @Override
    protected void onFindViewById() {
        percentageView = (TextView) findViewById(R.id.percentage);
        imageView = (ImageView) findViewById(R.id.image);
    }


    @Override
    public void onSetUpView() {
        String emojiconId = message.getStringAttribute(Constant.MESSAGE_ATTR_EXPRESSION_ID, null);
        Emojicon emojicon = null;
        if(UIProvider.getInstance().getEmojiconInfoProvider() != null){
            emojicon =  UIProvider.getInstance().getEmojiconInfoProvider().getEmojiconInfo(emojiconId);
        }
        if(emojicon != null){
            if(emojicon.getBigIcon() != 0){
                Glide.with(activity).load(emojicon.getBigIcon()).apply(RequestOptions.placeholderOf(R.drawable.hd_default_expression)).into(imageView);
//                Glide.with(activity).load(emojicon.getBigIcon()).placeholder(R.drawable.hd_default_expression).into(imageView);
            }else if(emojicon.getBigIconPath() != null){
                Glide.with(activity).load(emojicon.getBigIconPath()).apply(RequestOptions.placeholderOf(R.drawable.hd_default_expression)).into(imageView);
//                Glide.with(activity).load(emojicon.getBigIconPath()).placeholder(R.drawable.hd_default_expression).into(imageView);
            }else{
                imageView.setImageResource(R.drawable.hd_default_expression);
            }
        }

        handleTextMessage();
    }
}