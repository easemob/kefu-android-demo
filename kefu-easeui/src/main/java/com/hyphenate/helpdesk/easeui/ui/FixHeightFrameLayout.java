package com.hyphenate.helpdesk.easeui.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;


public class FixHeightFrameLayout extends FrameLayout implements Animator.AnimatorListener {

    private ObjectAnimator mAnimator;

    public FixHeightFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public FixHeightFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FixHeightFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int mHeight;
    private int mHalfHeight;
    private boolean mIsGetHeight;

    private boolean mFirstHalf;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (!mIsGetHeight){
            mHeight = height;
            mHalfHeight = mHeight / 2;
        }

        if (mFirstHalf && !mIsRunAnimator){
            setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, widthMode), MeasureSpec.makeMeasureSpec(mHalfHeight, heightMode));
        }

    }



    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mIsGetHeight = true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mAnimator == null){
            mAnimator = new ObjectAnimator();
            mAnimator.setTarget(this);
            mAnimator.setPropertyName("height");
            mAnimator.addListener(this);
        }
    }

    public void setDefaultShowHeight(boolean isHalf){
        if (mFirstHalf != isHalf){
            this.mFirstHalf = isHalf;
            requestLayout();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsGetHeight = false;
        if (mAnimator != null){
            if (mAnimator.isRunning()){
                mAnimator.cancel();
            }
            mAnimator.removeListener(this);
            mAnimator = null;
        }
    }

    private boolean mIsRunAnimator;
    public void showHalfHeight(){
        if (mAnimator != null && !mIsRunAnimator && !mFirstHalf){
            if (mAnimator.isRunning()){
                mAnimator.cancel();
            }
            mFirstHalf = true;
            mAnimator.setIntValues(mHeight, mHalfHeight);
            mAnimator.setDuration(400);
            mAnimator.start();
        }

    }

    public void showFullHeight(){
        if (mAnimator != null && !mIsRunAnimator && mFirstHalf){
            if (mAnimator.isRunning()){
                mAnimator.cancel();
            }
            mFirstHalf = false;
            mAnimator.setDuration(400);
            mAnimator.setIntValues(mHalfHeight, mHeight);
            mAnimator.start();
        }
    }

    private void setHeight(int value){
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams != null){
            layoutParams.height = value;
            setLayoutParams(layoutParams);
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {
        mIsRunAnimator = true;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        mIsRunAnimator = false;
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        mIsRunAnimator = false;
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
