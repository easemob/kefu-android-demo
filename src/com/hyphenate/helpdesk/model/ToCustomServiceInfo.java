package com.hyphenate.helpdesk.model;

import org.json.JSONObject;

/**
 */
public class ToCustomServiceInfo extends Content {

    static public final String PARENT_NAME = MessageHelper.TAG_WEICHAT;
    static public final String NAME = "ctrlArgs";
    static public final String TYPE = "ctrlType";
    static final String TYPE_VALUE = "TransferToKfHint";

    public ToCustomServiceInfo() {
        super();
    }

    public ToCustomServiceInfo(JSONObject jsonObj) {
        super(jsonObj);
    }

    public String getName() {
        return NAME;
    }

    public String getParentName() {
        return PARENT_NAME;
    }

    public String getId() {
        return get("id");
    }

    public String getServiceSessionId() {
        return get("serviceSessionId");
    }

    public String getLable() {
        return get("label");
    }

    public String getCtrlType() {
        return get("ctrlType");
    }


    public boolean isToCustomServiceMessage() {
        if (content.has("ctrlType") && !content.isNull("ctrlType")) {

        }
        String ctrlType = getCtrlType();
        if (ctrlType != null && ctrlType.equals("TransferToKfHint")) {
            return true;
        }
        return false;
    }


}
