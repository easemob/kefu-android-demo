package com.easemob.veckit.ui.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;

public abstract class EaseChatPrimaryMenuBase extends RelativeLayout {

    protected EaseChatPrimaryMenuListener listener;
    protected Context activity;
    protected InputMethodManager inputManager;
    protected Button emojiSendBtn;

    public EaseChatPrimaryMenuBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public EaseChatPrimaryMenuBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EaseChatPrimaryMenuBase(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.activity = context;
        inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public void setEmojiSendBtn(Button btn){
       emojiSendBtn = btn;
    }

    /**
     * 设置主按钮栏相关listener
     *
     * @param listener
     */
    public void setChatPrimaryMenuListener(EaseChatPrimaryMenuListener listener) {
        this.listener = listener;
    }

    /**
     * 表情输入
     *
     * @param emojiContent
     */
    public abstract void onEmojiconInputEvent(CharSequence emojiContent);

    /**
     * 表情删除
     */
    public abstract void onEmojiconDeleteEvent();

    /**
     * 整个扩展按钮栏(包括表情栏)隐藏
     */
    public abstract void onExtendAllContainerHide();

    /**
     * 输入文本内容
     * @param txtContent
     */
    public abstract void setInputMessage(CharSequence txtContent);

	/**
	 * 是否正在录制语音
     */
    public abstract boolean isRecording();

    /**
     * 获取发送按钮
     * @return
     */
    public abstract View getButtonSend();

    /**
     * 隐藏软键盘
     */
    /*public void hideKeyboard() {
        if (activity.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (activity.getCurrentFocus() != null)
                inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }*/

    public void hideKeyboard() {
        inputManager.hideSoftInputFromWindow(getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void displayKeyboard(View view) {
        inputManager.showSoftInput(view, 0);
    }

    public interface EaseChatPrimaryMenuListener {
        /**
         * 发送按钮点击事件
         *
         * @param content
         *            发送内容
         */
        void onSendBtnClicked(String content);

        /**
         * 录音完成
         *
         */
        void onRecorderCompleted(float seconds, String filePath);

        /**
         * 长按说话按钮隐藏或显示事件
         */
        void onToggleVoiceBtnClicked();

        /**
         * 显示或隐藏扩展menu按钮的点击事件
         */
        void onToggleExtendClicked();

        /**
         * 隐藏或显示表情栏按钮点击事件
         */
        void onToggleEmojiconClicked();

        /**
         * 文字输入框点击事件
         */
        void onEditTextClicked();

        // 显示图库
        void showGallery();

    }

}