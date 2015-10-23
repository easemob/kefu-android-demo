/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.easemob.helpdeskdemo.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.EMMessage.Type;
import com.easemob.exceptions.EaseMobException;
import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.R;

public class SatisfactionActivity extends BaseActivity {

	private String msgId = "";
	private RatingBar ratingBar = null;
	private Button btnSubmit = null;
	private EditText etContent = null;
	private ProgressDialog pd = null;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_satisfaction);
		msgId = getIntent().getStringExtra("msgId");
		initView();
	}

	private void initView() {
		ratingBar = (RatingBar) findViewById(R.id.ratingBar1);
		btnSubmit = (Button) findViewById(R.id.submit);
		etContent = (EditText) findViewById(R.id.edittext);
		btnSubmit.setOnClickListener(new MyClickListener());
	}

	class MyClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			final EMMessage message = EMChatManager.getInstance().getMessage(msgId);
			try {
				final JSONObject jsonObj = message.getJSONObjectAttribute(Constant.WEICHAT_MSG);
				JSONObject jsonArgs = jsonObj.getJSONObject("ctrlArgs");
				final EMMessage sendMessage = EMMessage.createSendMessage(Type.TXT);
				String ratingBarValue = String.valueOf(ratingBar.getSecondaryProgress());
				jsonArgs.put("summary", ratingBarValue);
				jsonArgs.put("detail", etContent.getText().toString());
				
				JSONObject jsonSend = jsonObj;
				jsonSend.put("ctrlType", "enquiry");
				sendMessage.setAttribute(Constant.WEICHAT_MSG, jsonSend);
				sendMessage.setReceipt(message.getFrom());
				sendMessage.addBody(new TextMessageBody(""));
				pd = new ProgressDialog(SatisfactionActivity.this);
				pd.setMessage("请稍等...");
				pd.show();

				EMChatManager.getInstance().sendMessage(sendMessage, new EMCallBack() {

					@Override
					public void onSuccess() {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								if (pd != null && pd.isShowing()) {
									pd.dismiss();
								}
								EMChatManager.getInstance().getConversation(message.getFrom())
										.removeMessage(sendMessage.getMsgId());
								try {
									jsonObj.put("enable", true);
								} catch (JSONException e) {
									e.printStackTrace();
								}
								message.setAttribute(Constant.WEICHAT_MSG, jsonObj);
								EMChatManager.getInstance().updateMessageBody(message);
								setResult(RESULT_OK);
								finish();
							}
						});

					}

					@Override
					public void onProgress(int arg0, String arg1) {

					}

					@Override
					public void onError(int arg0, String arg1) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								if (pd != null && pd.isShowing()) {
									pd.dismiss();
								}
								Toast.makeText(getApplicationContext(), "提交失败", Toast.LENGTH_SHORT).show();
							}
						});
					}
				});
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (EaseMobException e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (pd != null && pd.isShowing()) {
			pd.dismiss();
		}
	}

}
