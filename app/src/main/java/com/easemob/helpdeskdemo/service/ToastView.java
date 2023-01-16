package com.easemob.helpdeskdemo.service;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ToastView extends RelativeLayout {
    public ToastView(View view) {
        super(view.getContext());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (view instanceof ViewGroup){
                    ViewGroup viewGroup = (ViewGroup) view;
                    setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    viewGroup.addView(ToastView.this, viewGroup.getChildCount());
                }
            }
        });
    }

    public ToastView(@NonNull Context context) {
        super(context);
    }

    public ToastView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ToastView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsShow = false;
        mHandler.removeCallbacksAndMessages(null);
        mTextView = null;
        mHandler = null;
    }

    private Handler mHandler = new Handler();
    private TextView mTextView;
    private boolean mIsShow;
    public void showAndMessage(String msg) {
        if (mIsShow){
            return;
        }

        if (mTextView == null){
            mTextView = new TextView(getContext());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    MarginLayoutParams.WRAP_CONTENT,MarginLayoutParams.WRAP_CONTENT
            );

            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.bottomMargin = dp2px(40);
            mTextView.setLayoutParams(params);

            mTextView.setPadding(dp2px(10),dp2px(6),dp2px(10),dp2px(6));
            mTextView.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    Rect rect = new Rect();
                    view.getGlobalVisibleRect(rect);
                    int leftMargin = 0;
                    int topMargin = 0;
                    Rect selfRect = new Rect(leftMargin, topMargin,
                            rect.right - rect.left - leftMargin,
                            rect.bottom - rect.top - topMargin);
                    outline.setRoundRect(selfRect, dp2px(10));
                }
            });
            mTextView.setClipToOutline(true);
            mTextView.setBackgroundColor(Color.parseColor("#ee999999"));
        }


        show(mTextView);
        mTextView.setText(msg);
        setBackgroundColor(Color.TRANSPARENT);
        mTextView.setTextColor(Color.WHITE);
        if (!isContain(mTextView)){
            addView(mTextView);
        }

        if (getVisibility() != VISIBLE){
            setVisibility(VISIBLE);
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mIsShow = false;
                dismiss();
            }
        }, 3000);
    }

    private void show(View view){
        for (int i = 0; i < getChildCount(); i++){
            View childAt = getChildAt(i);
            if (childAt == view){
                view.setVisibility(VISIBLE);
            }else {
                childAt.setVisibility(GONE);
            }
        }
    }

    private void hiddenAll(){
        for (int i = 0; i < getChildCount(); i++){
            View childAt = getChildAt(i);
            if (childAt.getVisibility() == VISIBLE){
                childAt.setVisibility(GONE);
            }
        }
    }

    private boolean isContain(View view){
        for (int i = 0; i < getChildCount(); i++){
            View childAt = getChildAt(i);
            if (childAt == view){
                return true;
            }
        }
        return false;
    }

    private int dp2px(int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    public void dismiss() {
        if (getVisibility() == VISIBLE){
            hiddenAll();
            setVisibility(GONE);
        }
    }
}
