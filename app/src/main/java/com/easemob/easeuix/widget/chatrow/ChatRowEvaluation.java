package com.easemob.easeuix.widget.chatrow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.easemob.chat.EMMessage;
import com.easemob.easeui.adapter.EaseMessageAdapter;
import com.easemob.easeui.widget.chatrow.EaseChatRow;
import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.DemoHelper;
import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.ui.ChatFragment;
import com.easemob.helpdeskdemo.ui.SatisfactionActivity;

import org.json.JSONObject;

public class ChatRowEvaluation extends EaseChatRow{
	
	Button btnEval;

	public ChatRowEvaluation(Context context, EMMessage message, int position, BaseAdapter adapter) {
		super(context, message, position, adapter);
	}

	@Override
	protected void onInflatView() {
		if (DemoHelper.getInstance().isEvalMessage(message)) {
			inflater.inflate(message.direct == EMMessage.Direct.RECEIVE ? R.layout.em_row_received_satisfaction
					: R.layout.em_row_sent_satisfaction, this);
		}
	}

	@Override
	protected void onFindViewById() {
		btnEval = (Button) findViewById(R.id.btn_eval);
		
	}

	@Override
	protected void onUpdateView() {
		if(adapter instanceof EaseMessageAdapter){
			((EaseMessageAdapter)adapter).refresh();
		}else {
			adapter.notifyDataSetChanged();
		}
		
	}

	@Override
	protected void onSetUpView() {
		try {
			final JSONObject jsonObj = message.getJSONObjectAttribute(Constant.WEICHAT_MSG);
			if(jsonObj.has("ctrlType")&&!jsonObj.isNull("ctrlType")){
				btnEval.setEnabled(true);
				btnEval.setText("立即评价");
				btnEval.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						((Activity)context).startActivityForResult(new Intent(context, SatisfactionActivity.class)
								.putExtra("msgId", message.getMsgId()),ChatFragment.REQUEST_CODE_EVAL);
					}
				});
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onBubbleClick() {
		
	}

}
