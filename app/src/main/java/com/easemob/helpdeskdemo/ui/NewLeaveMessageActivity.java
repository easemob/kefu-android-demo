package com.easemob.helpdeskdemo.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.easemob.helpdeskdemo.Preferences;
import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.utils.ListenerManager;
import com.google.gson.Gson;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.callback.ValueCallBack;
import com.hyphenate.helpdesk.domain.NewTicketBody;
import com.hyphenate.helpdesk.easeui.ui.BaseActivity;
import com.hyphenate.helpdesk.easeui.widget.AlertDialogFragment;
import com.hyphenate.helpdesk.easeui.widget.ToastHelper;
import com.hyphenate.helpdesk.util.Log;
import com.hyphenate.util.EMLog;

/**
 * 发起留言界面
 */
public class NewLeaveMessageActivity extends BaseActivity implements View.OnClickListener {


    private static final String TAG = NewLeaveMessageActivity.class.getSimpleName();

    private ProgressDialog progressDialog;

    private RelativeLayout sendLayout;
    private RelativeLayout successLayout;
    private EditText contentText;
    private RelativeLayout rlName;
    private RelativeLayout rlPhone;
    private RelativeLayout rlEmail;
    private RelativeLayout rlTheme;
    private EditText itemName;
    private EditText itemPhone;
    private EditText itemEmail;
    private EditText itemTheme;
    private RelativeLayout detailLayout;
    private TextView detailText;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.em_activity_newleave);
        initView();
        initListener();
    }

    private void initView() {
        sendLayout = $(R.id.rl_new_leave_send);
        successLayout = $(R.id.rl_new_leave_success);
        contentText = $(R.id.et_new_leave_content);
        itemName = $(R.id.et_name);
        rlName = $(R.id.rl_name);
        itemPhone = $(R.id.et_phone);
        rlPhone = $(R.id.rl_phone);
        itemEmail = $(R.id.et_email);
        rlEmail = $(R.id.rl_email);
        itemTheme = $(R.id.et_theme);
        rlTheme = $(R.id.rl_theme);
        detailLayout = $(R.id.rl_detail_content);
        detailText = $(R.id.tv_detail);
        contentText.requestFocus();
    }

    private void initListener() {
        $(R.id.rl_back).setOnClickListener(this);
        $(R.id.rl_send).setOnClickListener(this);
        //输入法回车焦点切换
        itemName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    itemPhone.requestFocus();
                        return true;
                    }
                        return false;
                }
            });
        itemPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    itemEmail.requestFocus();
                    return true;
                }
                return false;
            }
        });
        itemEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    itemTheme.requestFocus();
                    return true;
                }
                return false;
            }
        });
        itemTheme.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    itemTheme.clearFocus();
                    return true;
                }
                return false;
            }
        });

        /* 4个内容的点击区域过小 点击layout可以request focus */
        rlName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemName.requestFocus();
            }
        });
        rlPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemPhone.requestFocus();
            }
        });
        rlEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemEmail.requestFocus();
            }
        });
        rlTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemTheme.requestFocus();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_send:
                if (hasItemsNoValue()) {
                    ToastHelper.show(NewLeaveMessageActivity.this, R.string.new_leave_item_empty_value_toast);
                    return;
                }
                contentText.requestFocus();
                commitLeaveMessage();
                break;
            default:
                break;
        }
    }

    private boolean hasItemsNoValue () {
        if (itemName.getText().toString().length() == 0){
            return true;
        }
        if (itemPhone.getText().toString().length() == 0) {
            return true;
        }
//        if (itemEmail.getText().toString().length() == 0) {
//            return true;
//        }
        return itemTheme.getText().toString().length() == 0;
    }

    private void commitLeaveMessage(){
        if (!ChatClient.getInstance().isLoggedInBefore()){
            // 未成功登录不允许发送留言信息
            return;
        }

        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.leave_sending));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();


        NewTicketBody ticketBody = new NewTicketBody();
        ticketBody.setContent(contentText.getText().toString());
        ticketBody.setSubject(itemTheme.getText().toString());
        NewTicketBody.CreatorBean creatorBean = new NewTicketBody.CreatorBean();
        creatorBean.setEmail(itemEmail.getText().toString());
        creatorBean.setName(itemName.getText().toString());
        creatorBean.setPhone(itemPhone.getText().toString());
        ticketBody.setCreator(creatorBean);

        String target = Preferences.getInstance().getCustomerAccount();
        String tenantId = Preferences.getInstance().getTenantId();
        String projectId = Preferences.getInstance().getProjectId();
        Gson gson = new Gson();
        ChatClient.getInstance().leaveMsgManager().createLeaveMsg(gson.toJson(ticketBody).toString(), projectId, target, new ValueCallBack<String>() {

            @Override
            public void onSuccess(final String value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeDialog();
                        ListenerManager.getInstance().sendBroadCast("NewTicketEvent", null);
                        Log.d(TAG, "value:" + value);
                        sendLayout.setVisibility(View.GONE);
                        contentText.setVisibility(View.GONE);
                        successLayout.setVisibility(View.VISIBLE);
                        itemName.setKeyListener(null);
                        itemPhone.setKeyListener(null);
                        itemEmail.setKeyListener(null);
                        itemTheme.setKeyListener(null);
                        detailLayout.setVisibility(View.VISIBLE);
                        detailText.setText(contentText.getText().toString());
                    }
                });
            }

            @Override
            public void onError(int code, final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeDialog();
                        EMLog.e(TAG, "error:" + error);
                        if (!NewLeaveMessageActivity.this.isFinishing()) {
                            showAlertDialog();
                        }
                    }
                });
            }

        });
    }

    private void showAlertDialog() {
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        String fragmentTag = "dialogFragment";
        Fragment fragment =  getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if(fragment!=null){
            //为了不重复显示dialog，在显示对话框之前移除正在显示的对话框
            mFragTransaction.remove(fragment);
        }
        final AlertDialogFragment dialogFragment = new AlertDialogFragment();
        dialogFragment.setTitleText(getString(R.string.new_leave_msg_sub_fail));
        dialogFragment.setContentText(getString(R.string.new_leave_msg_sub_fail_alert_content));
        dialogFragment.setRightBtnText(getString(R.string.new_leave_msg_alert_ok));
        dialogFragment.show(mFragTransaction, fragmentTag);
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
