package com.hyphenate.helpdesk.model;

public class MessageAttributes {

    private VisitorInfo visitorInfo;
    private AgentIdentityInfo agentIdentityInfo;
    private QueueIdentityInfo queueIdentityInfo;

    public MessageAttributes setVisitorInfo(VisitorInfo info) {
        this.visitorInfo = info;
        return this;
    }

    public MessageAttributes setAgentIdentityInfo(AgentIdentityInfo info) {
        this.agentIdentityInfo = info;
        return this;
    }

    public MessageAttributes setQueueIdentityInfo(QueueIdentityInfo info) {
        this.queueIdentityInfo = info;
        return this;
    }

    public VisitorInfo getVisitorInfo() {
        return visitorInfo;
    }


    public AgentIdentityInfo getAgentIdentityInfo() {
        return agentIdentityInfo;
    }

    public QueueIdentityInfo getQueueIdentityInfo() {
        return queueIdentityInfo;
    }

}
