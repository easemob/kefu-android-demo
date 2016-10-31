package com.hyphenate.helpdesk.easeui.widget.chatrow;

import android.content.Context;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.easeui.util.SmileUtils;
import com.hyphenate.helpdesk.model.MessageHelper;
import com.hyphenate.helpdesk.model.ToCustomServiceInfo;

/**
 */
public class ChatRowTransferToKefu extends ChatRow {

    Button btnTransfer;
    TextView tvContent;

    public ChatRowTransferToKefu(Context context, Message message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        if (MessageHelper.getToCustomServiceInfo(message) != null){
            inflater.inflate(message.direct() == Message.Direct.RECEIVE ? R.layout.ease_row_received_transfertokefu
                    : R.layout.ease_row_sent_transfertokefu, this);
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
        final ToCustomServiceInfo toCustomServiceInfo;
        if ((toCustomServiceInfo = MessageHelper.getToCustomServiceInfo(message)) != null){
            EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
            Spannable span = SmileUtils.getSmiledText(context, txtBody.getMessage());
            // 设置内容
            tvContent.setText(span, TextView.BufferType.SPANNABLE);
            String btnLable = toCustomServiceInfo.getLable();
            if (!TextUtils.isEmpty(btnLable)){
                btnTransfer.setText(btnLable);
            }
            btnTransfer.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendToCustomServiceMessage(toCustomServiceInfo);
                }
            });

        }
    }

    private void sendToCustomServiceMessage(ToCustomServiceInfo info){
        if (TextUtils.isEmpty(info.getId()) || TextUtils.isEmpty(info.getServiceSessionId())){
            return;
        }
        Message cmdMessage = Message.createSendMessage(Message.Type.CMD);
        cmdMessage.setTo(message.getFrom());
        EMCmdMessageBody cmdMessageBody = new EMCmdMessageBody("TransferToKf");
        cmdMessage.addBody(cmdMessageBody);
        cmdMessage.addContent(info);
//        cmdMessage.setMessageStatusCallback(new Callback() {
//            @Override
//            public void onSuccess() {
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(activity, "发送成功!", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//
//            @Override
//            public void onError(int i, String s) {
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(activity, "发送失败!", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//            }
//
//            @Override
//            public void onProgress(int i, String s) {
//
//            }
//        });
        ChatClient.getInstance().getChat().sendMessage(cmdMessage);


    }


    @Override
    protected void onBubbleClick() {

    }
}
