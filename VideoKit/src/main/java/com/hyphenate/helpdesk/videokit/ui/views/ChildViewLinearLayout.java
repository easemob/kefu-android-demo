package com.hyphenate.helpdesk.videokit.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class ChildViewLinearLayout extends LinearLayout {
    private int mMarginLeft;
    private int mMarginRight;
    private int mSpacing;
    private int mChildWidth;
    private int mWidth;

    public ChildViewLinearLayout(Context context, int marginLeft, int marginRight, int spacing) {
        this(context, null);
        this.mMarginLeft = marginLeft;
        this.mMarginRight = marginRight;
        this.mSpacing = spacing;
        // setPadding(mMarginLeft,getPaddingTop(), 0, getPaddingBottom());
    }

    public ChildViewLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChildViewLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mChildWidth = (mWidth - mMarginLeft - mMarginRight - 2 * mSpacing) / 3;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        set(child);
    }

    @Override
    public void addView(View child, int index) {
        super.addView(child, index);
        set(child);
    }

    private void set(View child){
        MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
        layoutParams.width = mChildWidth;
        layoutParams.rightMargin = mSpacing;
    }
}
