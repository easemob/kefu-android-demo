package com.easemob.veckit.bean;

import android.content.Context;

import com.google.gson.Gson;
import com.hyphenate.helpdesk.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class EntityBean {
    public String configId;
    public int tenantId;
    public String configName;
    public int associationId;
    public boolean isDefault;

    //public String configJson;
    private VideoStyleBean videoStyleBean;

    public EntityBean(String jsonStr) throws JSONException {
        JSONObject entity = new JSONObject(jsonStr);
        configId = entity.getString("configId");
        tenantId = entity.getInt("tenantId");
        configName = entity.getString("configName");
        associationId = entity.getInt("associationId");
        isDefault = entity.getBoolean("default");

        String json = entity.getString("configJson");
        String configJson = json.replace("\\", "");
        Gson gson = new Gson();
        videoStyleBean = gson.fromJson(configJson, VideoStyleBean.class);
    }

    public VideoStyleBean getVideoStyleBean(Context context) {
        if (videoStyleBean != null){
            return videoStyleBean;
        }
        return VideoStyleBean.create(context);
    }
}
