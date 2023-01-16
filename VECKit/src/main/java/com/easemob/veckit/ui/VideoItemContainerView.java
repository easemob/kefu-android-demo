package com.easemob.veckit.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.easemob.veckit.R;

import java.util.HashMap;
import java.util.Map;

public class VideoItemContainerView extends HorizontalScrollView implements View.OnClickListener ,FrameLayoutClickView.OnFrameLayoutClickViewCallback{

    private LinearLayout mLinearLayout;
    private Map<Integer, View> mViewMap = new HashMap<>();

    public VideoItemContainerView(Context context) {
        this(context, null);
    }

    public VideoItemContainerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoItemContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT
        );
        int dp_8 = getResources().getDimensionPixelOffset(R.dimen.dp_8);
        layoutParams.leftMargin = dp_8;
        mLinearLayout = new ChildViewLinearLayout(context, dp_8, dp_8, dp_8);
        mLinearLayout.setLayoutParams(layoutParams);
        mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        addView(mLinearLayout);
    }

    public void addVideoIconView(int uid, View child){
        if (child instanceof FrameLayoutClickView){
            ((FrameLayoutClickView) child).setOnFrameLayoutClickViewCallback(this);
        }else {
            child.setOnClickListener(this);
        }

        child.setTag(uid);
        mLinearLayout.addView(child);
        mViewMap.put(uid, child);
    }

    public void addVideoIconView(int index, int uid, View child){
        if (child instanceof FrameLayoutClickView){
            ((FrameLayoutClickView) child).setOnFrameLayoutClickViewCallback(this);
        }else {
            child.setOnClickListener(this);
        }
        child.setTag(uid);
        if (index >= mLinearLayout.getChildCount()){
            mLinearLayout.addView(child);
        }else {
            mLinearLayout.addView(child, index);
        }

        mViewMap.put(uid, child);
    }

    public boolean isContains(View child){
        for (int i = 0; i < mLinearLayout.getChildCount(); i++){
            if (child == mLinearLayout.getChildAt(i)){
                return true;
            }
        }

        return false;
    }

    // 记录显示的真实uid
    /*private int mKey = R.id.video_item_key;
    public void set(Integer uid, int realUid) {
        View view = mViewMap.get(uid);
        view.setTag(mKey,realUid);
    }*/

    public int getVideoIconViewCount(){
        return mLinearLayout.getChildCount();
    }

    public View getVideoIconView(int index){
        return mLinearLayout.getChildAt(index);
    }

    public View getVideoIconViewByUid(int uid){
        return mViewMap.get(uid);
    }

    public void removeVideoIconView(int uid, int delete){
        View view = mViewMap.remove(uid);
        if (view != null){
            mLinearLayout.removeView(view);
            if (view instanceof FrameLayoutClickView){
                ((FrameLayoutClickView) view).setOnFrameLayoutClickViewCallback(null);
            }else {
                view.setOnClickListener(null);
            }

            View v = mViewMap.get(delete);
            if (v != null){
                v.setTag(uid);
            }
        }else {
            for (int i = 0; i < mLinearLayout.getChildCount(); i++){
                View childAt = mLinearLayout.getChildAt(i);
                Object tag = childAt.getTag();
                if (tag instanceof Integer){
                    Integer n = (Integer) tag;
                    if (n == uid){
                        mLinearLayout.removeViewAt(i);
                        if (childAt instanceof FrameLayoutClickView){
                            ((FrameLayoutClickView) childAt).setOnFrameLayoutClickViewCallback(null);
                        }else {
                            childAt.setOnClickListener(null);
                        }
                        break;
                    }
                }
            }
        }


    }

    public void removeVideoIconView(int uid){
        View view = mViewMap.remove(uid);
        if (view instanceof FrameLayoutClickView){
            ((FrameLayoutClickView) view).setOnFrameLayoutClickViewCallback(null);
        }else {
            view.setOnClickListener(null);
        }
        for (int i = 0; i < mLinearLayout.getChildCount(); i++){
            View childAt = mLinearLayout.getChildAt(i);
            Object tag = childAt.getTag();
            if (tag instanceof Integer){
                Integer n = (Integer) tag;
                if (n == uid){
                    mLinearLayout.removeViewAt(i);
                    if (view instanceof FrameLayoutClickView){
                        ((FrameLayoutClickView) view).setOnFrameLayoutClickViewCallback(null);
                    }else {
                        view.setOnClickListener(null);
                    }
                    break;
                }
            }
        }

    }

    public void removeAllVideoIconView(){
        for (View view : mViewMap.values()){
            mLinearLayout.removeView(view);
            view.setOnClickListener(null);
            ViewParent parent = view.getParent();
            if (parent instanceof ViewGroup){
                ((ViewGroup)parent).removeView(view);
            }
        }
        mViewMap.clear();
    }

    @Override
    public void onClick(View v) {
        if (mClickListener != null){
            mClickListener.iconViewClick(getIndex(v), v, getTag(v));
        }
    }

    private int getIndex(View view){
        for (int i = 0; i < mLinearLayout.getChildCount(); i++){
            if (mLinearLayout.getChildAt(i) == view){
                return i;
            }
        }
        return 0;
    }

    private Object getTag(View view){
        for (int i = 0; i < mLinearLayout.getChildCount(); i++){
            if (mLinearLayout.getChildAt(i) == view){
                return view.getTag();
            }
        }
        return null;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mLinearLayout != null){
            mLinearLayout.removeAllViews();
        }
        removeAllViews();
        mViewMap.clear();
        mClickListener = null;
        mViewMap = null;
    }

    private OnVideoIconViewClickListener mClickListener;
    public void setOnVideoIconViewClickListener(OnVideoIconViewClickListener listener){
        this.mClickListener = listener;
    }

    public interface OnVideoIconViewClickListener{
        void iconViewClick(int index, View view, Object tag);
    }
}
