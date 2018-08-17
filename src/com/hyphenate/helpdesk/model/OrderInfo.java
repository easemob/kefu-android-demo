package com.hyphenate.helpdesk.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.hyphenate.helpdesk.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderInfo extends Content implements Parcelable {
    private static final String TAG = "OrderInfo";

    static public final String PARENT_NAME = MessageHelper.TAG_MSGTYPE;
    static public final String NAME = "order";

    public OrderInfo() {
        super();
    }

    public OrderInfo(JSONObject jsonObj) {
        super(jsonObj);
    }

    protected OrderInfo(Parcel in) {
        stringContent = in.readString();
        String jsonStr = in.readString();
        try {
            content = new JSONObject(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e == null ? "null" : e.getMessage());
        }

    }

    public static final Creator<OrderInfo> CREATOR = new Creator<OrderInfo>() {
        @Override
        public OrderInfo createFromParcel(Parcel in) {
            return new OrderInfo(in);
        }

        @Override
        public OrderInfo[] newArray(int size) {
            return new OrderInfo[size];
        }
    };

    public String getName() {
        return NAME;
    }

    public String getParentName() {
        return PARENT_NAME;
    }

    public OrderInfo title(String title) {
        return set("title", title);
    }

    public String getTitle() {
        return get("title");
    }

    public OrderInfo orderTitle(String orderTitle) {
        return set("order_title", orderTitle);
    }

    public String getOrderTitle() {
        return get("order_title");
    }

    public OrderInfo price(String price) {
        return set("price", price);
    }

    public String getPrice() {
        return get("price");
    }

    public OrderInfo desc(String desc) {
        return set("desc", desc);
    }

    public String getDesc() {
        return get("desc");
    }

    public OrderInfo imageUrl(String imageUrl) {
        return set("img_url", imageUrl);
    }

    public String getImageUrl() {
        return get("img_url");
    }

    public OrderInfo itemUrl(String itemUrl) {
        return set("item_url", itemUrl);
    }

    public String getItemUrl() {
        return get("item_url");
    }

    OrderInfo set(String name, String value) {
        return (OrderInfo) super.set(name, value);
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