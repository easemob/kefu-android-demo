package com.easemob.veckit.utils;

import android.os.SystemClock;
import android.view.View;

import com.easemob.veckit.R;


/**
 * created by JakeYang
 * blog url: https://blog.csdn.net/JakeYangChina
 * on time: 2021/3/25 19:11
 * <p>
 * file description:
 */
public class ViewOnClickUtils {
    public static void onClick(View view, final OnClickListener listener){
        if (view == null || listener == null)
            return;

        Object tag = view.getTag(R.id.key_click_position);
        if (tag == null){
            view.setOnClickListener(null);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object tag = v.getTag(R.id.key_click_position);
                    if (tag != null){
                        Long time = (Long) tag;
                        Long l = SystemClock.uptimeMillis();
                        if (l - time > 1000){
                            v.setTag(R.id.key_click_position, SystemClock.uptimeMillis());
                            listener.onClick(v);
                        }
                    }else {
                        Object run = v.getTag(R.id.key_clicked);
                        if (run == null){
                            v.setTag(R.id.key_clicked, true);
                            v.setTag(R.id.key_click_position, SystemClock.uptimeMillis());
                            listener.onClick(v);
                        }
                    }
                }
            });
        }
    }

    public static void onClickDestroy(View view){
        if (view != null){
            view.setTag(R.id.key_click_position, null);
            view.setTag(R.id.key_clicked, null);
            view.setOnClickListener(null);
        }
    }


    public interface OnClickListener{
        void onClick(View v);
    }
}
