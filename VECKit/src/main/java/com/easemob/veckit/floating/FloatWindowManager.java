/*
 * Copyright (C) 2016 Facishare Technology Co., Ltd. All Rights Reserved.
 */
package com.easemob.veckit.floating;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;


import com.easemob.veckit.floating.rom.HuaweiUtils;
import com.easemob.veckit.floating.rom.MeizuUtils;
import com.easemob.veckit.floating.rom.MiuiUtils;
import com.easemob.veckit.floating.rom.OppoUtils;
import com.easemob.veckit.floating.rom.QikuUtils;
import com.easemob.veckit.floating.rom.RomUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class FloatWindowManager {
    private static final String TAG = "FloatWindowManager";

    private static volatile FloatWindowManager instance;

    private boolean isWindowDismiss = true;
    private WindowManager windowManager = null;
    private WindowManager.LayoutParams mParams = null;
    private AVCallFloatView floatView = null;
    private Dialog dialog;

    public static FloatWindowManager getInstance() {
        if (instance == null) {
            synchronized (FloatWindowManager.class) {
                if (instance == null) {
                    instance = new FloatWindowManager();
                }
            }
        }
        return instance;
    }

    public void applyPermission(Context context, String message, String okContent, String cancelContent, OnConfirmResult result) {
        if (Build.VERSION.SDK_INT < 23) {
            if (RomUtils.checkIsMiuiRom()) {
                miuiROMPermissionApply(context, message, okContent, cancelContent, result);
            } else if (RomUtils.checkIsMeizuRom()) {
                meizuROMPermissionApply(context, message, okContent, cancelContent, result);
            } else if (RomUtils.checkIsHuaweiRom()) {
                huaweiROMPermissionApply(context, message, okContent, cancelContent, result);
            } else if (RomUtils.checkIs360Rom()) {
                ROM360PermissionApply(context, message, okContent, cancelContent, result);
            } else if (RomUtils.checkIsOppoRom()) {
                oppoROMPermissionApply(context, message, okContent, cancelContent, result);
            }
        } else {
            commonROMPermissionApply(context, message, okContent, cancelContent, result);
        }
    }

    public void applyPermission(Context context, OnConfirmResult result) {
        applyPermission(context, null, null, null, result);
    }

    public void applyPermission(Context context, String message, String okContent, String cancelContent) {
        applyPermission(context, message, okContent, cancelContent, null);
    }

    public void applyPermission(Context context) {
        applyPermission(context, null, null, null, null);
    }

    public boolean checkPermission(Context context) {
        //6.0 版本之后由于 google 增加了对悬浮窗权限的管理，所以方式就统一了
        if (Build.VERSION.SDK_INT < 23) {
            if (RomUtils.checkIsMiuiRom()) {
                return miuiPermissionCheck(context);
            } else if (RomUtils.checkIsMeizuRom()) {
                return meizuPermissionCheck(context);
            } else if (RomUtils.checkIsHuaweiRom()) {
                return huaweiPermissionCheck(context);
            } else if (RomUtils.checkIs360Rom()) {
                return qikuPermissionCheck(context);
            } else if (RomUtils.checkIsOppoRom()) {
                return oppoROMPermissionCheck(context);
            }
        }
        return commonROMPermissionCheck(context);
    }

    private boolean huaweiPermissionCheck(Context context) {
        return HuaweiUtils.checkFloatWindowPermission(context);
    }

    private boolean miuiPermissionCheck(Context context) {
        return MiuiUtils.checkFloatWindowPermission(context);
    }

    private boolean meizuPermissionCheck(Context context) {
        return MeizuUtils.checkFloatWindowPermission(context);
    }

    private boolean qikuPermissionCheck(Context context) {
        return QikuUtils.checkFloatWindowPermission(context);
    }

    private boolean oppoROMPermissionCheck(Context context) {
        return OppoUtils.checkFloatWindowPermission(context);
    }

    private boolean commonROMPermissionCheck(Context context) {
        //最新发现魅族6.0的系统这种方式不好用，天杀的，只有你是奇葩，没办法，单独适配一下
        if (RomUtils.checkIsMeizuRom()) {
            return meizuPermissionCheck(context);
        } else {
            Boolean result = true;
            if (Build.VERSION.SDK_INT >= 23) {
                try {
                    Class clazz = Settings.class;
                    Method canDrawOverlays = clazz.getDeclaredMethod("canDrawOverlays", Context.class);
                    result = (Boolean) canDrawOverlays.invoke(null, context);
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
            return result;
        }
    }

    private void ROM360PermissionApply(final Context context, String message, String okContent, String cancelContent, OnConfirmResult result) {
        showConfirmDialog(context, message, okContent, cancelContent, new OnConfirmResult() {
            @Override
            public void confirmResult(boolean confirm) {
                if (result != null){
                    result.confirmResult(confirm);
                }
                if (confirm) {
                    QikuUtils.applyPermission(context);
                } else {
                    Log.e(TAG, "ROM:360, user manually refuse OVERLAY_PERMISSION");
                }
            }
        });
    }

    private void huaweiROMPermissionApply(final Context context, String message, String okContent, String cancelContent, OnConfirmResult result) {
        showConfirmDialog(context,message, okContent, cancelContent, new OnConfirmResult() {
            @Override
            public void confirmResult(boolean confirm) {
                if (result != null){
                    result.confirmResult(confirm);
                }
                if (confirm) {
                    HuaweiUtils.applyPermission(context);
                } else {
                    Log.e(TAG, "ROM:huawei, user manually refuse OVERLAY_PERMISSION");
                }
            }
        });
    }

    private void meizuROMPermissionApply(final Context context, String message, String okContent, String cancelContent, OnConfirmResult result) {
        showConfirmDialog(context, message, okContent, cancelContent, new OnConfirmResult() {
            @Override
            public void confirmResult(boolean confirm) {
                if (result != null){
                    result.confirmResult(confirm);
                }
                if (confirm) {
                    MeizuUtils.applyPermission(context);
                } else {
                    Log.e(TAG, "ROM:meizu, user manually refuse OVERLAY_PERMISSION");
                }
            }
        });
    }

    private void miuiROMPermissionApply(final Context context, String message, String okContent, String cancelContent, OnConfirmResult result) {
        showConfirmDialog(context, message, okContent, cancelContent, new OnConfirmResult() {
            @Override
            public void confirmResult(boolean confirm) {
                if (result != null){
                    result.confirmResult(confirm);
                }
                if (confirm) {
                    MiuiUtils.applyMiuiPermission(context);
                } else {
                    Log.e(TAG, "ROM:miui, user manually refuse OVERLAY_PERMISSION");
                }
            }
        });
    }

    private void oppoROMPermissionApply(final Context context, String message, String okContent, String cancelContent, OnConfirmResult result) {
        showConfirmDialog(context,message, okContent, cancelContent,  new OnConfirmResult() {
            @Override
            public void confirmResult(boolean confirm) {
                if (result != null){
                    result.confirmResult(confirm);
                }
                if (confirm) {
                    OppoUtils.applyOppoPermission(context);
                } else {
                    Log.e(TAG, "ROM:miui, user manually refuse OVERLAY_PERMISSION");
                }
            }
        });
    }

    /**
     * 通用 rom 权限申请
     */
    private void commonROMPermissionApply(final Context context, String message, String okContent, String cancelContent, OnConfirmResult result) {
        //这里也一样，魅族系统需要单独适配
        if (RomUtils.checkIsMeizuRom()) {
            meizuROMPermissionApply(context, message, okContent, cancelContent, result);
        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                showConfirmDialog(context, message, okContent, cancelContent, new OnConfirmResult() {
                    @Override
                    public void confirmResult(boolean confirm) {
                        if (result != null){
                            result.confirmResult(confirm);
                        }
                        if (confirm) {
                            try {
                                commonROMPermissionApplyInternal(context);
                            } catch (Exception e) {
                                Log.e(TAG, Log.getStackTraceString(e));
                            }
                        } else {
                            Log.d(TAG, "user manually refuse OVERLAY_PERMISSION");
                            //需要做统计效果
                        }
                    }
                });
            }
        }
    }

    public static void commonROMPermissionApplyInternal(Context context) throws NoSuchFieldException, IllegalAccessException {
        Class clazz = Settings.class;
        Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");

        Intent intent = new Intent(field.get(null).toString());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

    /*private void showConfirmDialog(Context context, OnConfirmResult result) {
        showConfirmDialog(context, "您的手机没有授予悬浮窗权限，请开启后再试", null, null,result);
    }*/

    private void showConfirmDialog(Context context, String message, String okContent, String cancelContent, final OnConfirmResult result) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        message = TextUtils.isEmpty(message) ? "您的手机没有授予悬浮窗权限，请开启后再试" : message;
        okContent = TextUtils.isEmpty(okContent) ? "现在去开启" : okContent;
        cancelContent = TextUtils.isEmpty(cancelContent) ? "暂不开启" : cancelContent;
        dialog = new AlertDialog.Builder(context).setCancelable(true).setTitle("")
                .setMessage(message)
                .setPositiveButton(okContent,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirmResult(true);
                                dialog.dismiss();
                            }
                        }).setNegativeButton(cancelContent,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirmResult(false);
                                dialog.dismiss();
                            }
                        }).create();

        dialog.show();
    }

    public void onDestroy(){
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = null;
    }

    public interface OnConfirmResult {
        void confirmResult(boolean confirm);
    }

    private void showWindow(Context context) {
        if (!isWindowDismiss) {
            Log.e(TAG, "view is already added here");
            return;
        }

        isWindowDismiss = false;
        if (windowManager == null) {
            windowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        }

        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        mParams = new WindowManager.LayoutParams();
        mParams.packageName = context.getPackageName();
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        int mType;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mType = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        }
        mParams.type = mType;
        mParams.format = PixelFormat.RGBA_8888;
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        mParams.x = screenWidth - dp2px(context, 100);
        mParams.y = screenHeight - dp2px(context, 171);


//        ImageView imageView = new ImageView(mContext);
//        imageView.setImageResource(R.drawable.app_icon);
        floatView = new AVCallFloatView(context);
        // floatView.setParams(mParams);
        floatView.setIsShowing(true);
        windowManager.addView(floatView, mParams);
    }

    public void dismissWindow() {
        if (isWindowDismiss) {
            Log.e(TAG, "window can not be dismiss cause it has not been added");
            return;
        }

        isWindowDismiss = true;
        floatView.setIsShowing(false);
        if (windowManager != null && floatView != null) {
            windowManager.removeViewImmediate(floatView);
        }
    }

    private int dp2px(Context context, float dp){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
