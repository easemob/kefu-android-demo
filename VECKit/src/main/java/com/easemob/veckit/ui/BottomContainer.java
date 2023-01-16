package com.easemob.veckit.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.easemob.veckit.R;
import com.easemob.veckit.utils.Utils;

import java.util.List;

public class BottomContainer extends FrameLayout {
    private int mHeight;
    private BottomContainerView mBottomContainerView;
    private FrameLayout mFrameLayout;
    private int mMBottomHeight;

    public BottomContainer(Context context) {
        super(context);
        init(context);
    }

    public BottomContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BottomContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        int bottomContainerViewHeight = getResources().getDimensionPixelSize(R.dimen.dp_52);
        int topHeight = Utils.dp2px(context, 30);
        mHeight = bottomContainerViewHeight + topHeight;

        mMBottomHeight = Utils.dp2px(context, 15);

        mFrameLayout = new FrameLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, mHeight);
        mFrameLayout.setLayoutParams(params);
        addView(mFrameLayout);

        mBottomContainerView = new BottomContainerView(context, bottomContainerViewHeight);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, bottomContainerViewHeight);
        layoutParams.gravity = Gravity.BOTTOM;
        mBottomContainerView.setLayoutParams(layoutParams);
        mFrameLayout.addView(mBottomContainerView);

        View view = new View(context);
        view.setClickable(true);
        view.setBackgroundColor(Color.WHITE);
        FrameLayout.LayoutParams viewParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, mMBottomHeight);
        viewParams.gravity = Gravity.BOTTOM;
        view.setLayoutParams(viewParams);
        addView(view);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, widthMode), MeasureSpec.makeMeasureSpec(mHeight + mMBottomHeight, MeasureSpec.EXACTLY));
    }

    public void addIcons(List<BottomContainerView.ViewIconData> icons){
        if (icons == null){
            throw new RuntimeException("icons is null.");
        }

        if (icons.size() < 5){
            throw new RuntimeException("icons size is not five.");
        }

        mBottomContainerView.clear();
        mBottomContainerView.addIconsFive(icons);
        View centerView = mBottomContainerView.getCenterView();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.topMargin = Utils.dp2px(getContext(), 10);
        centerView.setBackgroundResource(R.drawable.hd_black_shape);
        centerView.setLayoutParams(params);
        addView(centerView);
    }

    public BottomContainerView.OnViewPressStateListener getOnViewPressStateListener(){
        if (mBottomContainerView != null){
            return mBottomContainerView.getOnViewPressStateListener();
        }

        return null;
    }

    public void setCustomItemState(int index, boolean isSelect){
        if (mBottomContainerView != null){
            mBottomContainerView.setCustomItemState(index, isSelect);
        }
    }

    public void setOnBottomContainerViewPressStateListener(BottomContainerView.OnViewPressStateListener listener){
        if (mBottomContainerView != null){
            mBottomContainerView.setOnBottomContainerViewPressStateListener(listener);
        }
    }

    public void showNum(int index, int num){
        if (mBottomContainerView != null){
            mBottomContainerView.showNum(index, num);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeAllViews();
        mBottomContainerView = null;
    }
}
