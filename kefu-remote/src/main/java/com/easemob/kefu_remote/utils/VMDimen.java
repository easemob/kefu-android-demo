package com.easemob.kefu_remote.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import com.easemob.kefu_remote.RemoteApp;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2015/4/15.
 * 尺寸转化工具类
 */
public class VMDimen {

    private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";
    private static final String NAV_BAR_HEIGHT_RES_NAME = "navigation_bar_height";
    private static final String NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME = "navigation_bar_height_landscape";
    private static final String NAV_BAR_WIDTH_RES_NAME = "navigation_bar_width";

    public VMDimen() {

    }

    public static Point getScreenSize() {
        WindowManager wm = (WindowManager) new RemoteApp().getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point outSize = new Point();
        display.getSize(outSize);
        return outSize;
    }

    public static Point getImageSize(String str) {
        String wh = str.substring(str.indexOf(".") + 1, str.lastIndexOf("."));
        String w = wh.substring(0, wh.indexOf("."));
        String h = wh.substring(wh.indexOf(".") + 1);
        Point outSize = new Point(Integer.valueOf(w), Integer.valueOf(h));
        return outSize;
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight() {
        Resources res = RemoteApp.getInstance().getContext().getResources();
        int height = res.getIdentifier("status_bar_height", "dimen", "android");
        height = res.getDimensionPixelSize(height);
        VMLog.i("statusBar.h." + height);
        return height;
    }

    /**
     * 获取NavigationBar的高度（在NavigationBar 存在的情况下）
     */
    public static int getNavigationBarHeight() {
        Resources res =RemoteApp.getInstance().getContext().getResources();
        int height = 0;
        if (hasNavigationBar()) {
            String key = NAV_BAR_HEIGHT_RES_NAME;
            height = getInternalDimensionSize(res, key);
        }
        //        VMLog.i("navigationbar.h." + height);
        return height;
    }

    public static int getSystemBarHeight() {
        Resources res = RemoteApp.getInstance().getContext().getResources();
        int height = res.getIdentifier("system_bar_height", "dimen", "android");
        height = res.getDimensionPixelSize(height);
        VMLog.i("systembar.h." + height);

        return height;
    }

    /**
     * 获取ToolBar高度
     */
    public static int getToolbarHeight() {
        //        int toolbarHeight = activity.getActionBar().getHeight();
        int height = 0;
        if (height != 0) {
            return height;
        }
        TypedValue tv = new TypedValue();
        if (RemoteApp.getInstance().getContext()
                .getTheme()
                .resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            height = TypedValue.complexToDimensionPixelSize(tv.data, new RemoteApp().getContext()
                    .getResources()
                    .getDisplayMetrics());
        }
        VMLog.i("toolbar.h." + height);
        return height;
    }

    private static int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 判断是否有虚拟导航栏NavigationBar，
     */
    private static boolean hasNavigationBar() {
        boolean hasNavigationBar = false;
        Resources rs = RemoteApp.getInstance().getContext().getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            VMLog.e(e.getMessage());
        }
        return hasNavigationBar;
    }

    /**
     * 将控件尺寸的资源转换为像素尺寸
     *
     * @param resId 尺寸资源id
     */
    public static int getDimenPixel(int resId) {
        Resources res = RemoteApp.getInstance().getContext().getResources();
        int result = res.getDimensionPixelSize(resId);
        return result;
    }

    /**
     * 将控件尺寸大小转为当前设备下的像素大小
     *
     * @param dp 控件尺寸大小
     */
    public static int dp2px(int dp) {
        Resources res = RemoteApp.getInstance().getContext().getResources();
        float density = res.getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    /**
     * 将字体尺寸大小转为当前设备下的像素尺寸大小
     *
     * @param sp 字体的尺寸大小
     */
    public static float sp2px(int sp) {
        Resources res = RemoteApp.getInstance().getContext().getResources();
        float density = res.getDisplayMetrics().scaledDensity;
        return (int) (sp * density + 0.5f);
    }

    /**
     * 获取文字的宽度
     *
     * @param paint 绘制文字的画笔
     * @param str 需要计算宽度的字符串
     * @return 返回字符串宽度
     */
    public static float getTextWidth(Paint paint, String str) {
        float textWidth = 0;
        if (str != null && str.length() > 0) {
            // 记录字符串中每个字符宽度的数组
            float[] widths = new float[str.length()];
            // 获取字符串中每个字符的宽度到数组
            paint.getTextWidths(str, widths);
            for (int i = 0; i < str.length(); i++) {
                textWidth += (float) Math.ceil(widths[i]);
            }
        }
        return textWidth;
    }

    /**
     * 计算文字的高度
     *
     * @param paint 绘制文字的画笔
     * @return 返回字符串高度
     */
    public static float getTextHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (float) Math.ceil(fm.descent - fm.ascent);
    }
}
