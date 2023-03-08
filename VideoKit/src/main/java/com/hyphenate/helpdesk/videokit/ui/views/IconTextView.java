package com.hyphenate.helpdesk.videokit.ui.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class IconTextView extends AppCompatTextView {
    public IconTextView(Context context) {
        this(context, null);
    }

    public IconTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/iconfont.ttf");
        if (font != null){
            setTypeface(font);
        }
    }
}
