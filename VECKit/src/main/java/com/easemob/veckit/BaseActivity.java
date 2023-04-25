package com.easemob.veckit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Outline;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.easemob.veckit.utils.AppStateVecCallback;
import com.hyphenate.helpdesk.util.Log;


public abstract class BaseActivity extends Activity {
    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            handleMessage(msg);
        }
    };

    public <T extends View> T $(@IdRes int id){
        return findViewById(id);
    }

    public abstract void initView(@NonNull Intent intent, @Nullable Bundle savedInstanceState);
    public abstract @LayoutRes int getLayoutResId();

    public void postDelayed(Runnable r, long delayMillis){
        if (mHandler != null){
            mHandler.postDelayed(r, delayMillis);
        }
    }

    public void removeRunnable(Runnable r){
        if (mHandler != null){
            mHandler.removeCallbacks(r);
        }
    }

    public abstract void handleMessage(Message msg);

    public void sendEmptyMessage(int what){
        if (mHandler != null){
            mHandler.sendEmptyMessage(what);
        }
    }

    public void sendMessage(Message msg){
        if (mHandler != null){
            mHandler.sendMessage(msg);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isLoadLayoutRes(getIntent())){
            setContentView(getLayoutResId());
        }
        initView(getIntent(), savedInstanceState);
    }

    public boolean isLoadLayoutRes(Intent intent){
        return true;
    }

    public void showAndHidden(View view, boolean isShow){
        if (view == null){
            return;
        }

        if (isShow && view.getVisibility() != View.VISIBLE){
            view.setVisibility(View.VISIBLE);
        }

        if (!isShow && view.getVisibility() == View.VISIBLE){
            view.setVisibility(View.GONE);
        }
    }

    public void showAndHiddenInvisible(View view, boolean isShow){
        if (view == null){
            return;
        }

        if (isShow && view.getVisibility() != View.VISIBLE){
            view.setVisibility(View.VISIBLE);
        }

        if (!isShow && view.getVisibility() == View.VISIBLE){
            view.setVisibility(View.INVISIBLE);
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void clip(View view, int radius){
        final int r = radius <= 0 ? dp2px(10) : dp2px(radius);
        view.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                Rect rect = new Rect();
                view.getGlobalVisibleRect(rect);
                int leftMargin = 0;
                int topMargin = 0;
                Rect selfRect = new Rect(leftMargin, topMargin,
                        rect.right - rect.left - leftMargin,
                        rect.bottom - rect.top - topMargin);

                outline.setRoundRect(selfRect, r);
            }
        });
        view.setClipToOutline(true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    public void removeHandlerAll(){
        if (mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }
}
