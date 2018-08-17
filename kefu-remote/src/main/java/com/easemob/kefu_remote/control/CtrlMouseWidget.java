package com.easemob.kefu_remote.control;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.easemob.kefu_remote.R;

public class CtrlMouseWidget extends LinearLayout {

    protected Context context;
    // 用于更新指针位置
    protected WindowManager windowManager;
    // 指针参数
    protected WindowManager.LayoutParams params;

    protected ImageView mouseIconView;

    public CtrlMouseWidget(Context context) {
        this(context, null);
    }

    public CtrlMouseWidget(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CtrlMouseWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.widget_ctrl_cursor_float, this);

        mouseIconView = (ImageView) findViewById(R.id.img_mouse_icon);
    }

    /**
     * 设置属性
     */
    public void setParams(WindowManager.LayoutParams params) {
        this.params = params;
    }

    public void mouseDown() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mouseIconView.setImageResource(R.drawable.ic_cursor_default);
            mouseIconView.setColorFilter(ContextCompat.getColor(context, R.color.theme_accent));
        } else {
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_cursor_default);
            Drawable wrapDrawable = DrawableCompat.wrap(drawable).mutate();
            wrapDrawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            DrawableCompat.setTint(drawable, ContextCompat.getColor(context, R.color.theme_accent));
            mouseIconView.setImageDrawable(wrapDrawable);
        }
    }

    public void mouseUp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mouseIconView.setImageResource(R.drawable.ic_cursor_default);
            mouseIconView.setColorFilter(ContextCompat.getColor(context, R.color.theme_background));
        } else {
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_cursor_default);
            Drawable wrapDrawable = DrawableCompat.wrap(drawable).mutate();
            wrapDrawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            DrawableCompat.setTint(drawable, ContextCompat.getColor(context, R.color.theme_background));
            mouseIconView.setImageDrawable(wrapDrawable);
        }
    }

    /**
     * 更新位置
     */
    public void updatePosition(int x, int y) {
        params.x = x;
        params.y = y;
        windowManager.updateViewLayout(this, params);
    }
}


