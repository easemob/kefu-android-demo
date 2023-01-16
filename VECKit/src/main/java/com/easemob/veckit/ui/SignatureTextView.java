package com.easemob.veckit.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.easemob.veckit.R;

public class SignatureTextView extends AppCompatTextView {
    public SignatureTextView(@NonNull Context context) {
        super(context);
        init();
    }

    public SignatureTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SignatureTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        initHint();
    }

    public void initHint(){
        setText(getResources().getString(R.string.vec_handwritten_signature));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            String s = getText().toString();
            if (!TextUtils.isEmpty(s)){
                setText("");
            }
        }
        return super.onTouchEvent(event);
    }
}
