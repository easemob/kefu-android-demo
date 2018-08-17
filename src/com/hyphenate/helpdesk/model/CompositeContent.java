package com.hyphenate.helpdesk.model;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

public abstract class CompositeContent {

    protected Collection<Content> contents = new ArrayList<Content>();

    abstract public String getParentName();

    public boolean isNull() {
        return contents.size() == 0;
    }


    CompositeContent() {
    }

    CompositeContent(JSONObject obj) {
    }

    Collection<Content> getContents() {
        return contents;
    }

    protected abstract void fillContents();
}
