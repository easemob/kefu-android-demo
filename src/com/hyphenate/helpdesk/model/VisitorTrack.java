package com.hyphenate.helpdesk.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.hyphenate.helpdesk.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class VisitorTrack extends Content implements Parcelable{

    private static final String TAG = "VisitorTrack";
    static public final String PARENT_NAME = MessageHelper.TAG_MSGTYPE;
    static public final String NAME = "track";
    public VisitorTrack() {
        super();
    }

    public VisitorTrack(JSONObject jsonObj) {
        super(jsonObj);
    }

    protected VisitorTrack(Parcel in) {
        stringContent = in.readString();
        String jsonStr = in.readString();
        if(jsonStr != null){
            try {
                content = new JSONObject(jsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, e == null ? "null" : e.getMessage());
            }
        }
    }

    public static final Creator<VisitorTrack> CREATOR = new Creator<VisitorTrack>() {
        @Override
        public VisitorTrack createFromParcel(Parcel in) {
            return new VisitorTrack(in);
        }

        @Override
        public VisitorTrack[] newArray(int size) {
            return new VisitorTrack[size];
        }
    };

    public String getName() {
        return NAME;
    }

    public String getParentName() {
        return PARENT_NAME;
    }

    public VisitorTrack title(String title) {
        return set("title", title);
    }

    public String getTitle() {
        return get("title");
    }

    public VisitorTrack price(String price) {
        return set("price", price);
    }

    public String getPrice() {
        return get("price");
    }

    public VisitorTrack desc(String desc) {
        return set("desc", desc);
    }

    public String getDesc() {
        return get("desc");
    }

    public VisitorTrack imageUrl(String imageUrl) {
        return set("img_url", imageUrl);
    }

    public String getImageUrl() {
        return get("img_url");
    }

    public VisitorTrack itemUrl(String itemUrl) {
        return set("item_url", itemUrl);
    }

    public String getItemUrl() {
        return get("item_url");
    }

    VisitorTrack set(String name, String value) {
        return (VisitorTrack)super.set(name, value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stringContent);
        if(content != null){
            dest.writeString(content.toString());
        }else {
            dest.writeString(null);
        }

    }
}