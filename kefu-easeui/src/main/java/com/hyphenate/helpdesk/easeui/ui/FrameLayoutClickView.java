package com.hyphenate.helpdesk.easeui.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hyphenate.helpdesk.easeui.agora.AgoraRtcEngine;
import com.hyphenate.helpdesk.util.Log;

public class FrameLayoutClickView extends FrameLayout {
    public FrameLayoutClickView(@NonNull Context context) {
        super(context);
    }

    public FrameLayoutClickView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FrameLayoutClickView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    private float mDownX;
    private float mDownY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            mDownX = event.getX();
            mDownY = event.getY();
        }else if (event.getAction() == MotionEvent.ACTION_UP){
            float x = event.getX();
            float y = event.getY();
            if (mCallback != null && mDownX == x && mDownY == y){
                mCallback.onClick(this);
            }
        }
        return true;
    }

    private OnFrameLayoutClickViewCallback mCallback;
    public void setOnFrameLayoutClickViewCallback(OnFrameLayoutClickViewCallback clickViewCallback){
        this.mCallback = clickViewCallback;
    }

    public interface OnFrameLayoutClickViewCallback{
        void onClick(View v);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mCallback = null;
    }
}
