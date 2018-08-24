package com.hyphenate.helpdesk;


import android.text.TextUtils;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.model.AgentIdentityInfo;
import com.hyphenate.helpdesk.model.Message;
import com.hyphenate.helpdesk.model.MessageAttributes;
import com.hyphenate.helpdesk.model.QueueIdentityInfo;
import com.hyphenate.helpdesk.model.VisitorInfo;
import com.hyphenate.helpdesk.util.DebugUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Chat {
    private EMChatManager _manager;
    private List<MessageListener> listeners = Collections.synchronizedList(new ArrayList<MessageListener>());

    Chat(EMChatManager chatManager){
        _manager = chatManager;
//        listeners = Collections.synchronizedList(new ArrayList<MessageListener>());
        _manager.addMessageListener(new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                synchronized (listeners) {
                    for(MessageListener listener : listeners){
                        listener.onMessage(toMessages(messages));
                    }
                }
                DebugUtil.printMessageList(messages);
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                synchronized (listeners) {
                    for (MessageListener listener : listeners) {
                        listener.onCmdMessage(toMessages(messages));
                    }
                }
                DebugUtil.printMessageList(messages);
            }

            @Override
            public void onMessageReadAckReceived(List<EMMessage> messages) {
                synchronized (listeners) {
                    for(MessageListener listener : listeners){
                        listener.onMessageStatusUpdate();
                    }
                }
            }

            @Override
            public void onMessageDeliveryAckReceived(List<EMMessage> messages) {
                synchronized (listeners) {
                    for(MessageListener listener : listeners){
                        listener.onMessageStatusUpdate();
                    }
                }
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                synchronized (listeners) {
                    for(MessageListener listener : listeners){
                        listener.onMessageStatusUpdate();
                    }
                }
            }

        });
    }

    public void sendText(String text, String username) {
        EMMessage message = EMMessage.createTxtSendMessage(text, username);
        sendEMMessage(message);
    }

    public void sendImage(String path, String username){
        EMMessage message = EMMessage.createImageSendMessage(path, false, username);
        sendEMMessage(message);
    }

    public void sendAudio(String path, int length, String username){
        EMMessage message = EMMessage.createVoiceSendMessage(path, length, username);
        sendEMMessage(message);
    }

    public void sendLocation(double latitude, double longitude, String locationAddress, String username) {
        EMMessage message = EMMessage.createLocationSendMessage(latitude, longitude, locationAddress, username);
        sendEMMessage(message);
    }

    public void sendFile(String filePath, String username) {
        EMMessage message = EMMessage.createFileSendMessage(filePath, username);
        sendEMMessage(message);
    }

    public void sendText(String text, String username, MessageAttributes param) {
        EMMessage message = EMMessage.createTxtSendMessage(text, username);
        message = attachAttributesToEMMessage(message, param);
        sendEMMessage(message);
    }

    public void sendImage(String path, String username, MessageAttributes param){
        EMMessage message = EMMessage.createImageSendMessage(path, false, username);
        message = attachAttributesToEMMessage(message, param);
        sendEMMessage(message);
    }

    public void sendAudio(String path, int length, String username, MessageAttributes param){
        EMMessage message = EMMessage.createVoiceSendMessage(path, length, username);
        message = attachAttributesToEMMessage(message, param);
        sendEMMessage(message);
    }

    public void sendLocation(double latitude, double longitude, String locationAddress, String username, MessageAttributes param) {
        EMMessage message = EMMessage.createLocationSendMessage(latitude, longitude, locationAddress, username);
        message = attachAttributesToEMMessage(message, param);
        sendEMMessage(message);
    }

    public void sendFile(String filePath, String username, MessageAttributes param) {
        EMMessage message = EMMessage.createFileSendMessage(filePath, username);
        message = attachAttributesToEMMessage(message, param);
        sendEMMessage(message);
    }

    private EMMessage attachAttributesToEMMessage(EMMessage message, MessageAttributes param){
        if (param != null){
            VisitorInfo visitorInfo = param.getVisitorInfo();
            AgentIdentityInfo agentIdentityInfo = param.getAgentIdentityInfo();
            QueueIdentityInfo queueIdentityInfo = param.getQueueIdentityInfo();
            if (queueIdentityInfo != null && !TextUtils.isEmpty(queueIdentityInfo.getString())){
                attachAttributeToEMMessage(message, QueueIdentityInfo.PARENT_NAME, QueueIdentityInfo.NAME, queueIdentityInfo.getString());
            }
            if (agentIdentityInfo != null && !TextUtils.isEmpty(agentIdentityInfo.getString())){
                attachAttributeToEMMessage(message, AgentIdentityInfo.PARENT_NAME, agentIdentityInfo.NAME, agentIdentityInfo.getString());
            }
            if (visitorInfo != null && visitorInfo.getContent() != null){
                attachAttributeToEMMessage(message, VisitorInfo.PARENT_NAME, visitorInfo.NAME, visitorInfo.getContent());
            }
        }
        return message;
    }

    /**
     *
     * @param message
     * @param parentName
     * @param name
     * @param jsonObj
     */
    private void attachAttributeToEMMessage(EMMessage message, String parentName, String name, JSONObject jsonObj){
        JSONObject parentJson = null;
        try {
            parentJson = message.getJSONObjectAttribute(parentName);
        } catch (HyphenateException e) {
        }
        if (parentJson == null){
            parentJson = new JSONObject();
        }
        try {
            parentJson.put(name, jsonObj);
            message.setAttribute(VisitorInfo.PARENT_NAME, parentJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void attachAttributeToEMMessage(EMMessage message, String parentName, String name, String content){
        JSONObject parentJson = null;
        try {
            parentJson = message.getJSONObjectAttribute(parentName);
        } catch (HyphenateException e) {
        }
        if (parentJson == null){
            parentJson = new JSONObject();
        }
        try {
            parentJson.put(name, content);
            message.setAttribute(VisitorInfo.PARENT_NAME, parentJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 异步发送消息 如果是语音,图片类有附件的消息, sdk会自动上传附件
     *
     * @param msg 待发送消息对象
     */
    public void sendMessage(Message msg){
        EMMessage message = msg.getEMMessage();
        sendEMMessage(message);
    }

    public void sendMessage(Message msg, MessageAttributes param){
        EMMessage message = msg.getEMMessage();
        message = attachAttributesToEMMessage(message, param);
        sendEMMessage(message);
    }

    /**
     * 保存用户app生成的消息,比如系统提示 消息会存到内存中的conversation 和数据库
     * CMD类型数据不保存本地
     * @param msg  待存储的消息
     */
    public void saveMessage(Message msg){
        EMMessage message = msg.getEMMessage();
        saveEMMessage(message);
    }

    public void reSendMessage(Message msg) {
        msg.getEMMessage().setStatus(EMMessage.Status.CREATE);
        sendMessage(msg);
    }

    public void addMessageListener(MessageListener listener) {
        if (!listeners.contains(listener)){
            listeners.add(listener);
        }
    }

    public void removeMessageListener(MessageListener listener){
        if (listeners.contains(listener)){
            listeners.remove(listener);
        }
    }

    public void loadConversations(){
        _manager.loadAllConversations();
    }

    public void clearConversation(String username) {
        _manager.deleteConversation(username, true);

    }

    public void removeMessageFromConversation(Message msg){
        _manager.getConversation(msg.getTo())
                .removeMessage(msg.getMsgId());
    }

    /**
     * 获取指定ID的消息对象
     * @param msgId 消息ID
     * @return
     */
    public Message getMessage(String msgId) {
        EMMessage message = _manager.getMessage(msgId);
        return Message.createMessage(message);
    }

    /**
     * 根据用户ID获取会话
     * @param id IM服务号
     * @return
     */
    public Conversation getConversation(String id) {
        EMConversation emConversation = _manager.getConversation(id, EMConversation.EMConversationType.Chat, true);
        if(emConversation == null)
            return null;
        else
            return new Conversation(emConversation);
    }

    /**
     * 判断是否是免打扰的消息,如果是app中应该不要给用户提示新消息
     *
     * @param msg
     * @return
     */
    public boolean isSilentMessage(Message msg) {
        return _manager.isSlientMessage(msg.getEMMessage());
    }

    /**
     * 下载消息的附件，未成功下载的附件，可调用此方法再次下载
     * @param msg
     */
    public void downloadAttachment(Message msg) {
        _manager.downloadAttachment(msg.getEMMessage());
    }

    /**
     * 下载消息的缩略图
     * @param msg
     */
    public void downloadThumbnail(Message msg) {
        _manager.downloadThumbnail(msg.getEMMessage());
    }

    /**
     * 从环信服务器下载文件
     *
     * @param remoteUrl 服务器上的远程文件
     * @param localFilePath 本地要生成的文件
     * @param headers Http Request Header
     * @param callback CallbackW
     */
    public void downloadFile(final String remoteUrl, final String localFilePath, final Map<String, String> headers, final Callback callback){
        _manager.downloadFile(remoteUrl, localFilePath, headers, new EMCallBack() {
            @Override
            public void onSuccess() {
                if (callback != null){
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(int i, String s) {
                if (callback != null){
                    callback.onError(i, s);
                }
            }

            @Override
            public void onProgress(int i, String s) {
                if (callback != null){
                    callback.onProgress(i, s);
                }
            }
        });
    }



    public void setMessageListened(Message msg) {
        _manager.setMessageListened(msg.getEMMessage());
    }

    private void sendEMMessage(EMMessage message){
        message.setChatType(EMMessage.ChatType.Chat);
        _manager.sendMessage(message);
        for(MessageListener listener : listeners){
            listener.onMessageSent();
        }
    }

    private void saveEMMessage(EMMessage message){
        message.setChatType(EMMessage.ChatType.Chat);
        if (message.getMsgId() == null){
            message.setMsgId(UUID.randomUUID().toString());
        }
        if (message.getMsgTime() == 0){
            message.setMsgTime(System.currentTimeMillis());
        }
        message.setStatus(EMMessage.Status.SUCCESS);
        _manager.saveMessage(message);
    }

    private List<Message> toMessages(List<EMMessage> messages) {
        List<Message> msgs = new ArrayList<Message>();
        for(EMMessage message : messages) {
            msgs.add(Message.createMessage(message));
        }
        return msgs;
    }

    /**
     * 获取未读消息计数
     * @return
     */
    public int getUnreadMsgsCount(){
       return _manager.getUnreadMsgsCount();
    }

    /**
     * 把所有的会话都设置为已读
     */
    public void markAllConversationsAsRead(){
        _manager.markAllConversationsAsRead();
    }

    public interface MessageListener {
        public void onMessage(List<Message> msgs);
        public void onCmdMessage(List<Message> msgs);
        public void onMessageSent();
        public void onMessageStatusUpdate();
    }
}