package com.hyphenate.helpdesk.model;


import com.hyphenate.helpdesk.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

abstract public class Content {
    private static final String TAG = "Content";
    protected JSONObject content = null;
    protected String stringContent = null;

    Content() {
        content = new JSONObject();
    }

    Content(JSONObject jsonObject) {
        content = jsonObject;
    }

    Content(String value) {
        stringContent = value;
    }


    Content set(String name, String value) {
        try {
            if (content != null) {
                content.put(name, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e == null ? "null" : e.getMessage());
        }
        return this;
    }

    Content set(String name, Collection<String> values) {
        try {
            if (content != null) {
                content.put(name, new JSONArray(values));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e == null ? "null" : e.getMessage());
        }
        return this;
    }

    String get(String name) {
        if (content == null || !content.has(name) || content.isNull(name)) {
            return null;
        }
        try {
            return content.getString(name);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e == null ? "null" : e.getMessage());
        }
        return null;
    }

    boolean has(String key) {
        if (content == null) {
            return false;
        }
        if (content.has(key) && !content.isNull(key)) {
            return true;
        }
        return false;
    }

//    boolean isNull(String name) {
//        if (content == null) {
//            return true;
//        }
//        if (!content.isNull(name)) {
//            return false;
//        }
//        return true;
//    }


    Collection<JSONObject> getObjectArray(String name) {
        try {
            JSONArray jsonArray = content.optJSONArray(name);
            if (jsonArray != null) {
                Collection<JSONObject> objArray = new ArrayList<JSONObject>();
                for (int index = 0; index < jsonArray.length(); index++) {
                    objArray.add(jsonArray.getJSONObject(index));
                }
                return objArray;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e == null ? "null" : e.getMessage());
        }

        return null;
    }

    Collection<String> getStringArray(String name) {
        try {
            JSONArray jsonArray = content.optJSONArray(name);
            if (jsonArray != null) {
                Collection<String> strArray = new ArrayList<String>();
                for (int index = 0; index < jsonArray.length(); index++) {
                    strArray.add(jsonArray.getString(index));
                }
                return strArray;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e == null ? "null" : e.getMessage());
        }
        return null;
    }

    public void setString(String value) {
        stringContent = value;
    }

    public String getString() {
        return stringContent;
    }

    public JSONObject getContent() {
        return content;
    }

    abstract public String getName();

    abstract public String getParentName();


}
