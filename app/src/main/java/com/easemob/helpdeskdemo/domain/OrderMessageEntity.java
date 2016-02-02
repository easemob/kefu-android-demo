package com.easemob.helpdeskdemo.domain;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * 用户订单消息 
 */
public class OrderMessageEntity {
	
	private int id;//id 是没有任何意义的，demo传这个id，只是为了知道选择的是哪张图片，应该显示哪张图片
	private String title;
	private String orderTitle;
	private String price;
	private String desc;
	private String imgUrl;
	private String itemUrl;

	public OrderMessageEntity(int id,String title, String orderTitle, String price, String desc, String imgUrl, String itemUrl) {
		this.id = id;
		this.title = title;
		this.orderTitle = orderTitle;
		this.price = price;
		this.desc = desc;
		this.imgUrl = imgUrl;
		this.itemUrl = itemUrl;

	}

	public String getTitle() {
		return title;
	}

	public String getOrderTitle() {
		return orderTitle;
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

	public int getId(){
		return id;
	}
	
	public JSONObject getJSONObject() {
		JSONObject jsonObject = new JSONObject();
		JSONObject jsonMsgType = new JSONObject();
		try {
			jsonObject.put("id", this.id);
			jsonObject.put("title", this.title);
			jsonObject.put("order_title", this.orderTitle);
			jsonObject.put("price", this.price);
			jsonObject.put("desc", this.desc);
			jsonObject.put("img_url", this.imgUrl);
			jsonObject.put("item_url", this.itemUrl);
			jsonMsgType.put("order", jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonMsgType;
	}

	
	
	public static OrderMessageEntity getEntityFromJSONObject(JSONObject jsonMsgType){
		try {
			JSONObject jsonOrder = jsonMsgType.getJSONObject("order");
			int id = jsonOrder.getInt("id");
			String title = jsonOrder.getString("title");
			String orderTitle = jsonOrder.getString("order_title");
			String price = jsonOrder.getString("price");
			String desc = jsonOrder.getString("desc");
			String imgUrl = jsonOrder.getString("img_url");
			String itemUrl = jsonOrder.getString("item_url");
			OrderMessageEntity entity = new OrderMessageEntity(id, title, orderTitle, price, desc, imgUrl, itemUrl);
			return entity;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}
