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
import android.widget.TextView;

import com.baidu.platform.comapi.map.E;
import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.ui.adapter.TagAdapter;
import com.easemob.helpdeskdemo.widget.RatingBar;
import com.easemob.helpdeskdemo.widget.flow.FlowTagLayout;
import com.easemob.helpdeskdemo.widget.flow.OnTagSelectListener;
import com.hyphenate.chat.AgoraMessage;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.callback.ValueCallBack;
import com.hyphenate.helpdesk.easeui.ui.BaseActivity;
import com.hyphenate.helpdesk.easeui.widget.ToastHelper;
import com.hyphenate.helpdesk.model.ContentFactory;
import com.hyphenate.helpdesk.model.EvaluationInfo;
import com.hyphenate.helpdesk.model.MessageHelper;
import com.hyphenate.helpdesk.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SatisfactionActivity extends BaseActivity implements View.OnClickListener {

    private EditText etContent = null;
    private TextView tvLevelName = null;
    private ProgressDialog pd = null;
    private FlowTagLayout mFlowTagLayout;
    private TagAdapter<EvaluationInfo.TagInfo> tagAdapter;
    private EvaluationInfo evaluationInfo;
    private volatile EvaluationInfo.Degree currentDegree;
    private List<EvaluationInfo.TagInfo> selectedTags = Collections.synchronizedList(new ArrayList<EvaluationInfo.TagInfo>());
    private Message currentMessage;
    private View mOneLlt;
    private View mTwoFlt;
    private View mPintJiaCLlt;
    private TextView mPingJiaTv;
    private List<EvaluationInfo.ResolutionParam> mResolutionParams;
    private volatile List<EvaluationInfo.ResolutionParam> mParamList;
    // private EvaluationInfo.ResolutionParam mParam;
    private String mSessionId;
    private TextView mPingJiaOneTv;
    private TextView mPingJiaTwoTv;
    private TextView mTv_description;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.em_activity_satisfaction);
        String msgId = getIntent().getStringExtra("msgId");
        initView();
        mResolutionParams = new ArrayList<>();
        mParamList = new ArrayList<>();

        currentMessage = ChatClient.getInstance().chatManager().getMessage(msgId);
        evaluationInfo = MessageHelper.getEvalRequest(currentMessage);

        mSessionId = evaluationInfo.getSessionId();

        currentDegree = evaluationInfo.getDegree(5);
        refreshLevelName();
        refreshFlowLayout();

        mTv_description = findViewById(R.id.tv_description);
        mPingJiaOneTv = findViewById(R.id.pingJiaOneTv);
        mPingJiaTwoTv = findViewById(R.id.pingJiaTwoTv);
        mPingJiaTv = findViewById(R.id.pingJiaTv);
        mPintJiaCLlt = findViewById(R.id.pintJiaCLlt);
        mOneLlt = findViewById(R.id.pingJiaOneLlt);
        mOneLlt.setOnClickListener(this);
        mOneLlt.setSelected(true);
        mTwoFlt = findViewById(R.id.pingJiaTwoLlt);
        mTwoFlt.setOnClickListener(this);

        showAndHidden(mPintJiaCLlt, false);


        // 是否显示评价
        ChatClient.getInstance().chatManager().asyncProblemSolvingOnServiceSessionResolved(ChatClient.getInstance().tenantId(), new ValueCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                try {
                    JSONObject jsonObject = new JSONObject(value);
                    if (jsonObject.has("entities")){
                        JSONObject entities = jsonObject.getJSONArray("entities").getJSONObject(0);
                        if (entities.has("optionName")){
                            String optionName = entities.getString("optionName");
                            if ("problemSolvingOnServiceSessionResolved".equalsIgnoreCase(optionName)){
                                String optionValue = entities.getString("optionValue");
                                String val = optionValue.replaceAll("(&quot;)+", "\"");
                                JSONObject v = new JSONObject(val);
                                if (v.has("app")){
                                    initPingJia(v.getBoolean("app"));
                                }else {
                                    initPingJia(false);
                                }
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    asyncEvalSolveWord();
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                asyncEvalSolveWord();
            }
        });

    }

    private void initPingJia(final boolean isShow){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 显示评价
                showAndHidden(mPintJiaCLlt, isShow);
            }
        });
    }

    // 请对我的服务做出评价
    private void asyncGreetingMsgEnquiryInvite(){
        ChatClient.getInstance().chatManager().asyncGreetingMsgEnquiryInvite(ChatClient.getInstance().tenantId(), mSessionId, new ValueCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                try {
                    JSONObject jsonObject = new JSONObject(value);
                    JSONArray entities = jsonObject.getJSONArray("entities");
                    JSONObject object = entities.getJSONObject(0);
                    String optionValue = object.getString("optionValue");
                    updateMsgEnquiryInvite(optionValue);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    closePd();
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                closePd();
            }
        });
    }

    private void updateMsgEnquiryInvite(String optionValue) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTv_description.setText(optionValue);
            }
        });
    }

    private void closePd() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (pd != null && pd.isShowing()){
                    pd.dismiss();
                }
            }
        });
    }

    // 获取 请问客服是否解决了您的问题？
    private void asyncEvalSolveWord(){
        ChatClient.getInstance().chatManager().asyncEvalSolveWord(ChatClient.getInstance().tenantId(), mSessionId, new ValueCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                try {
                    JSONObject jsonObject = new JSONObject(value);
                    if (jsonObject.has("entities")){
                        JSONObject entities = jsonObject.getJSONArray("entities").getJSONObject(0);
                        if (entities.has("optionName")){
                            String optionName = entities.getString("optionName");
                            if ("evaluteSolveWord".equalsIgnoreCase(optionName)){
                                String optionValue = entities.getString("optionValue");
                                String val = optionValue.replaceAll("(&quot;)+", "\"");
                                setPingJiaText(val);
                            }
                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    asyncResolutionParams();
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                asyncResolutionParams();
            }
        });
    }

    // 获取评价按钮：例如：已解决，未解决
    private void asyncResolutionParams(){
        ChatClient.getInstance().chatManager().asyncResolutionParams(ChatClient.getInstance().tenantId(), mSessionId, new ValueCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                try {
                    JSONObject jsonObject = new JSONObject(value);
                    JSONArray entities = jsonObject.getJSONArray("entities");
                    for (int i = 0; i < entities.length(); i++){
                        JSONObject obj = entities.getJSONObject(i);
                        EvaluationInfo.ResolutionParam param = new EvaluationInfo.ResolutionParam();
                        param.setId(obj.getString("id"));
                        param.setName(obj.getString("name"));
                        param.setScore(obj.getString("score"));
                        if (obj.has("resolutionParamTags")){
                            param.setResolutionParamTags(obj.getJSONArray("resolutionParamTags"));
                        }
                        mParamList.add(param);
                    }
                    updateResolutionParams();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    asyncGreetingMsgEnquiryInvite();
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                asyncGreetingMsgEnquiryInvite();
            }
        });
    }

    private void updateResolutionParams() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mParamList.size() < 2){
                    return;
                }
                mPingJiaOneTv.setText(mParamList.get(0).getName());
                mPingJiaTwoTv.setText(mParamList.get(1).getName());
                mResolutionParams.add(mParamList.get(0));
            }
        });
    }

    private void setPingJiaText(String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPingJiaTv.setText(value);
            }
        });

    }


    private ProgressDialog getProgressDialog() {
        if (pd == null){
            pd = new ProgressDialog(SatisfactionActivity.this);
            pd.setCanceledOnTouchOutside(false);
        }
        pd.setMessage(getResources().getString(R.string.em_tip_wating));
        return pd;
    }

    private void showAndHidden(View view, boolean isShow){
        if (view == null){
            return;
        }
        if (isShow){
            if (view.getVisibility() != View.VISIBLE){
                view.setVisibility(View.VISIBLE);
            }
        }else {
            if (view.getVisibility() == View.VISIBLE){
                view.setVisibility(View.GONE);
            }
        }
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
        ratingBar.setStar(5);
        ratingBar.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(float ratingCount) {
                if (ratingCount < 1.0f) {
                    //ratingBar.setRating(0);
                    ratingBar.setStar(0);
                }
                currentDegree = evaluationInfo.getDegree((int) ratingCount);
                refreshLevelName();
                refreshFlowLayout();
            }
        });
        /*ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating < 1.0f) {
                    ratingBar.setRating(1.0f);
                }
                currentDegree = evaluationInfo.getDegree((int) rating);
                refreshLevelName();
                refreshFlowLayout();
            }
        });*/
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.pingJiaOneLlt){
            if (mParamList.size() == 0){
                return;
            }
            mOneLlt.setSelected(true);
            mTwoFlt.setSelected(false);
            mResolutionParams.clear();
            mResolutionParams.add(mParamList.get(0));

        }else if (id == R.id.pingJiaTwoLlt){
            if (mParamList.size() < 1){
                return;
            }
            mTwoFlt.setSelected(true);
            mOneLlt.setSelected(false);
            mResolutionParams.clear();
            mResolutionParams.add(mParamList.get(1));
        }


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

            pd = getProgressDialog();
            pd.setMessage(getResources().getString(R.string.em_tip_wating));
            if (!pd.isShowing()){
                pd.show();
            }


            // TODO 此处为测试，临时传visitor，实际值根据自己业务场景传值
            String evaluateWay = "visitor";
            try {
                JSONObject jsonObject = MessageHelper.getContainerObject(currentMessage, EvaluationInfo.PARENT_NAME);
                if (jsonObject != null) {
                    JSONObject content = getJSONObject(jsonObject, EvaluationInfo.ARGS);
                    if (content != null) {
                        // Log.e("ppppppppppp","aa content = "+content);
                        if (content.has("inviteId")){
                            int inviteId = content.getInt("inviteId");
                            evaluateWay = inviteId == 0 ? evaluateWay : "";
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }


            // evaluateWay 评价方式：由前端传入，visitor访客主动评价，agent坐席邀请，system强制邀请访客点击关闭窗口或会话结束
            MessageHelper.sendEvalMessage(currentMessage, evaluateWay, etContent.getText().toString(), currentDegree, selectedTags, mResolutionParams, new Callback() {
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

    private static JSONObject getJSONObject(JSONObject jsonObject, String name) {
        if (jsonObject.has(name) && !jsonObject.isNull(name)) {
            try {
                return jsonObject.getJSONObject(name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void back(View view) {
        finish();
    }
}
