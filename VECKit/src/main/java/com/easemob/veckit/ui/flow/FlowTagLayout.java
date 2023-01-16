package com.easemob.veckit.ui.flow;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.easemob.veckit.R;
import com.easemob.veckit.bean.DegreeBean;
import com.easemob.veckit.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 流式标签布局
 * 原理：重写{@link ViewGroup#onMeasure(int, int)}
 * 和{@link ViewGroup#onLayout(boolean, int, int, int, int)}
 * 方法
 * Created by HanHailong on 15/10/19.
 */
public class FlowTagLayout extends ViewGroup implements View.OnClickListener {

    public FlowTagLayout(Context context) {
        super(context);
    }

    public FlowTagLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowTagLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //获取Padding
        // 获得它的父容器为它设置的测量模式和大小
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        //FlowLayout最终的宽度和高度值
        int resultWidth = 0;
        int resultHeight = 0;

        //测量时每一行的宽度
        int lineWidth = 0;
        //测量时每一行的高度，加起来就是FlowLayout的高度
        int lineHeight = 0;

        //遍历每个子元素
        for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
            View childView = getChildAt(i);
            //测量每一个子view的宽和高
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);

            //获取到测量的宽和高
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();

            //因为子View可能设置margin，这里要加上margin的距离
            MarginLayoutParams mlp = (MarginLayoutParams) childView.getLayoutParams();
            int realChildWidth = childWidth + mlp.leftMargin + mlp.rightMargin;
            int realChildHeight = childHeight + mlp.topMargin + mlp.bottomMargin;

            //如果当前一行的宽度加上要加入的子view的宽度大于父容器给的宽度，就换行
            if ((lineWidth + realChildWidth) > sizeWidth) {
                //换行
                resultWidth = Math.max(lineWidth, realChildWidth);
                resultHeight += realChildHeight;
                //换行了，lineWidth和lineHeight重新算
                lineWidth = realChildWidth;
                lineHeight = realChildHeight;
            } else {
                //不换行，直接相加
                lineWidth += realChildWidth;
                //每一行的高度取二者最大值
                lineHeight = Math.max(lineHeight, realChildHeight);
            }

            //遍历到最后一个的时候，肯定走的是不换行
            if (i == childCount - 1) {
                resultWidth = Math.max(lineWidth, resultWidth);
                resultHeight += lineHeight;
            }

        }

        if (resultHeight != 0){
            setMeasuredDimension(modeWidth == MeasureSpec.EXACTLY ? sizeWidth : resultWidth,
                    modeHeight == MeasureSpec.EXACTLY ? sizeHeight : resultHeight);
        }else {
            setMeasuredDimension(1, 1);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int flowWidth = getWidth();

        int childLeft = 0;
        int childTop = 0;

        //遍历子控件，记录每个子view的位置
        for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
            View childView = getChildAt(i);

            //跳过View.GONE的子View
            if (childView.getVisibility() == View.GONE) {
                continue;
            }

            //获取到测量的宽和高
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();

            //因为子View可能设置margin，这里要加上margin的距离
            MarginLayoutParams mlp = (MarginLayoutParams) childView.getLayoutParams();

            if (childLeft + mlp.leftMargin + childWidth + mlp.rightMargin > flowWidth) {
                //换行处理
                childTop += (mlp.topMargin + childHeight + mlp.bottomMargin);
                childLeft = 0;
            }
            //布局
            int left = childLeft + mlp.leftMargin;
            int top = childTop + mlp.topMargin;
            int right = childLeft + mlp.leftMargin + childWidth;
            int bottom = childTop + mlp.topMargin + childHeight;
            childView.layout(left, top, right, bottom);

            childLeft += (mlp.leftMargin + childWidth + mlp.rightMargin);
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    public void onClick(View v) {
        if (mFlowBeans != null){
            for (FlowBean flowBean : mFlowBeans){
                if (flowBean.textView == v){
                    flowBean.textView.setSelected(!flowBean.isSelected);
                    flowBean.isSelected = !flowBean.isSelected;
                }
            }
        }
    }

    public List<DegreeBean> getContent(){
        List<DegreeBean> flowBeans = new ArrayList<>();
        if (mFlowBeans != null){
            for (FlowBean flowBean : mFlowBeans){
                if (flowBean.isSelected){
                    flowBeans.add(new DegreeBean(flowBean.id, flowBean.tagName));
                }
            }
        }
        return flowBeans;
    }


    private TextView addChildView(String content){
        TextView textView = new TextView(getContext());
        textView.setOnClickListener(this);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = Utils.dp2px(getContext(),5);
        params.bottomMargin = Utils.dp2px(getContext(),5);
        params.leftMargin = Utils.dp2px(getContext(),5);
        params.rightMargin = Utils.dp2px(getContext(),5);
        textView.setLayoutParams(params);
        textView.setText(content);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(Utils.dp2px(getContext(),10), Utils.dp2px(getContext(),6), Utils.dp2px(getContext(),10), Utils.dp2px(getContext(),6));
        textView.setBackgroundResource(R.drawable.item_flow_text);
        ColorStateList colorStateList = getResources().getColorStateList(R.color.item_flow_text_color);
        textView.setTextColor(colorStateList);
        addView(textView);
        return textView;
    }

    private List<FlowBean> mFlowBeans = new ArrayList<>();
    public void addContent(List<FlowBean> flowBeans){
        if (flowBeans == null){
            return;
        }
        clearFlowBeans();
        mFlowBeans.addAll(flowBeans);
        for (FlowBean flowBean : flowBeans){
            flowBean.textView = addChildView(flowBean.tagName);
        }

        requestLayout();
    }

    private void clearFlowBeans(){
        if (mFlowBeans != null){
            for (FlowBean flowBean : mFlowBeans){
                flowBean.clear();
            }
            mFlowBeans.clear();
        }
        removeAllViews();
    }
}
