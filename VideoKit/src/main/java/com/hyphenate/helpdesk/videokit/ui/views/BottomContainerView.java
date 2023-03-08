package com.hyphenate.helpdesk.videokit.ui.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;


import com.hyphenate.helpdesk.videokit.R;

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

    public void setCustomItemState(int index, boolean isSelect){
        ViewIconData data = mDataList.get(index);
        data.mState = isSelect;
        if (isSelect){
            data.mTextView.setText(data.mDefaultIcon);
            data.mTextView.setTextColor(Color.parseColor(data.mDefaultIconColor));
        }else {
            data.mTextView.setText(data.mPressIcon);
            data.mTextView.setTextColor(Color.parseColor(data.mPressIconColor));
        }
    }

    public void setCustomItemColor(int index, String color){
        ViewIconData data = mDataList.get(index);
        data.mTextView.setTextColor(Color.parseColor(color));
    }

    public void setCustomItemIcon(int index, String icon){
        ViewIconData data = mDataList.get(index);
        data.mTextView.setText(icon);
    }

    public View getIconView(int index){
        return mDataList.get(index).mTextView;
    }

    private View createView(ViewIconData iconData){
        LinearLayout c = createC();
        c.setTag(iconData);
        IconTextView textView = new IconTextView(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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
        LayoutParams layoutParams = new LayoutParams(0, LayoutParams.MATCH_PARENT);
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
                if (!data.mIsCustomState){

                    data.mState = !data.mState;
                    if (!data.mIsClickState){
                        data.mTextView.setText(data.mState ? data.getDefaultIcon() : data.getPressIcon());
                        data.mTextView.setTextColor(data.mState ? Color.parseColor(data.mDefaultIconColor) : Color.parseColor(data.mPressIconColor));
                    }

                    boolean b = mListener.onPressStateChange(data.mIndex, data.mState, data.mIsCustomState);
                    // mIsClickState点击事件false，选中true
                    if (!data.mIsClickState){
                        if (!b){
                            // 还原状态
                            data.mState = !data.mState;
                            data.mTextView.setText(data.mState ? data.mDefaultIcon : data.getPressIcon());
                            data.mTextView.setTextColor(data.mState ? Color.parseColor(data.getDefaultIconColor()) : Color.parseColor(data.mPressIconColor));
                        }
                    }
                }else {

                    data.mState = !data.mState;
                    if (!data.mIsClickState){
                        data.mTextView.setText(data.mState ? data.getDefaultIcon() : data.getPressIcon());
                        data.mTextView.setTextColor(data.mState ? Color.parseColor(data.mDefaultIconColor) : Color.parseColor(data.mPressIconColor));
                    }

                    boolean b = mListener.onPressStateChange(data.mIndex, data.mState, data.mIsCustomState);
                    if (!b){
                        // 还原状态
                        data.mState = !data.mState;
                        data.mTextView.setText(data.mState ? data.mDefaultIcon : data.getPressIcon());
                        data.mTextView.setTextColor(data.mState ? Color.parseColor(data.mDefaultIconColor) : Color.parseColor(data.mPressIconColor));
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
        boolean onPressStateChange(int index, boolean isClick, boolean isCustomState);
    }

    public static class ViewIconData{
        // 声音
        public static final String TYPE_ITEM_VOICE = "voice";
        // 相机
        public static final String TYPE_ITEM_CAME = "camera";
        // 电话
        public static final String TYPE_ITEM_PHONE = "phone";
        // 分享
        public static final String TYPE_ITEM_SHARE = "share";
        // 电子白板
        public static final String TYPE_ITEM_FLAT = "flat";

        String mDefaultIcon = "";
        String mDefaultIconColor = "#000000";
        // 两种状态。点击和选中
        // mIsClickState点击事件false，选中true
        boolean mIsClickState;
        String mPressIcon = "";
        String mPressIconColor = "#ff4400";
        int mSize = 36;
        int mIndex;
        boolean mState;

        TextView mTextView;

        boolean mIsCustomState;

        String mName;

        public ViewIconData(String defaultIcon, String defaultIconColor, String pressIcon, String pressIconColor,
                            boolean isClickState, String name){
            this.mDefaultIcon = defaultIcon;
            this.mPressIcon = pressIcon;
            this.mIsClickState = isClickState;
            this.mDefaultIconColor = defaultIconColor;
            this.mPressIconColor = pressIconColor;
            this.mName = name;
        }

        public ViewIconData(String defaultIcon, String defaultIconColor, String pressIcon, String pressIconColor, String name){
            this(defaultIcon, defaultIconColor, pressIcon, pressIconColor, false, name);
        }

        public ViewIconData(boolean isCustomState, String defaultIcon, String defaultIconColor, String pressIcon, String pressIconColor, String name){
            this(defaultIcon, defaultIconColor, pressIcon, pressIconColor, false, name);
            this.mIsCustomState = isCustomState;
        }

        public ViewIconData(boolean isCustomState, String defaultIcon, String defaultIconColor, String pressIcon, String pressIconColor, boolean isClickState, String name){
            this(defaultIcon, defaultIconColor, pressIcon, pressIconColor, false, name);
            this.mIsCustomState = isCustomState;
            this.mIsClickState = isClickState;
        }

        public ViewIconData(String defaultIcon, String defaultIconColor, String pressIcon, String pressIconColor, int textSize, String name){
            this(defaultIcon, defaultIconColor, pressIcon, pressIconColor, false, name);
            this.mSize = textSize;
        }

        public ViewIconData(String defaultIcon, String defaultIconColor, String pressIcon, String pressIconColor, int textSize, boolean isClickState, String name){
            this(defaultIcon, defaultIconColor, pressIcon, pressIconColor, false, name);
            this.mSize = textSize;
            this.mIsClickState = isClickState;
        }

        // 默认单位sp
        public void setTextSize(int textSize){
            this.mSize = textSize;
        }

        public int getIndex() {
            return mIndex;
        }

        public String getName() {
            return mName;
        }

        public void onDestroy(){
            mTextView = null;
        }

        public String getPressIcon() {
            return mPressIcon;
        }

        public String getDefaultIcon() {
            return mDefaultIcon;
        }

        public String getDefaultIconColor() {
            return mDefaultIconColor;
        }

        public String getPressIconColor() {
            return mPressIconColor;
        }

        public void setState(boolean state) {
            mState = state;
        }
    }
}
