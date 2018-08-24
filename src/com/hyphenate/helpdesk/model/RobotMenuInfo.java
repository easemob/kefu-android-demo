package com.hyphenate.helpdesk.model;


import com.hyphenate.helpdesk.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

public class RobotMenuInfo extends Content {

    private static final String TAG = "RobotMenuInfo";

    static public final String PARENT_NAME = MessageHelper.TAG_MSGTYPE;
    static public final String NAME = "choice";
    static final String NAME_ITEMS = "items";
    static final String NAME_LIST = "list";

    public RobotMenuInfo() {
        super();
    }

    public RobotMenuInfo(JSONObject jsonObj) {
        super(jsonObj);
    }


    public String getName() {
        return NAME;
    }

    public String getParentName() {
        return PARENT_NAME;
    }

    public String getTitle() {
        return get("title");
    }

    public Collection<Item> getItems() {
        Collection<JSONObject> objArray = getObjectArray(NAME_ITEMS);
        if (objArray == null){
            return null;
        }
        Collection<Item> items = new ArrayList<Item>();
        Item item;
        for (JSONObject obj : objArray) {
            item = createItem(obj);
            if (item != null) {
                items.add(item);
            }
        }
        return items;
    }

    public Collection<String> getList(){
        return getStringArray(NAME_LIST);
    }

    private Item createItem(JSONObject obj) {
        Item item = null;
        try {
            item = new Item(obj.getString("id"), obj.getString("name"));
            item.setContent(obj);

        } catch (JSONException ex) {
            ex.printStackTrace();
            Log.e(TAG, ex == null ? "null" : ex.getMessage());
        }

        return item;
    }


    public class Item {
        private String id;
        private String name;
        private JSONObject jsonObj;

        Item(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public void setContent(JSONObject jsonObj) {
            this.jsonObj = jsonObj;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String toString() {
            return jsonObj.toString();
        }

    }

}
