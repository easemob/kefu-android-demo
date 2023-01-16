package com.hyphenate.helpdesk.easeui.ui;


import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;

import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.easeui.widget.TitleBar;

public abstract class BaseFragment extends Fragment {

    protected TitleBar titleBar;
    protected InputMethodManager inputMethodManager;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getView() != null)
            titleBar = (TitleBar) getView().findViewById(R.id.title_bar);

        initView();
        setUpView();
    }

    /**
     * 显示标题栏
     */
    public void ShowTitleBar() {
        if (titleBar != null) {
            titleBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏标题栏
     */
    public void hideTitleBar() {
        if (titleBar != null) {
            titleBar.setVisibility(View.GONE);
        }
    }

    protected void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    /**
     * 初始化控件
     */
    protected abstract void initView();

    /**
     * 设置属性，监听等
     */
    protected abstract void setUpView();

}
