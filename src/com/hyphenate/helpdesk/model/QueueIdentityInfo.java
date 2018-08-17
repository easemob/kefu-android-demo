package com.hyphenate.helpdesk.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.hyphenate.helpdesk.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class QueueIdentityInfo extends Content implements Parcelable {

    private static final String TAG = "QueueIdentityInfo";
    static public final String PARENT_NAME = MessageHelper.TAG_WEICHAT;
    static public final String NAME = "queueName";

    public QueueIdentityInfo() {
        super();
    }

    public QueueIdentityInfo(String value) {
        super(value);
    }

    protected QueueIdentityInfo(Parcel in) {
        stringContent = in.readString();
        String jsonStr = in.readString();
        if (jsonStr != null) {
            try {
                content = new JSONObject(jsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, e == null ? "null" : e.getMessage());
            }
        }
    }

    public static final Creator<QueueIdentityInfo> CREATOR = new Creator<QueueIdentityInfo>() {
        @Override
        public QueueIdentityInfo createFromParcel(Parcel in) {
            return new QueueIdentityInfo(in);
        }

        @Override
        public QueueIdentityInfo[] newArray(int size) {
            return new QueueIdentityInfo[size];
        }
    };

    public QueueIdentityInfo queueName(String name) {
        setString(name);
        return this;
    }

    public String getName() {
        return NAME;
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