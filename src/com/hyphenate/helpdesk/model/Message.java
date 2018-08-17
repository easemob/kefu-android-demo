package com.hyphenate.helpdesk.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.helpdesk.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;

public class Message implements Parcelable, Cloneable {

    private static final String TAG = "Message";
    private EMMessage message;
//    private boolean persistent = true;
//    private Callback statusCallback = null;

    Message() {
    }

    protected Message(Parcel in) {
        message = in.readParcelable(EMMessage.class.getClassLoader());
//        persistent = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(message, flags);
//        dest.writeByte((byte) (persistent ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    static public Message createMessage(EMMessage msg) {
        Message message = new Message();
        message.setEMMessage(msg);
        return message;
    }

    static public Message createMessage() {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        message.addBody(new EMTextMessageBody(""));
        Message msg = createMessage(message);
        return msg;
    }

    static public Message createRecvMessage(String text) {
        EMMessage message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        if (text != null)
            message.addBody(new EMTextMessageBody(text));
        else
            message.addBody(new EMTextMessageBody(""));
        message.setTo(EMClient.getInstance().getCurrentUser());
        Message msg = createMessage(message);
        return msg;
    }

    public synchronized void addContent(Content content) {
        if (content == null)
            return;

        JSONObject container = MessageHelper.getContainerObject(this, content.getParentName());
        if (container == null){
            container = new JSONObject();
        }
        try {
            if (content.getString() == null) {
                if (content.getName() != null){
                    container.put(content.getName(), content.getContent());
                }
            } else {
                if (content.getName() != null) {
                    container.put(content.getName(), content.getString());
                }
            }
            if (content.getParentName() != null){
                message.setAttribute(content.getParentName(), container);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            Log.e(TAG, ex == null ? "null" : ex.getMessage());
        }
    }

    public synchronized void addContent(CompositeContent compositeContent) {
        Collection<Content> contents = compositeContent.getContents();
        if (contents != null && contents.size() > 0){
            for (Content c : contents) {
                addContent(c);
            }
        }
    }

    public String getMsgId() {
        return message.getMsgId();
    }

    public String getFrom() {
        return message.getFrom();
    }

    public void setFrom(String from) {
        message.setFrom(from);
    }

    public String getTo() {
        return message.getTo();
    }

    public void setTo(String to) {
        message.setTo(to);
    }


//    public synchronized void setPersitent(boolean persistent) {
//        this.persistent = persistent;
//    }
//
//    public synchronized void setStatusCallback(Callback callback) {
//        statusCallback = callback;
//        EMLog.e(TAG, "setStatusCallback:"+ statusCallback);
//    }

    public Type getType(){
        return Type.valueOf(message.getType().ordinal());
    }

    public Direct getDirect() {
        return Direct.valueOf(message.direct().ordinal());
    }

    public static enum Direct {
        SEND,
        RECEIVE;
        private Direct(){}
        public static Direct valueOf(int value){
            switch (value){
                case 0:
                    return SEND;
                case 1:
                    return RECEIVE;
                default:
                    return SEND;
            }
        }
    }


    public static enum Type {
        TXT,
        IMAGE,
        VIDEO,
        LOCATION,
        VOICE,
        FILE,
        CMD;

        private Type() {
        }

        public static Type valueOf(int value) {
            switch (value) {
                case 0:
                    return TXT;
                case 1:
                    return IMAGE;
                case 2:
                    return VIDEO;
                case 3:
                    return LOCATION;
                case 4:
                    return VOICE;
                case 5:
                    return FILE;
                case 6:
                    return CMD;
                default:
                    return TXT;
            }
        }

    }

    public static enum Status {
        SUCCESS,
        FAIL,
        INPROGRESS,
        CREATE;

        private Status() {
        }

        public static Status valueOf(int value) {
            switch (value) {
                case 0:
                    return SUCCESS;
                case 1:
                    return FAIL;
                case 2:
                    return INPROGRESS;
                case 3:
                    return CREATE;
                default:
                    return SUCCESS;
            }
        }

    }


    public Status getStatus() {
        return Status.valueOf(message.status().ordinal());
    }

    public Long getMsgTime() {
        return message.getMsgTime();
    }

    public boolean isAcked() {
        return message.isAcked();
    }

    public boolean isDelivered() {
        return message.isDelivered();
    }

    public boolean isListened() {
        return message.isListened();
    }

    public int getError() {
        return message.getError();
    }

    public void setListened(boolean isListened) {
        message.setListened(isListened);
    }

    public void setMessageStatusCallback(EMCallBack callback) {
        message.setMessageStatusCallback(callback);
    }

    public synchronized EMMessageBody getBody() {
        return message.getBody();
    }

    public int getProgress() {
        return message.progress();
    }

    public synchronized EMMessage getEMMessage() {
        return message;
    }

    void setEMMessage(EMMessage msg) {
        message = msg;
    }

    @Override
    public String toString() {
        return "message:" + message.toString();
    }
}
