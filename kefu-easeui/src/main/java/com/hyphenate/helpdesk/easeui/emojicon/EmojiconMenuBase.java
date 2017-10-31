package com.hyphenate.helpdesk.easeui.emojicon;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.hyphenate.helpdesk.emojicon.Emojicon;

public class EmojiconMenuBase extends LinearLayout {
    protected EaseEmojiconMenuListener listener;

    public EmojiconMenuBase(Context context) {
        super(context);
    }

    @SuppressLint("NewApi")
    public EmojiconMenuBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public EmojiconMenuBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * 设置回调监听
     * @param listener
     */
    public void setEmojiconMenuListener(EaseEmojiconMenuListener listener){
        this.listener = listener;
    }

    public interface EaseEmojiconMenuListener{
        /**
         * 表情被点击
         * @param emojicon
         */
        void onExpressionClicked(Emojicon emojicon);
        /**
         * 删除按钮被点击
         */
        void onDeleteImageClicked();
    }
}