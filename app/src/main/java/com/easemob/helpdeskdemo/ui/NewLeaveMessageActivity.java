package com.easemob.helpdeskdemo.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.Preferences;
import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.utils.ListenerManager;
import com.google.gson.Gson;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.callback.ValueCallBack;
import com.hyphenate.helpdesk.domain.NewTicketBody;
import com.hyphenate.helpdesk.easeui.ui.BaseActivity;
import com.hyphenate.helpdesk.manager.TicketManager;
import com.hyphenate.helpdesk.util.Log;

/**
 * 发起留言界面
 */
public class NewLeaveMessageActivity extends BaseActivity implements View.OnClickListener {


    private static final String TAG = NewLeaveMessageActivity.class.getSimpleName();

    private static final int REQUEST_CODE_NAME = 0x01;
    private static final int REQUEST_CODE_PHONE = 0x02;
    private static final int REQUEST_CODE_EMAIL = 0x03;
    private static final int REQUEST_CODE_CONTENT = 0x04;

    /**
     * 姓名
     */
    private TextView tvName;
    /**
     * 电话
     */
    private TextView tvPhone;
    /**
     * 邮箱
     */
    private TextView tvEmail;
    /**
     * 内容
     */
    private TextView tvContent;

    private String tempName;
    private String tempPhone;
    private String tempEmail;
    private String tempContent;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_newleave);
        initView();
        initListener();
    }

    private void initView() {
        tvName = $(R.id.tv_name);
        tvPhone = $(R.id.tv_phone);
        tvEmail = $(R.id.tv_email);
        tvContent = $(R.id.tv_content);
    }

    private void initListener() {
        $(R.id.rl_name).setOnClickListener(this);
        $(R.id.rl_phone).setOnClickListener(this);
        $(R.id.rl_email).setOnClickListener(this);
        $(R.id.rl_content).setOnClickListener(this);

        $(R.id.ib_back).setOnClickListener(this);
        $(R.id.btn_send).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.rl_name:
                String strName = tvName.getText().toString();
                intent.setClass(this, ModifyActivity.class);
                intent.putExtra(Constant.MODIFY_ACTIVITY_INTENT_INDEX, Constant.MODIFY_INDEX_LEAVE_NAME);
                intent.putExtra(Constant.MODIFY_ACTIVITY_INTENT_CONTENT, strName);
                startActivityForResult(intent, REQUEST_CODE_NAME);
                break;
            case R.id.rl_phone:
                String strPhone = tvPhone.getText().toString();
                intent.setClass(this, ModifyActivity.class);
                intent.putExtra(Constant.MODIFY_ACTIVITY_INTENT_INDEX, Constant.MODIFY_INDEX_LEAVE_PHONE);
                intent.putExtra(Constant.MODIFY_ACTIVITY_INTENT_CONTENT, strPhone);
                startActivityForResult(intent, REQUEST_CODE_PHONE);
                break;
            case R.id.rl_email:
                String strEmail = tvEmail.getText().toString();
                intent.setClass(this, ModifyActivity.class);
                intent.putExtra(Constant.MODIFY_ACTIVITY_INTENT_INDEX, Constant.MODIFY_INDEX_LEAVE_EMAIL);
                intent.putExtra(Constant.MODIFY_ACTIVITY_INTENT_CONTENT, strEmail);
                startActivityForResult(intent, REQUEST_CODE_EMAIL);
                break;
            case R.id.rl_content:
                String strContent = tvContent.getText().toString();
                intent.setClass(this, ModifyActivity.class);
                intent.putExtra(Constant.MODIFY_ACTIVITY_INTENT_INDEX, Constant.MODIFY_INDEX_LEAVE_CONTENT);
                intent.putExtra(Constant.MODIFY_ACTIVITY_INTENT_CONTENT, strContent);
                startActivityForResult(intent, REQUEST_CODE_CONTENT);
                break;
            case R.id.ib_back:
                finish();
                break;
            case R.id.btn_send:
                commitLeaveMessage();
                break;
        }
    }

    private void commitLeaveMessage(){
        if (!ChatClient.getInstance().isLoggedInBefore()){
            // 未成功登录不允许发送留言信息
            return;
        }

        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.leave_sending));
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();


        NewTicketBody ticketBody = new NewTicketBody();
        ticketBody.setContent(tvContent.getText().toString());
        NewTicketBody.CreatorBean creatorBean = new NewTicketBody.CreatorBean();
        creatorBean.setEmail(tvEmail.getText().toString());
        creatorBean.setName(tvName.getText().toString());
        creatorBean.setPhone(tvPhone.getText().toString());
        ticketBody.setCreator(creatorBean);

        String target = Preferences.getInstance().getCustomerAccount();
        String tenantId = Preferences.getInstance().getTenantId();
        String projectId = Preferences.getInstance().getProjectId();
        Gson gson = new Gson();
        TicketManager.getInstance().createLeaveMessage(gson.toJson(ticketBody).toString(), projectId, target, new ValueCallBack<String>() {

            @Override
            public void onSuccess(final String value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeDialog();
                        Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
                        finish();
                        ListenerManager.getInstance().sendBroadCast("NewTicketEvent", null);
                        Log.d(TAG, "value:" + value);
                    }
                });
            }

            @Override
            public void onError(int code, final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeDialog();
                        Log.e(TAG, "error:" + error);
                    Toast.makeText(getApplicationContext(), "发送失败,请检查网络", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK){
            return;
        }
        switch (requestCode){
            case REQUEST_CODE_NAME:
                String newName = data.getStringExtra(Constant.MODIFY_ACTIVITY_INTENT_CONTENT);
                if(tempName != null && tempName.equals(newName)){
                    return;
                }
                tempName = newName;
                tvName.setText(newName);
                break;
            case REQUEST_CODE_PHONE:
                String newPhone = data.getStringExtra(Constant.MODIFY_ACTIVITY_INTENT_CONTENT);
                if(tempPhone != null && tempPhone.equals(newPhone)){
                    return;
                }
                tempPhone = newPhone;
                tvPhone.setText(newPhone);
                break;
            case REQUEST_CODE_EMAIL:
                String newEmail = data.getStringExtra(Constant.MODIFY_ACTIVITY_INTENT_CONTENT);
                if(tempEmail != null && tempEmail.equals(newEmail)){
                    return;
                }
                tempEmail = newEmail;
                tvEmail.setText(newEmail);
                break;
            case REQUEST_CODE_CONTENT:
                String newContent = data.getStringExtra(Constant.MODIFY_ACTIVITY_INTENT_CONTENT);
                if(tempContent != null && tempContent.equals(newContent)){
                    return;
                }
                tempContent = newContent;
                tvContent.setText(newContent);
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDialog();
    }

    private void closeDialog(){
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

}
