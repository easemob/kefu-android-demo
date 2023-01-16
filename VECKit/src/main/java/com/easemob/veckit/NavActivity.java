package com.easemob.veckit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;


import androidx.annotation.Nullable;

import com.easemob.veckit.utils.CloudCallbackUtils;

public class NavActivity extends Activity {

    private View mContent;
    private WindowManager mWm;
    private Point mPoint;
    private boolean mIsBack;

    private final ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            mContent.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
            int navHeight = getNav(mWm, mContent, mPoint);
            CloudCallbackUtils.newCloudCallbackUtils().updateNav(navHeight, mIsBack);
            NavActivity.this.finish();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mIsBack = intent.getBooleanExtra("isBack", false);
        mContent = getWindow().getDecorView().findViewById(android.R.id.content);
        mWm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        mPoint = new Point();
        mContent.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
    }

    private int getNav(WindowManager wm, View content, Point point){
        Display display = wm.getDefaultDisplay();
        display.getRealSize(point);
        if (content.getBottom() == 0){
            return 0;
        }
        return point.y - content.getBottom();
    }
}
