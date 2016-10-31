package com.easemob.helpdeskdemo;

import android.text.TextUtils;

import com.hyphenate.helpdesk.model.AgentIdentityInfo;
import com.hyphenate.helpdesk.model.ContentFactory;
import com.hyphenate.helpdesk.model.OrderInfo;
import com.hyphenate.helpdesk.model.QueueIdentityInfo;
import com.hyphenate.helpdesk.model.VisitorInfo;
import com.hyphenate.helpdesk.model.VisitorTrack;

/**
 * 对轨迹跟踪的消息操作 此类不是必须，只是为了演示和初始化一些数据
 */
public class MessageHelper {

	public static final String IMAGE_URL_1 = "http://o8ugkv090.bkt.clouddn.com/em_one.png";
	public static final String IMAGE_URL_2 = "http://o8ugkv090.bkt.clouddn.com/em_two.png";
	public static final String IMAGE_URL_3 = "http://o8ugkv090.bkt.clouddn.com/em_three.png";
	public static final String IMAGE_URL_4 = "http://o8ugkv090.bkt.clouddn.com/em_four.png";


	public static VisitorInfo createVisitorInfo() {
		VisitorInfo info = ContentFactory.createVisitorInfo(null);
		info.nickName(Preferences.getInstance().getNickName())
		    .name(Preferences.getInstance().getUserName())
		    .qq("10000")
		    .companyName("环信")
		    .description("")
		    .email("abc@123.com");
		return info;
	}




	public static VisitorTrack createVisitorTrack(int index) {
		VisitorTrack track = ContentFactory.createVisitorTrack(null);
		switch(index) {
		case 3:
			track.title("test_track1")
                 .price("￥235")
                 .desc("假两件衬衣+V领毛衣上衣")
                 .imageUrl(IMAGE_URL_3)
                 .itemUrl("http://www.baidu.com");
			break;
		case 4:
			track.title("test_track2")
					.price("￥230")
					.desc("插肩棒球衫外套")
            .     imageUrl(IMAGE_URL_4)
                 .itemUrl("http://www.baidu.com");
			break;
		}
		return track;
	}
	
	public static OrderInfo createOrderInfo( int index) {
		OrderInfo info = ContentFactory.createOrderInfo(null);
		switch(index) {
		case 1:
			info.title("test_order1")
			    .orderTitle("订单号：7890")
			    .price("￥128")
			    .desc("2015早春新款高腰复古牛仔裙")
			    .imageUrl(IMAGE_URL_1)
			    .itemUrl("http://www.baidu.com");
			break;
		case 2:
			info.title("test_order2")
		        .orderTitle("订单号：7890")
		        .price("￥518")
		        .desc("露肩名媛范套装")
		        .imageUrl(IMAGE_URL_2)
		        .itemUrl("http://www.baidu.com");
			break;

		}
		return info;
		
	}
	
	public static AgentIdentityInfo createAgentIdentity(String agentName) {
		if (TextUtils.isEmpty(agentName)){
			return null;
		}
		AgentIdentityInfo info = ContentFactory.createAgentIdentityInfo(null);
		info.agentName(agentName);
		return info;
	}
	
	public static QueueIdentityInfo createQueueIdentity(String queueName) {
		if (TextUtils.isEmpty(queueName)){
			return null;
		}
		QueueIdentityInfo info = ContentFactory.createQueueIdentityInfo(null);
		info.queueName(queueName);
		return info;
	}
}
