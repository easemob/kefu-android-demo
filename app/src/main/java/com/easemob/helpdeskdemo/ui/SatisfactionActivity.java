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
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.ui.adapter.TagAdapter;
import com.easemob.helpdeskdemo.widget.flow.FlowTagLayout;
import com.easemob.helpdeskdemo.widget.flow.OnTagSelectListener;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.easeui.ui.BaseActivity;
import com.hyphenate.helpdesk.easeui.widget.ToastHelper;
import com.hyphenate.helpdesk.model.EvaluationInfo;
import com.hyphenate.helpdesk.model.MessageHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SatisfactionActivity extends BaseActivity {

    private EditText etContent = null;
    private TextView tvLevelName = null;
    private ProgressDialog pd = null;
    private FlowTagLayout mFlowTagLayout;
    private TagAdapter<EvaluationInfo.TagInfo> tagAdapter;
    private EvaluationInfo evaluationInfo;
    private volatile EvaluationInfo.Degree currentDegree;
    private List<EvaluationInfo.TagInfo> selectedTags = Collections.synchronizedList(new ArrayList<EvaluationInfo.TagInfo>());
    private Message currentMessage;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.em_activity_satisfaction);
        String msgId = getIntent().getStringExtra("msgId");
        initView();
        currentMessage = ChatClient.getInstance().chatManager().getMessage(msgId);
        evaluationInfo = MessageHelper.getEvalRequest(currentMessage);
        currentDegree = evaluationInfo.getDegree(5);
        refreshLevelName();
        refreshFlowLayout();


    }

    private void refreshLevelName(){
        if (currentDegree != null && !TextUtils.isEmpty(currentDegree.getName())){
            tvLevelName.setText(currentDegree.getName());
        }else{
            tvLevelName.setText("");
        }

    }

    private void refreshFlowLayout(){
        if (currentDegree != null && currentDegree.getAppraiseTag() != null && !currentDegree.getAppraiseTag().isEmpty()){
            mFlowTagLayout.setVisibility(View.VISIBLE);
            selectedTags.clear();
            tagAdapter.clearAndAndAll(currentDegree.getAppraiseTag());

        }else{
            mFlowTagLayout.setVisibility(View.INVISIBLE);
        }

    }


    private void initView() {
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar1);
        Button btnSubmit = (Button) findViewById(R.id.submit);
        etContent = (EditText) findViewById(R.id.edittext);
        tvLevelName = (TextView) findViewById(R.id.tv_level_name);
        mFlowTagLayout = (FlowTagLayout) findViewById(R.id.id_flowlayout);
        mFlowTagLayout.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_MULTI);
        mFlowTagLayout.setAdapter(tagAdapter = new TagAdapter<>(this));
        btnSubmit.setOnClickListener(new MyClickListener());
        ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating < 1.0f) {
                    ratingBar.setRating(1.0f);
                }
                currentDegree = evaluationInfo.getDegree((int) rating);
                refreshLevelName();
                refreshFlowLayout();
            }
        });
        mFlowTagLayout.setOnTagSelectListener(new OnTagSelectListener() {
            @Override
            public void onItemSelect(FlowTagLayout parent, List<Integer> selectedList) {
                selectedTags.clear();
                if (selectedList != null && !selectedList.isEmpty()){
                    for (int i : selectedList){
                        selectedTags.add((EvaluationInfo.TagInfo) parent.getAdapter().getItem(i));
                    }
                }
            }
        });

    }

    class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (currentDegree != null && currentDegree.getAppraiseTag() != null && !currentDegree.getAppraiseTag().isEmpty()){
                if (selectedTags == null || selectedTags.isEmpty()){
                    ToastHelper.show(getBaseContext(), R.string.no_selected_tag_noti);
                    return;
                }
            }

            pd = new ProgressDialog(SatisfactionActivity.this);
            pd.setMessage(getResources().getString(R.string.em_tip_wating));
            pd.show();
            MessageHelper.sendEvalMessage(currentMessage, etContent.getText().toString(), currentDegree, selectedTags, new Callback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (pd != null && pd.isShowing()) {
                                pd.dismiss();
                            }
                            ToastHelper.show(getBaseContext(), R.string.comment_suc);
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
                            ToastHelper.show(getBaseContext(), R.string.em_tip_request_fail);
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
