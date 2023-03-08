package com.hyphenate.helpdesk.videokit.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Chronometer;

public class MyChronometer extends Chronometer {
    public MyChronometer(Context context) {
        super(context);
    }

    public MyChronometer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyChronometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        // continue when view is hidden
        visibility = View.VISIBLE;
        super.onWindowVisibilityChanged(visibility);
    }
}
