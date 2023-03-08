package com.hyphenate.helpdesk.videokit.board;

import com.google.gson.Gson;

import org.json.JSONObject;

public class WhiteObject {

    static Gson gson = new Gson();

    @Override
    public String toString() {
        return gson.toJson(this);
    }

    public JSONObject toJSON() {
        try {
            JSONObject jsonObject = new JSONObject(this.toString());
            return jsonObject;
        } catch (Exception e) {
            return new JSONObject();
        }
    }
}