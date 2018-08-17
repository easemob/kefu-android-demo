package com.hyphenate.helpdesk.util;


import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.helpdesk.model.MessageHelper;

import org.json.JSONObject;

import java.util.List;

public class DebugUtil {

    static String tag = "EMMESSAGE_PRINT";

    static public void printMessage(EMMessage msg) {
        JSONObject jsonObj1 = null;
        JSONObject jsonObj2 = null;
        try {

            jsonObj1 = msg.getJSONObjectAttribute(MessageHelper.TAG_MSGTYPE);

        }catch (HyphenateException ex) {
//			ex.printStackTrace();
        }
        try {
            jsonObj2 = msg.getJSONObjectAttribute(MessageHelper.TAG_WEICHAT);

        }catch (HyphenateException ex) {
//			ex.printStackTrace();
        }
        if(msg.getType() == EMMessage.Type.CMD) {
            Log.d(tag, "CMD Message and action is" + ((EMCmdMessageBody) msg.getBody()).action());
        }
        Log.d(tag, msg.toString());
        if(jsonObj1 != null)
            Log.d(tag, "attribute1 is : " + jsonObj1.toString());
        if(jsonObj2 != null)
            Log.d(tag, "attribute2 is : " + jsonObj2.toString());
    }

    static public void printMessageList(List<EMMessage> msgs) {
        for(EMMessage msg: msgs) {
            printMessage(msg);
        }
    }
}