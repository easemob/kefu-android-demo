/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.easemob.helpdeskdemo.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Toast;

import com.easemob.helpdeskdemo.R;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.easeui.ui.BaseActivity;
import com.hyphenate.helpdesk.model.MessageHelper;

public class SatisfactionActivity extends BaseActivity {

    private String msgId = "";
    private RatingBar ratingBar = null;
    private Button btnSubmit = null;
    private EditText etContent = null;
    private ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.em_activity_satisfaction);
        msgId = getIntent().getStringExtra("msgId");
        initView();

    }

    private void initView() {
        ratingBar = (RatingBar) findViewById(R.id.ratingBar1);
        btnSubmit = (Button) findViewById(R.id.submit);
        etContent = (EditText) findViewById(R.id.edittext);
        btnSubmit.setOnClickListener(new MyClickListener());
        ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating < 1.0f) {
                    ratingBar.setRating(1.0f);
                }
            }
        });
    }

    class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            pd = new ProgressDialog(SatisfactionActivity.this);
            pd.setMessage(getResources().getString(R.string.em_tip_wating));
            pd.show();
            MessageHelper.sendEvalMessage(msgId, String.valueOf(ratingBar.getSecondaryProgress()), etContent.getText().toString(), new Callback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (pd != null && pd.isShowing()) {
                                pd.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), R.string.comment_suc, Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                }

                @Override
                public void onError(int code, String error) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (pd != null && pd.isShowing()) {
                                pd.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), R.string.em_tip_request_fail, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onProgress(int progress, String status) {

                }
            });
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        // 添加点击Edittext 以外的区域，收起键盘
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputManager != null) {
                    inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }

            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            // 获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    public void back(View view) {
        finish();
    }
}
