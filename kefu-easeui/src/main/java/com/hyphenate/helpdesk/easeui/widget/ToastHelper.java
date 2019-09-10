package com.hyphenate.helpdesk.easeui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

public class ToastHelper {

    private static Toast toast;

    @SuppressLint("ShowToast")
    public static void show(Context context, CharSequence text)
    {
        if (context == null) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
        }
        toast.show();
    }

    @SuppressLint("ShowToast")
    public static void show(Context context, int res)
    {
        if (context == null) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), context.getString(res), Toast.LENGTH_SHORT);
        } else {
            toast.setText(context.getString(res));
        }
        toast.show();
    }

}
