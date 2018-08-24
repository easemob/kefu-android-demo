package com.hyphenate.helpdesk.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.hyphenate.helpdesk.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;

public class VisitorInfo extends Content implements Parcelable{
    private static final String TAG = "VisitorInfo";

    static public final String PARENT_NAME = MessageHelper.TAG_WEICHAT;
    static public final String NAME = "visitor";

    public VisitorInfo() {
        super();
    }

    public VisitorInfo(JSONObject jsonObj) {
        super(jsonObj);
    }


    protected VisitorInfo(Parcel in) {
        stringContent = in.readString();
        String strContent = in.readString();
        if (strContent != null){
            try {
                content = new JSONObject(strContent);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, e == null ? "null" : e.getMessage());
            }
        }

    }

    public static final Creator<VisitorInfo> CREATOR = new Creator<VisitorInfo>() {
        @Override
        public VisitorInfo createFromParcel(Parcel in) {
            return new VisitorInfo(in);
        }

        @Override
        public VisitorInfo[] newArray(int size) {
            return new VisitorInfo[size];
        }
    };

    public String getName() {
        return NAME;
    }

    public String getParentName() {
        return PARENT_NAME;
    }

    public VisitorInfo name(String name) {
        return set("trueName", name);
    }

    public VisitorInfo qq(String qq) {
        return set("qq", qq);
    }

    public VisitorInfo companyName(String companyName) {
        return set("companyName", companyName);
    }

    public VisitorInfo nickName(String nickName) {
        return set("userNickname", nickName);
    }

    public VisitorInfo description(String description) {
        return set("description", description);
    }

    public VisitorInfo email(String email) {
        return set("email", email);
    }

    public VisitorInfo vip(Collection<String> tags) {
        return set("tags", tags);
    }

    VisitorInfo set(String name, String value) {
        return (VisitorInfo) super.set(name, value);
    }

    VisitorInfo set(String name, Collection<String> values) {
        return (VisitorInfo) super.set(name, values);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stringContent);
        if (content != null){
            dest.writeString(content.toString());
        }

    }
}