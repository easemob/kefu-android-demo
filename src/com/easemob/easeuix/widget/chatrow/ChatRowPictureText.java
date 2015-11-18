package com.easemob.easeuix.widget.chatrow;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.easeui.widget.chatrow.EaseChatRow;
import com.easemob.exceptions.EaseMobException;
import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.ui.ChatFragment;
import com.easemob.helpdeskdemo.ui.ContextMenuActivity;
import com.easemob.helpdeskdemo.utils.CommonUtils;

public class ChatRowPictureText extends EaseChatRow{

	ImageView mImageView;
	TextView mTextViewDes;
	TextView mTextViewprice;
	TextView mTV;
	TextView mChatTextView;
	
	public ChatRowPictureText(Context context, EMMessage message, int position, BaseAdapter adapter) {
		super(context, message, position, adapter);
	}

	@Override
	protected void onInflatView() {
		if (message.getStringAttribute(Constant.PICTURE_MSG, null) != null) {
			inflater.inflate(message.direct == EMMessage.Direct.RECEIVE ? R.layout.ease_row_received_message
					: R.layout.em_row_sent_picture_new, this);
		}
	}

	@Override
	protected void onFindViewById() {
		mTextViewDes = (TextView) findViewById(R.id.tv_send_desc);
		mTextViewprice = (TextView) findViewById(R.id.tv_send_price_new);
		mTV = (TextView) findViewById(R.id.tv_order);
		mImageView = (ImageView) findViewById(R.id.iv_sendPicture_add);
		mChatTextView = (TextView) findViewById(R.id.tv_chatcontent);
	}

	@Override
	protected void onUpdateView() {
		
	}

	@Override
	protected void onSetUpView() {
		TextMessageBody txtBody = (TextMessageBody) message.getBody();
		if(message.direct == EMMessage.Direct.RECEIVE){
			// 设置内容
			mChatTextView.setText(txtBody.getMessage());
			// 设置长按事件监听
			mChatTextView.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					activity.startActivityForResult(
							(new Intent(activity, ContextMenuActivity.class)).putExtra("position", position).putExtra("type",
									EMMessage.Type.TXT.ordinal()), ChatFragment.REQUEST_CODE_CONTEXT_MENU);
					return true;
				}
			});
			return;
		}
		String imageName = message.getStringAttribute("imageName", null);
		try {
			JSONObject jsonOrder = message.getJSONObjectAttribute("msgtype").getJSONObject("order");
			String item_url = jsonOrder.getString("item_url");
			String title = jsonOrder.getString("title");
			String price = jsonOrder.getString("price");
			String desc = jsonOrder.getString("desc");
			String img_url = jsonOrder.getString("img_url");
			if(desc == null){
				
			}else if(desc.equals("2015早春新款高腰复古牛仔裙")){
				mTextViewDes.setText("2015早春新款高腰复古牛仔裙");
				mTextViewprice.setText("￥128");
				mTV.setVisibility(View.VISIBLE);
				mTV.setText("订单号:123456");
				Bitmap newBitmap = CommonUtils.convertBitmap(((BitmapDrawable)context.getResources().getDrawable(R.drawable.em_one)).getBitmap(), CommonUtils.convertDip2Px(context, 100), CommonUtils.convertDip2Px(context, 120));
				mImageView.setImageBitmap(newBitmap);
			}else if(desc.equals("露肩名媛范套装")){
				mTextViewDes.setText("露肩名媛范套装");
				mTextViewprice.setText("￥518");
				mTV.setVisibility(View.VISIBLE);
				mTV.setText("订单号:7890");
				Bitmap newBitmap = CommonUtils.convertBitmap(((BitmapDrawable)context.getResources().getDrawable(R.drawable.em_two)).getBitmap(), CommonUtils.convertDip2Px(context, 100), CommonUtils.convertDip2Px(context, 120));
				mImageView.setImageBitmap(newBitmap);
				
			}else if(desc.equals("假两件衬衣+V领毛衣上衣")){
				mTextViewDes.setText("");
				mTextViewprice.setText("￥235");
				Bitmap newBitmap = CommonUtils.convertBitmap(((BitmapDrawable)context.getResources().getDrawable(R.drawable.em_three)).getBitmap(), CommonUtils.convertDip2Px(context, 100), CommonUtils.convertDip2Px(context, 120));
				mImageView.setImageBitmap(newBitmap);
			}else if(desc.equals("插肩棒球衫外套")){
				mTextViewDes.setText("");
				mTextViewprice.setText("￥162");
				Bitmap newBitmap = CommonUtils.convertBitmap(((BitmapDrawable)context.getResources().getDrawable(R.drawable.em_four)).getBitmap(), CommonUtils.convertDip2Px(context, 100), CommonUtils.convertDip2Px(context, 120));
				mImageView.setImageBitmap(newBitmap);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (EaseMobException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	protected void onBubbleClick() {
		
	}

}
