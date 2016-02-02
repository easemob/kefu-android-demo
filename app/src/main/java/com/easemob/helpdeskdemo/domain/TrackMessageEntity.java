package com.easemob.helpdeskdemo.domain;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用户轨迹消息
 */
public class TrackMessageEntity {

	private int id;
	private String title;
	private String price;
	private String desc;
	private String imgUrl;
	private String itemUrl;

	public TrackMessageEntity(int id, String title, String price, String desc, String imgUrl, String itemUrl) {
		this.id = id;
		this.title = title;
		this.price = price;
		this.desc = desc;
		this.imgUrl = imgUrl;
		this.itemUrl = itemUrl;
	}

	public int getId() {
		return id;
	}
	public String getTitle() {
		return title;
	}

	public String getPrice() {
		return price;
	}

	public String getDesc() {
		return desc;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public String getItemUrl() {
		return itemUrl;
	}

	public JSONObject getJSONObject() {
		JSONObject jsonMsgTrack = new JSONObject();
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("id", this.id);
			jsonObject.put("title", this.title);
			jsonObject.put("price", this.price);
			jsonObject.put("desc", this.desc);
			jsonObject.put("img_url", this.imgUrl);
			jsonObject.put("item_url", this.itemUrl);
			jsonMsgTrack.put("track", jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonMsgTrack;
	}
	
	public static TrackMessageEntity getEntityFromJSONObject(JSONObject jsonMsgType){
		try {
			JSONObject jsonTrack = jsonMsgType.getJSONObject("track");
			int id = jsonTrack.getInt("id");
			String title = jsonTrack.getString("title");
			String price = jsonTrack.getString("price");
			String desc = jsonTrack.getString("desc");
			String imgUrl = jsonTrack.getString("img_url");
			String itemUrl = jsonTrack.getString("item_url");
			TrackMessageEntity entity = new TrackMessageEntity(id, title, price, desc, imgUrl, itemUrl);
			return entity;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
		
	}

}
