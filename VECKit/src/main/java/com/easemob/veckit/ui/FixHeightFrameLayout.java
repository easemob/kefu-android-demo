package com.easemob.veckit.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class FixHeightFrameLayout extends FrameLayout implements Animator.AnimatorListener {

    private ObjectAnimator mAnimator;
    private long mTime = 300;

    public FixHeightFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public FixHeightFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FixHeightFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
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
        if (!mIsGetHeight){
            mHeight = height;
            // 16 : 9
            // height = width * 9 / 16;
            // mHalfHeight = width * 9 / 16;
            if (mFirstHalf){
                mHalfHeight = mHeight / 2;
            }
        }

        /*if (mFirstHalf && !mIsRunAnimator){
            super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mHalfHeight, MeasureSpec.EXACTLY));
        }*/
        /*if (!mIsRunAnimator){
            Log.e("ffffffffff","mHalfHeight = "+mHalfHeight);
            mHalfHeight = mHeight - mExtHeight;
            setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mHalfHeight, MeasureSpec.EXACTLY));
        }*/
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

    private int mExtHeight;
    public void setDefaultShowHeight(int extHeight){
        if (mExtHeight != extHeight){
            mExtHeight = extHeight;
            requestLayout();
            mHalfHeight = mHeight - mExtHeight;
        }
    }

    public void setDefaultShowHeight(){
        mFirstHalf = true;
        if (mHalfHeight == 0){
            requestLayout();
            if (mHalfHeight == 0){
                mHalfHeight = mHeight / 2;
            }
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
        mCloseFlatCallback = null;
    }

    private boolean mIsRunAnimator;
    private boolean mIsRunFull = true;
    public void showHalfHeight(){
        if (mAnimator != null && !mIsRunAnimator){
            mIsRunFull = false;
            if (mAnimator.isRunning()){
                mAnimator.cancel();
            }

            mAnimator.setIntValues(mHeight, mHalfHeight);
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

    public void setCloseFlatCallback(ICloseFlatCallback closeFlatCallback){
        mCloseFlatCallback = closeFlatCallback;
    }

    private ICloseFlatCallback mCloseFlatCallback;
    private boolean mIsRunCloseFlat;
    public void closeFlat() {
        if (mAnimator != null &&  mAnimator.isRunning()){
            return;
        }

        if (mIsRunFull){
            mIsRunAnimator = false;
            mIsRunCloseFlat = false;
            if (mCloseFlatCallback != null){
                mCloseFlatCallback.closeFlat(mIsRunFull);
            }
            return;
        }


        mIsRunCloseFlat = true;
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        int currentHeight = layoutParams.height;
        if (layoutParams.height == mHalfHeight){
            if (mAnimator !=null){
                if (mAnimator.isRunning()){
                    mAnimator.cancel();
                }
            }
            if (mCloseFlatCallback != null){
                mCloseFlatCallback.closeFlat(mIsRunFull);
            }
            mIsRunCloseFlat = false;
        }else {
            if (mAnimator !=null){
                if (mAnimator.isRunning()){
                    mAnimator.cancel();
                }
                mAnimator.setDuration(mTime);
                mAnimator.setIntValues(currentHeight, mHalfHeight);
                mAnimator.start();
            }
        }
    }



    @Override
    public void onAnimationStart(Animator animation) {
        mIsRunAnimator = true;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        mIsRunAnimator = false;
        if (mIsRunCloseFlat){
            mIsRunCloseFlat = false;
            if (mCloseFlatCallback != null){
                mCloseFlatCallback.closeFlat(mIsRunFull);
            }

        }else {
            if (mCloseFlatCallback != null){
                mCloseFlatCallback.onFullScreenCompleted(mIsRunFull);
            }
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        mIsRunAnimator = false;
        mIsRunCloseFlat = false;
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    public interface ICloseFlatCallback{
        void closeFlat(boolean isFullScreen);
        void onFullScreenCompleted(boolean isFullScreen);
    }
}
