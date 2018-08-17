package com.hyphenate.helpdesk;


import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.helpdesk.model.Message;

import java.util.ArrayList;
import java.util.List;

public class Conversation  {
    private EMConversation _conversation;

    Conversation(EMConversation conversation) {
        _conversation = conversation;
    }

    public String ConversationId() {
        return _conversation.conversationId();
    }

    public int getUnreadMsgCount() {
        return _conversation.getUnreadMsgCount();
    }

    public void markAllMessagesAsRead(){
        _conversation.markAllMessagesAsRead();
    }

    public int getAllMsgCount() {
        return _conversation.getAllMsgCount();
    }

    public List<Message> loadMoreMsgFromDB(String startMsgId, int pageSize) {
        return convertToMessages(_conversation.loadMoreMsgFromDB(startMsgId, pageSize));
    }

    public Message getMessage(String messageId, boolean markAsRead){
        return Message.createMessage(_conversation.getMessage(messageId, markAsRead));
    }

    public List<Message> loadMessages(List<String> msgIds){
        return convertToMessages(_conversation.loadMessages(msgIds));
    }

    public void markMessageAsRead(String messageId){
        _conversation.markMessageAsRead(messageId);
    }

    public List<Message> getAllMessages() {
        return convertToMessages(_conversation.getAllMessages());
    }

    public int getMessagePosition(Message message) {
        return _conversation.getMessagePosition(message.getEMMessage());
    }

    public String getUserName() {
        return _conversation.getUserName();
    }

    public void removeMessage(String messageId) {
        _conversation.removeMessage(messageId);
    }

    public Message getLastMessage() {
        return Message.createMessage(_conversation.getLastMessage());
    }

    public void clear() {
        _conversation.clear();
    }

    public void clearAllMessages() {
        _conversation.clearAllMessages();
    }

    public void insertMessage(Message msg) {
        _conversation.insertMessage(msg.getEMMessage());
    }

    private List<Message> convertToMessages(List<EMMessage> messages) {
        List<Message> msgs = new ArrayList<Message>();

        for(EMMessage emMsg : messages) {
            msgs.add(Message.createMessage(emMsg));
        }

        return msgs;
    }

}