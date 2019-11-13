package com.easemob.helpdeskdemo.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.ui.adapter.TagAdapter;
import com.easemob.helpdeskdemo.widget.flow.FlowTagLayout;
import com.easemob.helpdeskdemo.widget.flow.OnTagSelectListener;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.ChatManager;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.easeui.ui.BaseActivity;
import com.hyphenate.helpdesk.easeui.widget.ToastHelper;
import com.hyphenate.helpdesk.model.MessageHelper;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RobotCommentTagsActivity extends BaseActivity {
    private final static String TAG = RobotCommentTagsActivity.class.getSimpleName();
    private ProgressDialog pd = null;
    private FlowTagLayout mFlowTagLayout;
    private TagAdapter<String> tagAdapter;
    private List<String> selectedTags = Collections.synchronizedList(new ArrayList<String>());
    private Message currentMessage;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.em_activity_robot_comment_tags);
        String msgId = getIntent().getStringExtra("msgId");
        initView();
        currentMessage = ChatClient.getInstance().chatManager().getMessage(msgId);

        ChatManager.getInstance().getRobotQualityTags(currentMessage, new EMValueCallBack<List<String>>() {
            @Override
            public void onSuccess(List<String> strings) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        EMLog.d(TAG, "getRobotQualityTags success");
                        tagAdapter.clearAndAndAll(strings);
                    }
                });
            }

            @Override
            public void onError(int i, String s) {

            }
        });

        refreshFlowLayout();
    }


    private void refreshFlowLayout(){
        mFlowTagLayout.setVisibility(View.VISIBLE);
        selectedTags.clear();
    }

    private void initView() {
        Button btnSubmit = findViewById(R.id.submit);
        Button btnCancel = findViewById(R.id.btn_cancel);

        mFlowTagLayout = findViewById(R.id.id_flowlayout);
        mFlowTagLayout.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_MULTI);
        mFlowTagLayout.setAdapter(tagAdapter = new TagAdapter<>(this));

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(RobotCommentTagsActivity.this);
                pd.setMessage(getResources().getString(R.string.em_tip_wating));
                pd.show();

                ChatManager.getInstance().postRobotQuality(currentMessage, true, selectedTags, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        EMLog.d(TAG, "robot comment sucess");
                        MessageHelper.createCommentSuccessMsg(currentMessage,"");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (pd != null && pd.isShowing()) {
                                    pd.dismiss();
                                }
                                ToastHelper.show(getBaseContext(), R.string.comment_suc);
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onError(int i, String s) {
                        EMLog.e(TAG, "robot comment fail: " + i + "reason: " + s);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (pd != null && pd.isShowing()) {
                                    pd.dismiss();
                                }
                                ToastHelper.show(getBaseContext(), s);
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back(v);
            }
        });

        mFlowTagLayout.setOnTagSelectListener(new OnTagSelectListener() {
            @Override
            public void onItemSelect(FlowTagLayout parent, List<Integer> selectedList) {
                selectedTags.clear();
                if (selectedList != null && !selectedList.isEmpty()){
                    for (int i : selectedList){
                        selectedTags.add( (String)parent.getAdapter().getItem(i));
                    }
                }
            }
        });

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
