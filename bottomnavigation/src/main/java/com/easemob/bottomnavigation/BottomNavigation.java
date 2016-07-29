package com.easemob.bottomnavigation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

public class BottomNavigation extends RelativeLayout {
    protected OnBottomNavigationSelectedListener mSelectionListener;
    private RadioButton mRadioButton1;
    private RadioButton mRadioButton2;
    private RadioButton mRadioButton3;


    public BottomNavigation(Context context) {
        super(context);
        initView(context);
    }

    public BottomNavigation(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public BottomNavigation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        View view = View.inflate(context, R.layout.bottom_navigation, this);
        mRadioButton1 = (RadioButton) view.findViewById(R.id.rb1);
        mRadioButton2 = (RadioButton) view.findViewById(R.id.rb2);
        mRadioButton3 = (RadioButton) view.findViewById(R.id.rb3);
        mRadioButton1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectionListener.onValueSelected(0);
            }
        });
        mRadioButton2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectionListener.onValueSelected(1);
            }
        });
        mRadioButton3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectionListener.onValueSelected(2);
            }
        });
    }
    public void setBottomNavigationSelectedListener(OnBottomNavigationSelectedListener l) {
        this.mSelectionListener = l;
    }

    public void setBottomNavigationClick(int index) {
        switch (index) {
            case 0:
                mRadioButton1.setChecked(true);
                break;
            case 1:
                mRadioButton2.setChecked(true);
                break;
            case 2:
                mRadioButton3.setChecked(true);
                break;
        }
    }

}
