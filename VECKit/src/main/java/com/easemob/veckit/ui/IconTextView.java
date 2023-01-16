package com.easemob.veckit.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class IconTextView extends AppCompatTextView {
    public IconTextView(Context context) {
        this(context, null);
        init(context);
    }

    public IconTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public IconTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        try {
            Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/iconfont.ttf");
            if (font != null){
                setTypeface(font);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
