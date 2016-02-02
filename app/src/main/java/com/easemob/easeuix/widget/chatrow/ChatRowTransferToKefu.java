package com.easemob.easeuix.widget.chatrow;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.easeui.widget.chatrow.EaseChatRow;
import com.easemob.exceptions.EaseMobException;
import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.DemoHelper;
import com.easemob.helpdeskdemo.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lyuzhao on 2016/1/22.
 */
public class ChatRowTransferToKefu extends EaseChatRow {

    Button btnTransfer;
    TextView tvContent;
    String uuid = null;
    String serviceSessionId = null;

    public ChatRowTransferToKefu(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        if (DemoHelper.getInstance().isTransferToKefuMsg(message)) {
            inflater.inflate(message.direct == EMMessage.Direct.RECEIVE ? R.layout.em_row_received_transfertokefu
                    : R.layout.em_row_sent_transfertokefu, this);
        }
    }

    @Override
    protected void onFindViewById() {
        btnTransfer = (Button) findViewById(R.id.btn_transfer);
        tvContent = (TextView) findViewById(R.id.tv_chatcontent);
    }

    @Override
    protected void onUpdateView() {

    }

    @Override
    protected void onSetUpView() {
        if (message.getType() == EMMessage.Type.TXT) {
            TextMessageBody msgBody = (TextMessageBody) message.getBody();
            tvContent.setText(msgBody.getMessage());
        }
        if (btnTransfer == null) {
            return;
        }
        try {
            JSONObject jsonWeiChat = message.getJSONObjectAttribute(Constant.WEICHAT_MSG);
            if (jsonWeiChat.has("ctrlArgs")) {
                JSONObject jsonCtrlArgs = jsonWeiChat.getJSONObject("ctrlArgs");
                uuid = jsonCtrlArgs.getString("id");
                serviceSessionId = jsonCtrlArgs.getString("serviceSessionId");
                String btnLabel = jsonCtrlArgs.getString("label");
                if (!TextUtils.isEmpty(btnLabel)) {
                    btnTransfer.setText(btnLabel);
                }
            }
        } catch (EaseMobException e) {
        } catch (JSONException e) {
        }


        btnTransfer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendTransferToKefuMessage();
            }
        });
    }

    private void sendTransferToKefuMessage() {
        if (TextUtils.isEmpty(uuid) || TextUtils.isEmpty(serviceSessionId)) {
            return;
        }
        EMMessage cmdMessage = EMMessage.createSendMessage(EMMessage.Type.CMD);
        cmdMessage.setReceipt(message.getFrom());
        CmdMessageBody cmdMsgBody = new CmdMessageBody("TransferToKf");
        cmdMessage.addBody(cmdMsgBody);
        JSONObject weichatJson = new JSONObject();
        JSONObject ctrlArgsJson = new JSONObject();
        try {
            ctrlArgsJson.put("id", uuid);
            ctrlArgsJson.put("serviceSessionId", serviceSessionId);
            weichatJson.put("ctrlArgs", ctrlArgsJson);
            cmdMessage.setAttribute(Constant.WEICHAT_MSG, weichatJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        EMChatManager.getInstance().sendMessage(cmdMessage, new EMCallBack() {

            @Override
            public void onSuccess() {
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(activity, "发送成功！", Toast.LENGTH_SHORT).show();
                    }

                });
            }

            @Override
            public void onProgress(int progress, String status) {


            }

            @Override
            public void onError(int code, String error) {
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(activity, "发送失败！", Toast.LENGTH_SHORT).show();
                    }

                });
            }
        });


    }


    @Override
    protected void onBubbleClick() {

    }
}
