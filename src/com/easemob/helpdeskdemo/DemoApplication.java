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
package com.easemob.helpdeskdemo;

import com.hyphenate.helpdesk.ChatClient;
import com.hyphenate.helpdesk.ui.UIManager;

import android.app.Application;
import android.content.Context;

public class DemoApplication extends Application {

	public void onCreate() {
		super.onCreate();
		Preferences.init(this);
		ChatClient.Options options = ChatClient.getInstance().createOptions();
		options.setAppkey(Preferences.getInstance().getAppKey());
        ChatClient.getInstance().init((Context)this, options);
		UIManager.getInstance().init((Context)this);
	}
}
