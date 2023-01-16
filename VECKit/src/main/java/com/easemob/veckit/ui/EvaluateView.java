package com.easemob.veckit.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EvaluateView extends FrameLayout {
    private boolean mIsAllowClick;
    public EvaluateView(@NonNull Context context) {
        super(context);
    }

    public EvaluateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EvaluateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return !mIsAllowClick;
    }

    public void setIsAllowClick(boolean isAllowClick){
        this.mIsAllowClick = isAllowClick;
    }

}
