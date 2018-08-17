package com.hyphenate.helpdesk.model;


import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.helpdesk.ChatClient;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageHelper {
    private static final String TAG = "MessageHelper";
    private static final boolean DEBUG = false;
    public static final String TAG_ROOT = "ext";
    public static final String TAG_MSGTYPE = "msgtype";
    public static final String TAG_WEICHAT = "weichat";

    static final String TRANSFER_INDICATION_ACTION = "transfer";


    static JSONObject getContainerObject(Message msg, String tag) {
        EMMessage message = msg.getEMMessage();
        JSONObject obj = null;
        try {
            if (message != null)
                obj = message.getJSONObjectAttribute(tag);
        } catch (HyphenateException e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return obj;

    }

    public static ControlMessage getEvalRequest(Message message) {
        ControlMessage controlMessage = null;
        JSONObject content = extractObject(message, ControlMessage.PARENT_NAME, null, true);
        if (content != null)
            controlMessage = ContentFactory.createControlMessage(content);
        if (controlMessage == null || controlMessage.isNull() || controlMessage.getControlType() == null){
            return null;
        }
        if (controlMessage.getControlType().equals(ControlMessage.TYPE_EVAL_REQUEST)){
            return controlMessage;
        }
        return null;
    }

    public static ControlMessage getEvalResponse(Message message) {
        ControlMessage controlMessage = null;
        JSONObject content = extractObject(message, ControlMessage.PARENT_NAME, null, true);
        if (content != null)
            controlMessage = ContentFactory.createControlMessage(content);
        if (controlMessage == null || controlMessage.isNull() || controlMessage.getControlType() == null || !controlMessage.getControlType().equals(ControlMessage.TYPE_EVAL_RESPONSE))
            controlMessage = null;
        return controlMessage;
    }

    public static RobotMenuInfo getRobotMenu(Message message) {
        RobotMenuInfo menuInfo = null;
        JSONObject content = extractObject(message, RobotMenuInfo.PARENT_NAME, RobotMenuInfo.NAME, false);
        if (content != null)
            menuInfo = ContentFactory.createRobotMenuInfo(content);
        if (menuInfo != null){
            if (menuInfo.has(RobotMenuInfo.NAME_ITEMS) || menuInfo.has(RobotMenuInfo.NAME_LIST)){
                return menuInfo;
            }
        }
        return null;
    }

    public static TransferIndication getTransferIndication(Message message) {
        TransferIndication indication = null;
        if (hasAction(message, TRANSFER_INDICATION_ACTION)) {
            JSONObject content = extractObject(message, TransferIndication.PARENT_NAME, null, true);
            if (content != null) {
                indication = ContentFactory.createTransferIndication(content);
                if (indication.isNull())
                    indication = null;
            }
        }
        return indication;

    }

    public static AgentInfo getAgentInfo(Message message) {
        AgentInfo agentInfo = null;
        JSONObject content = extractObject(message, AgentInfo.PARENT_NAME, AgentInfo.NAME, false);
        if (content != null)
            agentInfo = ContentFactory.createAgentInfo(content);
        return agentInfo;
    }

    public static ControlMessage getTransferToAgent(Message message) {
        return null;
    }

    public static OrderInfo getOrderInfo(Message message) {
        OrderInfo orderInfo = null;
        JSONObject content = extractObject(message, OrderInfo.PARENT_NAME, OrderInfo.NAME, false);
        if (content != null)
            orderInfo = ContentFactory.createOrderInfo(content);
        if (orderInfo != null){
            if (orderInfo.getPrice() == null && orderInfo.getDesc() == null){
                orderInfo = null;
            }
        }
        return orderInfo;
    }

    public static VisitorTrack getVisitorTrack(Message message) {
        VisitorTrack visitorTrack = null;
        JSONObject content = extractObject(message, VisitorTrack.PARENT_NAME, VisitorTrack.NAME, false);
        if (content != null)
            visitorTrack = ContentFactory.createVisitorTrack(content);
        if (visitorTrack != null){
            if (visitorTrack.getDesc() == null && visitorTrack.getPrice() == null){
                visitorTrack = null;
            }
        }
        return visitorTrack;
    }

    private static boolean hasAction(Message message, String action) {
        EMMessage msg = message.getEMMessage();
        if (msg.getType() == EMMessage.Type.CMD) {
            EMCmdMessageBody body = (EMCmdMessageBody) msg.getBody();
            if (body.action().equals(action))
                return true;
        }
        return false;
    }

    private static JSONObject extractObject(Message message, String parentName, String name, boolean isComposite) {
        JSONObject content = null;
        JSONObject container = getContainerObject(message, parentName);
        if (!isComposite) {
            if (container != null) {
                if (container.isNull(name)) {
                    return null;
                }
                try {
                    content = container.getJSONObject(name);
                } catch (JSONException ex) {
                    if (DEBUG) {
                        ex.printStackTrace();
                    }
                }
            }
            return content;
        } else {
            return container;
        }

    }

    @SuppressWarnings("unused")
    private static String extractString(Message message, String parentName, String name) {
        String content = null;
        JSONObject container = getContainerObject(message, parentName);
        if (container != null) {
            try {
                content = container.getString(name);
            } catch (JSONException ex) {
                if (DEBUG) {
                    ex.printStackTrace();
                }
            }
        }
        return content;

    }


    public static ToCustomServiceInfo getToCustomServiceInfo(Message message) {
        ToCustomServiceInfo info = null;
        JSONObject jsonObject = getContainerObject(message, ToCustomServiceInfo.PARENT_NAME);
        if (jsonObject != null) {
            String ctrlType = getString(jsonObject, ToCustomServiceInfo.TYPE);
            if (ctrlType != null && ctrlType.equals(ToCustomServiceInfo.TYPE_VALUE)) {
                JSONObject content = getJSONObject(jsonObject, ToCustomServiceInfo.NAME);
                if (content != null) {
                    info = ContentFactory.createToCustomeServiceInfo(content);
                }
            }

        }
        return info;
    }

    private static JSONObject getJSONObject(JSONObject jsonObject, String name) {
        if (jsonObject.has(name) && !jsonObject.isNull(name)) {
            try {
                return jsonObject.getJSONObject(name);
            } catch (JSONException e) {
                if (DEBUG) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static String getString(JSONObject jsonObj, String name) {
        if (jsonObj.has(name) && !jsonObj.isNull(name)) {
            try {
                return jsonObj.getString(name);
            } catch (JSONException e) {
                if (DEBUG) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void sendEvalMessage(String msgId, String summary, String detail, final EMCallBack callback){
        Message msg = ChatClient.getInstance().getChat().getMessage(msgId);
        ControlMessage controlMessage = getEvalRequest(msg);
        controlMessage.setSummary(summary);
        controlMessage.setDetail(detail);
        controlMessage.setControlType(ControlMessage.TYPE_EVAL_RESPONSE);
        final Message messageToSend = Message.createMessage();
        messageToSend.setTo(msg.getFrom());
        messageToSend.addContent(controlMessage);
        messageToSend.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                ChatClient.getInstance().getChat().removeMessageFromConversation(messageToSend);
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(int i, String s) {
                ChatClient.getInstance().getChat().removeMessageFromConversation(messageToSend);
                if (callback != null) {
                    callback.onError(i, s);
                }
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
        ChatClient.getInstance().getChat().sendMessage(messageToSend);
    }

}