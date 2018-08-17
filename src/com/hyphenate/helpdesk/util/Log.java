package com.hyphenate.helpdesk.util;

import android.text.TextUtils;

import com.hyphenate.util.EMLog;

public class Log {

    public static boolean isSystem = true;

    public static void d(String tag, String msg) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
            return;
        }
        EMLog.d(tag, msg);
        if (isSystem) {
            android.util.Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
            return;
        }
        EMLog.e(tag, msg);
        if (isSystem) {
            android.util.Log.e(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
            return;
        }
        EMLog.e(tag, msg);
        if (isSystem) {
            android.util.Log.i(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
            return;
        }
        EMLog.v(tag, msg);
        if (isSystem) {
            android.util.Log.v(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
            return;
        }
        EMLog.w(tag, msg);
        if (isSystem) {
            android.util.Log.w(tag, msg);
        }
    }

}
