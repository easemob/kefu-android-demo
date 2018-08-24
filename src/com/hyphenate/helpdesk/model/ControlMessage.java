package com.hyphenate.helpdesk.model;


import com.hyphenate.helpdesk.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class ControlMessage extends CompositeContent {

    private static final String TAG = "ControlMessage";
    public static final String PARENT_NAME = MessageHelper.TAG_WEICHAT;
    public static final String TYPE_EVAL_REQUEST = "inviteEnquiry";
    public static final String TYPE_EVAL_RESPONSE = "enquiry";
    public static final String TYPE_TRANSFER_TO_AGENT = "TransferToKfHint";
    private ControlType type = null;
    private ControlArguments arguments = null;

    ControlMessage() {
        type = ContentFactory.createControlType(null);
        arguments = ContentFactory.createControlArguments(null);
        fillContents();
    }

    ControlMessage(JSONObject jsonObj) {
        String key;
        try {
            key = ContentFactory.createControlType(null).getName();
            if (jsonObj.has(key)) {
                type = ContentFactory.createControlType(jsonObj.getString(key));
            }
            key = ContentFactory.createControlArguments(null).getName();
            if (jsonObj.isNull(key)) {
                arguments = ContentFactory.createControlArguments(null);
            } else {
                arguments = ContentFactory.createControlArguments(jsonObj.getJSONObject(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e == null ? "null" : e.getMessage());
        }
        fillContents();
    }


    public String getControlType() {
        if (type != null && !type.equals("null"))
            return type.getString();
        else
            return null;
    }

    public void setControlType(String ctrlType) {
        if (type == null) {
            type = ContentFactory.createControlType(null);
        }
        type.setString(ctrlType);
    }

    public String getId() {
        return getArgumentsValue("id");
    }

    public String getServiceSessionId() {
        return getArgumentsValue("serviceSessionId");
    }

    public String getLabel() {
        return getArgumentsValue("label");
    }

    public String getInviteId() {
        return getArgumentsValue("inviteId");
    }

    public String getDetail() {
        return getArgumentsValue("detail");
    }

    public void setDetail(String detail) {
        arguments.set("detail", detail);
    }

    public String getSummary() {
        return getArgumentsValue("summary");
    }

    public void setSummary(String summary) {
        arguments.set("summary", summary);
    }

    private String getArgumentsValue(String key) {
        if (arguments != null)
            return arguments.get(key);
        else
            return null;
    }


    public String getParentName() {
        return PARENT_NAME;
    }

    @Override
    protected void fillContents() {
        if (type != null)
            contents.add(type);
        if (arguments != null)
            contents.add(arguments);
    }

}

class ControlType extends Content {

    static public final String PARENT_NAME = MessageHelper.TAG_WEICHAT;
    static public final String NAME = "ctrlType";

    ControlType() {
        super();
    }

    ControlType(String value) {
        super(value);
    }


    public String getName() {
        return NAME;
    }

    public String getParentName() {
        return PARENT_NAME;
    }


}

class ControlArguments extends Content {

    static public final String PARENT_NAME = MessageHelper.TAG_WEICHAT;
    static public final String NAME = "ctrlArgs";

    ControlArguments() {
        super();
    }

    ControlArguments(JSONObject jsonObj) {
        super(jsonObj);
    }


    public String getName() {
        return NAME;
    }

    public String getParentName() {
        return PARENT_NAME;
    }


}