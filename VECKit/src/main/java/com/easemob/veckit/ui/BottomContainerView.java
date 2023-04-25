package com.easemob.veckit.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.WrapperListAdapter;

import androidx.annotation.Nullable;

import com.easemob.veckit.R;
import com.easemob.veckit.utils.Utils;
import com.easemob.veckit.utils.ViewOnClickUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BottomContainerView extends LinearLayout implements ViewOnClickUtils.OnClickListener {
    private List<ViewIconData> mDataList;
    private int mHeight;

    public BottomContainerView(Context context, int height) {
        super(context);
        mHeight = height;
        init();
    }

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
        if (mHeight == 0){
            mHeight = getResources().getDimensionPixelSize(R.dimen.bottom_nav_height);
        }
        if (mDataList == null){
            mDataList = new ArrayList<>();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, widthMode), MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY));
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

    void clear(){
        if (mViewMap != null){
            mViewMap.clear();
        }

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
            addView(createView(i, iconData));
        }

    }

    public void addIconsFive(List<ViewIconData> icons){
        if (icons == null){
            throw new RuntimeException("icons is null.");
        }

        clear();
        mDataList.addAll(icons);
        FrameLayout c1 = createC();
        FrameLayout c2 = createC();
        FrameLayout center = createCenter();
        FrameLayout c4 = createC();
        FrameLayout c5 = createC();
        ViewIconData data_one = mDataList.get(0);
        data_one.mIndex = 0;
        addView(createViewC(c1, 0, data_one));

        ViewIconData data_two = mDataList.get(1);
        data_two.mIndex = 1;
        addView(createViewC(c2, 1, data_two));

        ViewIconData data_three = mDataList.get(2);
        data_three.mIndex = 2;
        addView(createViewC(center, 2, data_three));

        ViewIconData data_four = mDataList.get(3);
        data_four.mIndex = 3;
        addView(createViewC(c4, 3, data_four));

        ViewIconData data_five = mDataList.get(4);
        data_five.mIndex = 4;
        addView(createViewC(c5, 4, data_five));

        /*if (mDataList.size() > 5){
            for (int i = 5; i < mDataList.size(); i++){
                ViewIconData iconData = mDataList.get(i);
                iconData.mIndex = i;
                createView(i, iconData);
            }
        }*/
    }

    View getCenterView(){
        ViewGroup childAt = (ViewGroup) getChildAt(2);
        childAt.setOnClickListener(null);
        View view = childAt.getChildAt(0);
        childAt.removeAllViews();
        mViewMap.remove(2);



        FrameLayout f = new FrameLayout(getContext());
        // f.setOnClickListener(this);
        ViewOnClickUtils.onClick(f, this);
        f.setTag(mDataList.get(2));
        f.setPadding(Utils.dp2px(getContext(), 12),Utils.dp2px(getContext(), 12), Utils.dp2px(getContext(), 12),Utils.dp2px(getContext(), 12));



        View v = new View(getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(Utils.dp2px(getContext(), 40), Utils.dp2px(getContext(), 40));
        params.gravity = Gravity.CENTER;
        v.setLayoutParams(params);
        v.setBackgroundResource(R.drawable.hd_write_shape);
        f.addView(v);


        f.addView(view);

        f.measure(0,0);
        ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
        layoutParams.width = f.getMeasuredWidth();

        return f;
    }

    public void setCustomItemState(int index, boolean isSelect){
        if (index >= mDataList.size()){
            return;
        }
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

    private View createViewC(FrameLayout c, int index, ViewIconData iconData){
        c.setTag(iconData);
        IconTextView textView = new IconTextView(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
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
        if (iconData.mIsHasNum){
            c.addView(createNumView(index));
        }
        return c;
    }
    private View createView(int index, ViewIconData iconData){
        FrameLayout c = createC();
        c.setTag(iconData);
        IconTextView textView = new IconTextView(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
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
        if (iconData.mIsHasNum){
            c.addView(createNumView(index));
        }
        return c;
    }

    private Map<Integer, TextView> mViewMap = new HashMap<>();
    private View createNumView(int index){
        TextView textView = new TextView(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.END|Gravity.TOP;
        textView.setLayoutParams(layoutParams);
        textView.setGravity(Gravity.CENTER);
        textView.setVisibility(GONE);

        int px = Utils.dp2px(getContext(), 2);
        textView.setPadding(px, px, px, px);

        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundResource(R.drawable.num_corners_bg);
        mViewMap.put(index, textView);
        return textView;
    }

    void showNum(int index, int num){
        TextView textView = mViewMap.get(index);
        if (textView == null){
            return;
        }
        if (num <= 0){
            if (textView.getVisibility() == VISIBLE){
                textView.setVisibility(GONE);
            }
        }else if (num > 99){
            if (textView.getVisibility() != VISIBLE){
                textView.setVisibility(VISIBLE);
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) textView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.topMargin = Utils.dp2px(getContext(), 4);
            layoutParams.rightMargin = Utils.dp2px(getContext(), 10);
            textView.setText("99+");
        }else {
            if (textView.getVisibility() != VISIBLE){
                textView.setVisibility(VISIBLE);
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) textView.getLayoutParams();
            int size = Utils.dp2px(getContext(), 16);
            layoutParams.topMargin = Utils.dp2px(getContext(), 4);
            layoutParams.rightMargin = Utils.dp2px(getContext(), 10);
            layoutParams.width = size;
            layoutParams.height = size;
            textView.setText(String.valueOf(num));
        }
    }

    private FrameLayout createC(){
        FrameLayout linearLayout = new FrameLayout(getContext());
        LayoutParams layoutParams = new LayoutParams(0, LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;
        linearLayout.setLayoutParams(layoutParams);
        // linearLayout.setOnClickListener(this);
        ViewOnClickUtils.onClick(linearLayout, this);
        return linearLayout;
    }

    private FrameLayout createCenter(){
        FrameLayout linearLayout = new FrameLayout(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(layoutParams);
        // linearLayout.setOnClickListener(this);
        ViewOnClickUtils.onClick(linearLayout, this);
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

    public OnViewPressStateListener getOnViewPressStateListener(){
        return mListener;
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
        // 消息
        public static final String TYPE_ITEM_MESSAGE = "message";
        // 更多
        public static final String TYPE_ITEM_MORE = "more";

        String mDefaultIcon = "";
        String mDefaultIconColor = "#000000";
        // 两种状态。点击和选中
        // mIsClickState点击事件false，选中true
        boolean mIsClickState;
        String mPressIcon = "";
        String mPressIconColor = "#ff4400";
        int mSize = 32;
        int mIndex;
        boolean mState;

        boolean mIsHasNum;

        TextView mTextView;

        boolean mIsCustomState;

        String mName;

        public boolean isCustomState() {
            return mIsCustomState;
        }

        public boolean isClickState() {
            return mIsClickState;
        }

        public void setHasNum(boolean hasNum) {
            mIsHasNum = hasNum;
        }

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
