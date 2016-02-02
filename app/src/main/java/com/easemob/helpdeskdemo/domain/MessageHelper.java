package com.easemob.helpdeskdemo.domain;

import org.json.JSONObject;

/**
 * 对轨迹跟踪的消息操作 此类不是必须，只是为了演示和初始化一些数据
 */
public class MessageHelper {

	public static JSONObject getMessageExtFromPicture(int index) {
		switch (index) {
		case 1:
			OrderMessageEntity entity1 = new OrderMessageEntity(1, "test_order1", "订单号：7890", "￥128",
					"2015早春新款高腰复古牛仔裙", "https://www.baidu.com/img/bdlogo.png", "http://www.baidu.com");
			return entity1.getJSONObject();
		case 2:
			OrderMessageEntity entity2 = new OrderMessageEntity(2, "test_order2", "订单号：7890", "￥518", "露肩名媛范套装",
					"https://www.baidu.com/img/bdlogo.png", "http://www.baidu.com");
			return entity2.getJSONObject();
		case 3:
			TrackMessageEntity entity3 = new TrackMessageEntity(3, "test_track1", "￥235", "假两件衬衣+V领毛衣上衣",
					"https://www.baidu.com/img/bdlogo.png", "http://www.baidu.com");
			return entity3.getJSONObject();
		case 4:
			TrackMessageEntity entity4 = new TrackMessageEntity(4, "test_track2", "￥162", "插肩棒球衫外套",
					"https://www.baidu.com/img/bdlogo.png", "http://www.baidu.com");
			return entity4.getJSONObject();
		}
		return null;
	}
	
	

}
