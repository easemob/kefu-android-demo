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
package com.easemob.helpdeskdemo.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.IdRes;

import com.hyphenate.helpdesk.easeui.UIProvider;
import com.hyphenate.helpdesk.easeui.ui.BaseActivity;

/**
 * 如果不继承BaseActivity而继承默认的Activity需要在onResume添加如下代码:
 * <code>
 * // onresume时，取消notification显示
 * UIProvider.getInstance().getNotifier().reset();
 * </code>
 */
public class DemoBaseActivity extends BaseActivity{
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		UIProvider.getInstance().pushActivity(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		UIProvider.getInstance().popActivity(this);
	}
	/**
	 * 通过xml查找相应的ID，通用方法
	 * @param id
	 * @param <T>
	 * @return
	 */
	protected <T extends View> T $(@IdRes int id) {
		return (T) findViewById(id);
	}
}
