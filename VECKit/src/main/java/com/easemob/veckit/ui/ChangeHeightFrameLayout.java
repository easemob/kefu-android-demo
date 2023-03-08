package com.easemob.veckit.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class ChangeHeightFrameLayout extends RelativeLayout implements Animator.AnimatorListener {

    private ObjectAnimator mAnimator;
    private long mTime = 300;

    public ChangeHeightFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public ChangeHeightFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChangeHeightFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int mHeight;
    private boolean mIsGetHeight;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (!mIsGetHeight){
            mHeight = height;
            // 16 : 9
            // height = width * 9 / 16;
            // mHalfHeight = width * 9 / 16;

            mFitHeight = mHeight * 1 / 3;
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

    private int mFitHeight;
    public void setDefaultShowHeight(int fitHeight){
        if (mFitHeight != fitHeight){
            mFitHeight = fitHeight;
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
        mHeightChangeCallback = null;
    }

    private boolean mIsRunAnimator;
    private boolean mIsRunFull = true;
    public void showFitHeight(){
        if (mAnimator != null && !mIsRunAnimator){
            mIsRunFull = false;
            if (mAnimator.isRunning()){
                mAnimator.cancel();
            }

            mAnimator.setIntValues(mHeight, mFitHeight);
            mAnimator.setDuration(mTime);
            mAnimator.start();
        }
    }

    public boolean isFullScreen(){
        return mIsRunFull;
    }

    public void showFullHeight(){
        if (mAnimator != null && !mIsRunAnimator){
            mIsRunFull = true;
            if (mAnimator.isRunning()){
                mAnimator.cancel();
            }
            mAnimator.setDuration(mTime);
            mAnimator.setIntValues(mFitHeight, mHeight);
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

    private IHeightChangeCallback mHeightChangeCallback;
    public void setHeightChangeCallback(IHeightChangeCallback closeFlatCallback){
        mHeightChangeCallback = closeFlatCallback;
    }


    @Override
    public void onAnimationStart(Animator animation) {
        mIsRunAnimator = true;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        mIsRunAnimator = false;
        if (mHeightChangeCallback != null){
            mHeightChangeCallback.onHeightChanged(mIsRunFull);
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        mIsRunAnimator = false;
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    public interface IHeightChangeCallback{
        void onHeightChanged(boolean isFullScreen);
    }
}
