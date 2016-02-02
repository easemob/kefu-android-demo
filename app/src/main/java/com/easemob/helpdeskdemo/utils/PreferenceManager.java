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
package com.easemob.helpdeskdemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
	/**
	 * 保存Preference的name
	 */
	public static final String PREFERENCE_NAME = "saveInfo";
	private static SharedPreferences mSharedPreferences;
	private static PreferenceManager mPreferencemManager;
	private static SharedPreferences.Editor editor;

	private static String SHARED_KEY_CURRENTUSER_USERNAME = "SHARED_KEY_CURRENTUSER_USERNAME";
	private static String SHARED_KEY_CURRENTUSER_PASSWORD = "SHARED_KEY_CURRENTUSER_USERNAME";
	
	
	private PreferenceManager(Context cxt) {
		mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		editor = mSharedPreferences.edit();
	}

	public static synchronized void init(Context cxt) {
		if (mPreferencemManager == null) {
			mPreferencemManager = new PreferenceManager(cxt);
		}
	}

	/**
	 * 单例模式，获取instance实例
	 * 
	 * @param cxt
	 * @return
	 */
	public synchronized static PreferenceManager getInstance() {
		if (mPreferencemManager == null) {
			throw new RuntimeException("please init first!");
		}

		return mPreferencemManager;
	}

	public void setCurrentUserName(String username) {
		editor.putString(SHARED_KEY_CURRENTUSER_USERNAME, username);
		editor.commit();
	}

	public String getCurrentUsername() {
		return mSharedPreferences.getString(SHARED_KEY_CURRENTUSER_USERNAME, null);
	}

	public void setCurrentUserPwd(String password){
		editor.putString(SHARED_KEY_CURRENTUSER_PASSWORD, password);
	}
	
	public String getCurrentUserPwd(){
		return mSharedPreferences.getString(SHARED_KEY_CURRENTUSER_PASSWORD, null);
	}
	
}
