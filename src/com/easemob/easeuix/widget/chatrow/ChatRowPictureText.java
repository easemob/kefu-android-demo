package com.easemob.easeuix.widget.chatrow;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.easeui.widget.chatrow.EaseChatRow;
import com.easemob.exceptions.EaseMobException;
import com.easemob.helpdeskdemo.DemoHelper;
import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.domain.OrderMessageEntity;
import com.easemob.helpdeskdemo.domain.TrackMessageEntity;
import com.easemob.helpdeskdemo.ui.ChatFragment;
import com.easemob.helpdeskdemo.ui.ContextMenuActivity;
import com.easemob.util.DensityUtil;
import com.easemob.util.ImageUtils;

public class ChatRowPictureText extends EaseChatRow {

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
		if (DemoHelper.getInstance().isPictureTxtMessage(message)) {
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
		if (message.direct == EMMessage.Direct.RECEIVE) {
			// 设置内容
			mChatTextView.setText(txtBody.getMessage());
			// 设置长按事件监听
			mChatTextView.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					activity.startActivityForResult(
							(new Intent(activity, ContextMenuActivity.class)).putExtra("position", position).putExtra(
									"type", EMMessage.Type.TXT.ordinal()), ChatFragment.REQUEST_CODE_CONTEXT_MENU);
					return true;
				}
			});
			return;
		}
		JSONObject jsonMsgType = null;
		Bitmap newBitmap = null;
		try {
			jsonMsgType = message.getJSONObjectAttribute("msgtype");
		} catch (EaseMobException e) {
			e.printStackTrace();
		}
		if (jsonMsgType == null) {

		} else if (jsonMsgType.has("order")) {
			OrderMessageEntity entity = OrderMessageEntity.getEntityFromJSONObject(jsonMsgType);
			mTextViewDes.setText(entity.getDesc());
			mTextViewprice.setText(entity.getPrice());
			mTV.setVisibility(View.VISIBLE);
			mTV.setText(entity.getOrderTitle());
			if (entity.getId() == 1) {
				newBitmap = ImageUtils.decodeScaleImage(context, R.drawable.em_one, DensityUtil.dip2px(context, 100),
						DensityUtil.dip2px(context, 120));
			} else if (entity.getId() == 2) {
				newBitmap = ImageUtils.decodeScaleImage(context, R.drawable.em_two, DensityUtil.dip2px(context, 100),
						DensityUtil.dip2px(context, 120));
			}
		} else if (jsonMsgType.has("track")) {
			TrackMessageEntity entity = TrackMessageEntity.getEntityFromJSONObject(jsonMsgType);
			mTextViewDes.setText(entity.getDesc());
			mTextViewprice.setText(entity.getPrice());
			if (entity.getId() == 3) {
				newBitmap = ImageUtils.decodeScaleImage(context, R.drawable.em_three, DensityUtil.dip2px(context, 100),
						DensityUtil.dip2px(context, 120));
			} else if (entity.getId() == 4) {
				newBitmap = ImageUtils.decodeScaleImage(context, R.drawable.em_four, DensityUtil.dip2px(context, 100),
						DensityUtil.dip2px(context, 120));
			}
		}
		if (newBitmap != null) {
			mImageView.setImageBitmap(newBitmap);
		}

	}

	@Override
	protected void onBubbleClick() {

	}

}
