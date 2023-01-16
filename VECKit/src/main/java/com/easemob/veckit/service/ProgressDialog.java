package com.easemob.veckit.service;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ProgressDialog extends RelativeLayout {
    public ProgressDialog(View view) {
        super(view.getContext());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (view instanceof ViewGroup){
                    ViewGroup viewGroup = (ViewGroup) view;
                    setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    viewGroup.addView(ProgressDialog.this, viewGroup.getChildCount());
                }
                setVisibility(INVISIBLE);
            }
        });

    }

    public ProgressDialog(@NonNull Context context) {
        super(context);
    }

    public ProgressDialog(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressDialog(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsShow = false;
        mHandler.removeCallbacksAndMessages(null);
        mTextView = null;
        mHandler = null;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private TextView mTextView;
    private boolean mIsShow;
    private String mMsg;
    public void setMessage(String msg) {
        mMsg = msg;
    }

    private void createTextView(String msg) {
        if (mTextView == null){
            mTextView = new TextView(getContext());
            LayoutParams params = new LayoutParams(
                    MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.WRAP_CONTENT
            );

            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            params.bottomMargin = dp2px(40);
            mTextView.setLayoutParams(params);

            mTextView.setPadding(dp2px(40),dp2px(20),dp2px(40),dp2px(20));
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
            mTextView.setBackgroundColor(Color.WHITE);
            mTextView.setGravity(Gravity.CENTER);
            setBackgroundColor(Color.parseColor("#22666666"));
            addView(mTextView);
        }

        if (getVisibility() == VISIBLE){
            setVisibility(GONE);
        }
        mTextView.setText(msg);
    }

    public void show(){
        if (mIsShow){
            return;
        }
        mIsShow = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                createTextView(mMsg);
                showTextView(mTextView);
                if (getVisibility() != VISIBLE){
                    setVisibility(VISIBLE);
                }
            }
        });
    }

    private void showTextView(View view){
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

    private int dp2px(int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    public void dismiss() {
        mIsShow = false;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (getVisibility() == VISIBLE){
                    hiddenAll();
                    setVisibility(GONE);
                }
            }
        });
    }

    public boolean isShowing() {
        return mIsShow;
    }
}
