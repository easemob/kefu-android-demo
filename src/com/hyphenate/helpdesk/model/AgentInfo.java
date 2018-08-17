package com.hyphenate.helpdesk.model;


import org.json.JSONObject;

public class AgentInfo extends Content {
    static public final String PARENT_NAME = MessageHelper.TAG_WEICHAT;
    static public final String NAME = "agent";

    public AgentInfo() {
        super();
    }

    public AgentInfo(JSONObject jsonObj) {
        super(jsonObj);
    }


    public String getName() {
        return NAME;
    }

    public String getParentName() {
        return PARENT_NAME;
    }

    public AgentInfo nickname(String nickname) {
        return set("userNickname", nickname);
    }

    public String getNickname() {
        return get("userNickname");
    }

    public AgentInfo avatar(String avatar) {
        return set("avatar", avatar);
    }

    public String getAvatar() {
        return get("avatar");
    }

    AgentInfo set(String name, String value) {
        return (AgentInfo) super.set(name, value);
    }


}