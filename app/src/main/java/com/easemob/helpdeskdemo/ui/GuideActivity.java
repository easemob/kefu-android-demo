package com.easemob.helpdeskdemo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.Preferences;
import com.easemob.helpdeskdemo.R;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.easeui.widget.ToastHelper;


public class GuideActivity extends Activity implements View.OnClickListener {

    private static final int REQUEST_CODE_ACCOUNT = 2;
    private static final int REQUEST_CODE_CONFIG_ID = 6;
    private TextView tvAccount;
    private View rlAccount;
    private View ll_setting_list_account_config;
    private TextView iv_account_right_config;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_guide_activity);
        tvAccount = (TextView) findViewById(R.id.tv_setting_account);
        rlAccount = (RelativeLayout) findViewById(R.id.ll_setting_list_account);
        iv_account_right_config = (TextView)findViewById(R.id.tv_setting_account_config);
        ll_setting_list_account_config = (RelativeLayout)findViewById(R.id.ll_setting_list_account_config);


        rlAccount.setOnClickListener(this);
        ll_setting_list_account_config.setOnClickListener(this);

        // kefuchannelimid_392654
        Preferences.getInstance().setGuideVecImServiceNumber("kefuchannelimid_392654");
        // e123544e-1f36-4ca0-a666-135bb2f04466
        Preferences.getInstance().setGuideVecConfigId("e123544e-1f36-4ca0-a666-135bb2f04466");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.ll_setting_list_account){
            Intent intent = new Intent();
            String strAccount = tvAccount.getText().toString();
            intent.setClass(this, ModifyActivity.class);
            intent.putExtra(Constant.MODIFY_ACTIVITY_INTENT_INDEX, Constant.MODIFY_INDEX_VEC_ACCOUNT);
            intent.putExtra(Constant.MODIFY_ACTIVITY_INTENT_CONTENT, strAccount);
            startActivityForResult(intent, REQUEST_CODE_ACCOUNT);
        }else if (id == R.id.ll_setting_list_account_config){
            Intent intent = new Intent();
            String strConfigId = iv_account_right_config.getText().toString();
            intent.setClass(this, ModifyActivity.class);
            intent.putExtra(Constant.MODIFY_ACTIVITY_INTENT_INDEX, Constant.MODIFY_INDEX_LEAVE_VEC_CONFIG);
            intent.putExtra(Constant.MODIFY_ACTIVITY_INTENT_CONTENT, strConfigId);
            startActivityForResult(intent, REQUEST_CODE_CONFIG_ID);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_ACCOUNT){
                String oldAccount = tvAccount.getText().toString();
                String newAccount = data.getStringExtra(Constant.MODIFY_ACTIVITY_INTENT_CONTENT);
                if (TextUtils.isEmpty(newAccount.trim())) {
                    ToastHelper.show(this, R.string.cus_account_cannot_be_empty);
                    return;
                }
                if (oldAccount.equals(newAccount)) {
                    return;
                }
                tvAccount.setText(newAccount);
                Preferences.getInstance().setGuideVecImServiceNumber(newAccount);
            }else if (requestCode == REQUEST_CODE_CONFIG_ID){
                String oldConfigId = iv_account_right_config.getText().toString();
                String newConfigId = data.getStringExtra(Constant.MODIFY_ACTIVITY_INTENT_CONTENT);
                if (oldConfigId.equals(newConfigId)) {
                    return;
                }
                iv_account_right_config.setText(newConfigId);
                Preferences.getInstance().setGuideVecConfigId(newConfigId);
            }
        }
    }
}
