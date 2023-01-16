package com.easemob.veckit.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.easemob.veckit.utils.Utils;

public class StatusView extends View {
    public StatusView(Context context) {
        super(context);
    }

    public StatusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StatusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = Utils.getStateHeight(getContext());
        super.onMeasure(MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        setMeasuredDimension(width, height);
    }
}
