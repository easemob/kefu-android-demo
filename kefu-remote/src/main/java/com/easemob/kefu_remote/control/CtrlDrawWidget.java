package com.easemob.kefu_remote.control;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.easemob.kefu_remote.R;

public class CtrlDrawWidget extends LinearLayout {

    protected Context context;

    public CtrlDrawWidget(Context context) {
        this(context, null);
    }

    public CtrlDrawWidget(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CtrlDrawWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.widget_ctrl_draw_float, this);
        findViewById(R.id.btn_close).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CtrlManager.getInstance(context).stopDrawMode();
            }
        });

    }
}