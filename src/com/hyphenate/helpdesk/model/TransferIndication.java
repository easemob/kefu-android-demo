package com.hyphenate.helpdesk.model;


import com.hyphenate.helpdesk.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class TransferIndication extends CompositeContent {

    private static final String TAG = "TransferIndication";
    static public final String PARENT_NAME = MessageHelper.TAG_WEICHAT;
    private AgentInfo agentInfo;
    private Event event;

    TransferIndication() {
        agentInfo = ContentFactory.createAgentInfo(null);
        event = ContentFactory.createEvent(null);
        fillContents();
    }

    TransferIndication(JSONObject jsonObj) {
        String key;
        try {
            key = ContentFactory.createAgentInfo(null).getName();
            agentInfo = ContentFactory.createAgentInfo(jsonObj.getJSONObject(key));
            key = ContentFactory.createEvent(null).getName();
            event = ContentFactory.createEvent(jsonObj.getJSONObject(key));
        } catch (JSONException ex) {
            ex.printStackTrace();
            Log.e(TAG, ex == null ? "null" : ex.getMessage());
        }
        fillContents();
    }

    public AgentInfo getAgentInfo() {
        return agentInfo;
    }


    public String getEventName() {
        if (event != null)
            return event.getEventName();
        else
            return null;
    }

    public String getEventObj() {
        if (event != null)
            return event.getEventObj();
        else
            return null;
    }


    public String getParentName() {
        return PARENT_NAME;
    }

    @Override
    protected void fillContents() {
        if (agentInfo != null)
            contents.add(agentInfo);
        if (event != null)
            contents.add(event);
    }

}


class Event extends Content {
    static public final String PARENT_NAME = MessageHelper.TAG_WEICHAT;
    static public final String NAME = "event";

    Event() {
        super();
    }

    Event(JSONObject jsonObj) {
        super(jsonObj);
    }


    public String getName() {
        return NAME;
    }

    public String getParentName() {
        return PARENT_NAME;
    }

    public String getEventName() {
        return get("eventName");
    }

    String getEventObj() {
        return get("eventObj");
    }

}
