package com.hyphenate.helpdesk.easeui.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.helpdesk.R;

import java.util.ArrayList;
import java.util.List;

public class BottomContainerView extends LinearLayout implements View.OnClickListener {
    private List<ViewIconData> mDataList;
    private int mHeight;

    public BottomContainerView(Context context) {
        super(context);
        init();
    }

    public BottomContainerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomContainerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setBackgroundColor(Color.WHITE);
        mHeight = getResources().getDimensionPixelSize(R.dimen.bottom_navi_height_62);
        if (mDataList == null){
            mDataList = new ArrayList<>();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, widthMode), MeasureSpec.makeMeasureSpec(mHeight, heightMode));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mDataList == null){
            mDataList = new ArrayList<>();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clear();
        mDataList = null;
    }

    private void clear(){
        for (int i = 0; i < getChildCount(); i++){
            View childAt = getChildAt(i);
            childAt.setTag(null);
            childAt.setOnClickListener(null);
        }
        removeAllViews();

        if (mDataList != null){
            for (ViewIconData data : mDataList){
                data.mTextView = null;
            }
            mDataList.clear();
        }
    }

    public void addIcons(List<ViewIconData> icons){
        if (icons == null){
            throw new RuntimeException("icons is null.");
        }

        clear();
        mDataList.addAll(icons);

        for (int i = 0; i < mDataList.size(); i++){
            ViewIconData iconData = mDataList.get(i);
            iconData.mIndex = i;
            addView(createView(iconData));
        }

    }

    private View createView(ViewIconData iconData){
        LinearLayout c = createC();
        c.setTag(iconData);
        IconTextView textView = new IconTextView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        textView.setText(iconData.mDefaultIcon);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, iconData.mSize);
        if (iconData.mIsClickState){
            //状态
            int[][] states = new int[2][];
            //按下
            states[0] = new int[] {android.R.attr.state_pressed};
            //默认
            states[1] = new int[] {};

            //状态对应颜色值（按下，默认）
            int[] colors = new int[] { Color.parseColor(iconData.mPressIconColor), Color.parseColor(iconData.mDefaultIconColor)};

            ColorStateList colorStateList = new ColorStateList(states, colors);
            textView.setTextColor(colorStateList);
        }else {
            textView.setTextColor(Color.parseColor(iconData.mDefaultIconColor));
        }
        c.addView(textView);
        iconData.mTextView = textView;
        return c;
    }

    private LinearLayout createC(){
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOnClickListener(this);
        return linearLayout;
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag instanceof ViewIconData){
            ViewIconData data = (ViewIconData) tag;
            if (mListener != null){
                if(!data.mState){
                    // press
                    data.mTextView.setText(data.mPressIcon);
                    if (!data.mIsClickState){
                        data.mTextView.setTextColor(Color.parseColor(data.mPressIconColor));
                    }

                }else {
                    data.mTextView.setText(data.mDefaultIcon);
                    if (!data.mIsClickState){
                        data.mTextView.setTextColor(Color.parseColor(data.mDefaultIconColor));
                    }
                }

                data.mState = !data.mState;
                boolean b = mListener.onPressStateChange(data.mIndex, data.mState);
                if (!data.mIsClickState){
                    if (!b){
                        // 还原状态
                        data.mState = !data.mState;
                        data.mTextView.setText(data.mDefaultIcon);
                        data.mTextView.setTextColor(data.mState ? Color.parseColor(data.mPressIconColor) : Color.parseColor(data.mDefaultIconColor));
                    }
                }
            }
        }
    }

    private OnViewPressStateListener mListener;
    public void setOnBottomContainerViewPressStateListener(OnViewPressStateListener listener){
        this.mListener = listener;
    }

    public interface OnViewPressStateListener{
        boolean onPressStateChange(int index, boolean isClick);
    }

    public static class ViewIconData{
        String mDefaultIcon = "";
        String mDefaultIconColor = "#000000";
        // 两种状态。点击和选中
        boolean mIsClickState;
        String mPressIcon = "";
        String mPressIconColor = "#ff4400";
        int mSize = 36;
        int mIndex;
        boolean mState;

        TextView mTextView;

        public ViewIconData(String defaultIcon, String defaultIconColor, String pressIcon, String pressIconColor, boolean isClickState){
            this.mDefaultIcon = defaultIcon;
            this.mPressIcon = pressIcon;
            this.mIsClickState = isClickState;
            this.mDefaultIconColor = defaultIconColor;
            this.mPressIconColor = pressIconColor;
        }

        public ViewIconData(String defaultIcon, String defaultIconColor, String pressIcon, String pressIconColor){
            this(defaultIcon, defaultIconColor, pressIcon, pressIconColor, false);
        }

        public ViewIconData(String defaultIcon, String defaultIconColor, String pressIcon, String pressIconColor, int textSize){
            this(defaultIcon, defaultIconColor, pressIcon, pressIconColor, false);
            this.mSize = textSize;
        }

        public ViewIconData(String defaultIcon, String defaultIconColor, String pressIcon, String pressIconColor, int textSize, boolean isClickState){
            this(defaultIcon, defaultIconColor, pressIcon, pressIconColor, false);
            this.mSize = textSize;
            this.mIsClickState = isClickState;
        }

        public ViewIconData(String defaultIcon, String pressIcon){
            this(defaultIcon, "#000000", pressIcon, "#ff4400", false);
        }

        // 默认单位sp
        public void setTextSize(int textSize){
            this.mSize = textSize;
        }

        public int getIndex() {
            return mIndex;
        }
    }
}
