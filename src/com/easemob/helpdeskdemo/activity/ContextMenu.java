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

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.easemob.chat.EMMessage;
import com.easemob.helpdeskdemo.R;

public class ContextMenu extends BaseActivity {

	private int position;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int txtValue = EMMessage.Type.TXT.ordinal();
		int type = getIntent().getIntExtra("type", -1);
		if (type == EMMessage.Type.TXT.ordinal()) {
		    setContentView(R.layout.context_menu_for_text);
		} else if (type == EMMessage.Type.LOCATION.ordinal()) {
		    setContentView(R.layout.context_menu_for_location);
		} else if (type == EMMessage.Type.IMAGE.ordinal()) {
		    setContentView(R.layout.context_menu_for_image);
		} else if (type == EMMessage.Type.VOICE.ordinal()) {
		    setContentView(R.layout.context_menu_for_voice);
		} else if (type == EMMessage.Type.VIDEO.ordinal()) {
			setContentView(R.layout.context_menu_for_video);
		}
		position = getIntent().getIntExtra("position", -1);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	public void copy(View view){
		setResult(ChatActivity.RESULT_CODE_COPY, new Intent().putExtra("position", position));
		finish();
	}
	public void delete(View view){
		setResult(ChatActivity.RESULT_CODE_DELETE, new Intent().putExtra("position", position));
		finish();
	}
	public void forward(View view){
		setResult(ChatActivity.RESULT_CODE_FORWARD, new Intent().putExtra("position", position));
		finish();
	}
	
	public void open(View v){
	    setResult(ChatActivity.RESULT_CODE_OPEN, new Intent().putExtra("position", position));
        finish();
	}
	public void download(View v){
	    setResult(ChatActivity.RESULT_CODE_DWONLOAD, new Intent().putExtra("position", position));
        finish();
	}
	public void toCloud(View v){
	    setResult(ChatActivity.RESULT_CODE_TO_CLOUD, new Intent().putExtra("position", position));
        finish();
	}
	
}
