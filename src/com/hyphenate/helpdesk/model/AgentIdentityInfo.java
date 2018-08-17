package com.hyphenate.helpdesk.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.hyphenate.helpdesk.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class AgentIdentityInfo extends Content implements Parcelable {
    private static final String TAG = AgentIdentityInfo.class.getSimpleName();

    static public final String PARENT_NAME = MessageHelper.TAG_WEICHAT;
    static public final String NAME = "agentUsername";

    public AgentIdentityInfo() {
        super();
    }

    public AgentIdentityInfo(String value) {
        super(value);
    }


    protected AgentIdentityInfo(Parcel in) {
        stringContent = in.readString();
        String contentStr = in.readString();
        if (!TextUtils.isEmpty(contentStr)) {
            try {
                content = new JSONObject(contentStr);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, e == null ? "" : e.getMessage());
            }
        }

    }

    public static final Creator<AgentIdentityInfo> CREATOR = new Creator<AgentIdentityInfo>() {
        @Override
        public AgentIdentityInfo createFromParcel(Parcel in) {
            return new AgentIdentityInfo(in);
        }

        @Override
        public AgentIdentityInfo[] newArray(int size) {
            return new AgentIdentityInfo[size];
        }
    };

    public String getName() {
        return NAME;
    }

    public AgentIdentityInfo agentName(String name) {
        setString(name);
        return this;
    }

    public String getParentName() {
        return PARENT_NAME;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stringContent);
        if (content != null) {
            dest.writeString(content.toString());
        } else {
            dest.writeString(null);
        }

    }
}