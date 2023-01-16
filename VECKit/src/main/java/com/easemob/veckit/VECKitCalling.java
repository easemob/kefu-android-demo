package com.easemob.veckit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.easemob.veckit.utils.FlatFunctionUtils;
import com.easemob.veckit.utils.Utils;
import com.google.gson.JsonObject;
import com.hyphenate.agora.FunctionIconItem;
import com.hyphenate.chat.AgoraMessage;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.ChatManager;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.chat.VecConfig;
import com.hyphenate.helpdesk.callback.ValueCallBack;
import com.hyphenate.helpdesk.util.Log;
import com.hyphenate.util.EMLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class VECKitCalling extends Activity {

    private String mToChatUserName;
    private ProgressDialog mDialog;

    // 主动请求
    public static void callingRequest(Context context, String toChatUserName){
        EMLog.e("VECVideo","客户呼叫坐席 主动请求 VECKitCalling.callingRequest toChatUserName = "+toChatUserName);
        Intent intent = new Intent(context, VECKitCalling.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (TextUtils.isEmpty(toChatUserName)){
            toChatUserName = AgoraMessage.newAgoraMessage().getCurrentChatUsername();
        }
        intent.putExtra(VideoCallWindowService.CURRENT_CHAT_USER_NAME, toChatUserName);
        context.startActivity(intent);
    }

    // 满意度评价
    public static void callingRetry(Context context, String content){
        EMLog.e("VECVideo","唤起满意度评价 VECKitCalling.callingRetry content = "+content);
        CallVideoActivity.startDialogTypeRetry(context, content);
    }

    // 被动请求
    public static void callingResponse(Context context, Intent intent){
        EMLog.e("VECVideo","坐席呼叫客户 被动请求 VECKitCalling.callingResponse");
        CallVideoActivity.callingResponse(context, intent);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mToChatUserName = intent.getStringExtra(VideoCallWindowService.CURRENT_CHAT_USER_NAME);
        mDialog = new ProgressDialog(this);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.setMessage(Utils.getString(getApplicationContext(), R.string.vec_loading));


        // 请求灰度
        // getTenantIdFunctionIcons();

        String configId = ChatClient.getInstance().getConfigId();
        if (TextUtils.isEmpty(configId)){
            Toast.makeText(VECKitCalling.this, Utils.getString(getApplicationContext(), R.string.vec_config_setting), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mDialog.show();
        if (VecConfig.newVecConfig().isEnableVideo()){
            requestInfo();
        }else {
            showToast(VECKitCalling.this,Utils.getString(getApplicationContext(), R.string.vec_no_permission));
            if (mDialog != null && mDialog.isShowing()){
                mDialog.dismiss();
            }
            finish();
        }
    }

    private void request() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String tenantId = ChatClient.getInstance().tenantId();// "77556"
                String configId = ChatClient.getInstance().getConfigId();
                if (TextUtils.isEmpty(configId)){
                    mDialog.dismiss();
                    Toast.makeText(VECKitCalling.this, Utils.getString(getApplicationContext(), R.string.vec_config_setting), Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                AgoraMessage.asyncInitStyle(tenantId, configId, new ValueCallBack<String>() {
                    @Override
                    public void onSuccess(String value) {
                        try {
                            JSONObject jsonObject = new JSONObject(value);
                            if (!jsonObject.has("status")){
                                return;
                            }
                            String status = jsonObject.getString("status");
                            if (!"ok".equalsIgnoreCase(status)){
                                return;
                            }

                            // 存起来
                            JSONObject entity = jsonObject.getJSONObject("entity");
                            if (isFinishing()){
                                return;
                            }

                            String json = entity.toString();
                            to(json);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        to("");
                    }
                });
            }
        });

    }

    private void to(String json){
        runOnUiThread(() -> {
            mDialog.dismiss();
            CallVideoActivity.callingRequest(getApplicationContext(), mToChatUserName, json);
            finish();
        });
    }

    /**
     * 坐席发起视频邀请。访客发送接通信令
     * @param content 显示内容
     */
    public static void acceptCallFromZuoXi(String content){
        EMLog.e("VECVideo","座席端请求视频，访客接通 指令 acceptCallFromZuoXi content = "+content);
        AgoraMessage.acceptCallFromZuoXi(content);
    }

    /**
     * 坐席发起视频邀请，访客未接通状态。访客发送挂断信令
     * @param content 显示内容
     */
    public static void endCallFromZuoXi(String content){
        EMLog.e("VECVideo","坐席发起邀请，未接通，访客挂断 指令 endCallFromZuoXi content = "+content);
        AgoraMessage.endCallFromZuoXi(content);
    }

    /**
     * 视频已接通。发送挂断信令
     * @param callBack 回调函数
     */
    public static void endCallFromOn(ValueCallBack<String> callBack){
        EMLog.e("VECVideo","接通挂断 指令 endCallFromOn");
        AgoraMessage.endCallFromOn(callBack);
    }

    /**
     * 主动发起视频邀请，坐席未接通状态。主动发送挂断信令
     */
    public static void endCallFromOff(){
        EMLog.e("VECVideo","未接通挂断 指令 endCallFromOff");
        AgoraMessage.endCallFromOff();
    }

    private void getTenantIdFunctionIcons(){
        // 动态获取功能按钮，在视频页面使用到
        AgoraMessage.asyncGetTenantIdFunctionIcons(ChatClient.getInstance().tenantId(), new ValueCallBack<List<FunctionIconItem>>() {
            @Override
            public void onSuccess(List<FunctionIconItem> value) {
                FlatFunctionUtils.get().setIconItems(value);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (VecConfig.newVecConfig().isEnableVideo()){
                            requestInfo();
                        }else {
                            showToast(VECKitCalling.this,Utils.getString(getApplicationContext(), R.string.vec_no_permission));
                            finish();
                        }
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast(VECKitCalling.this,Utils.getString(getApplicationContext(), R.string.vec_request_fail));
                        finish();
                    }
                });
            }
        });
    }

    private void requestInfo() {
        AgoraMessage.asyncGetInfo(ChatClient.getInstance().tenantId(), new ValueCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                parseInfoData(value);
                request();
            }

            @Override
            public void onError(int error, String errorMsg) {
                request();
            }
        });
    }

    private void parseInfoData(String value) {
        try {
            JSONObject data = new JSONObject(value);
            JSONObject entity = data.getJSONObject("entity");
            VecConfig.newVecConfig().setTenantName(entity.getString("name"));
            String avatar = entity.getString("avatar");
            int i = avatar.indexOf("kefu.easemob.com");
            String substring = avatar.substring(i + "kefu.easemob.com".length());
            VecConfig.newVecConfig().setAvatarImage(ChatClient.getInstance().kefuRestServer().concat(substring));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showToast(Context context, String content){
        Toast.makeText(context.getApplicationContext(), content, Toast.LENGTH_LONG).show();
    }
}
