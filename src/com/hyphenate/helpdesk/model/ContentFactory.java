package com.hyphenate.helpdesk.model;


import org.json.JSONObject;

public class ContentFactory {

    public static VisitorInfo createVisitorInfo(JSONObject obj) {
        if (obj == null)
            return new VisitorInfo();
        else
            return new VisitorInfo(obj);
    }

    public static VisitorTrack createVisitorTrack(JSONObject obj) {
        if (obj == null)
            return new VisitorTrack();
        else
            return new VisitorTrack(obj);
    }

    public static RobotMenuInfo createRobotMenuInfo(JSONObject obj) {
        if (obj == null)
            return new RobotMenuInfo();
        else
            return new RobotMenuInfo(obj);
    }

    public static OrderInfo createOrderInfo(JSONObject obj) {
        if (obj == null)
            return new OrderInfo();
        else
            return new OrderInfo(obj);
    }

    public static AgentInfo createAgentInfo(JSONObject obj) {
        if (obj == null)
            return new AgentInfo();
        else
            return new AgentInfo(obj);
    }

    public static TransferIndication createTransferIndication(JSONObject obj) {
        if (obj == null)
            return new TransferIndication();
        else
            return new TransferIndication(obj);

    }

    static Event createEvent(JSONObject obj) {
        if (obj == null)
            return new Event();
        else
            return new Event(obj);

    }

    public static ControlMessage createControlMessage(JSONObject obj) {
        if (obj == null)
            return new ControlMessage();
        else
            return new ControlMessage(obj);

    }

    static ControlType createControlType(String value) {
        if (value == null)
            return new ControlType();
        else
            return new ControlType(value);

    }

    static ControlArguments createControlArguments(JSONObject obj) {
        if (obj == null)
            return new ControlArguments();
        else
            return new ControlArguments(obj);
    }

    public static QueueIdentityInfo createQueueIdentityInfo(String value) {
        if (value == null)
            return new QueueIdentityInfo();
        else
            return new QueueIdentityInfo(value);
    }

    public static AgentIdentityInfo createAgentIdentityInfo(String value) {
        if (value == null)
            return new AgentIdentityInfo();
        else
            return new AgentIdentityInfo(value);
    }

    public static ToCustomServiceInfo createToCustomeServiceInfo(JSONObject obj) {
        if (obj == null)
            return new ToCustomServiceInfo();
        else
            return new ToCustomServiceInfo(obj);

    }

}