/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.easemob.helpdeskdemo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.easemob.helpdeskdemo.Constant;

public class HelpDeskPreferenceUtils {

    /**
     * 保存Preference的name
     */
    public static final String PREFERENCE_NAME = "appkeyInfo";

    private static SharedPreferences mSharedPreferences;
    private static HelpDeskPreferenceUtils mPreferenceUtils;
    private static SharedPreferences.Editor editor;

    private String SHARED_KEY_SETTING_CUSTOMER_APPKEY = "shared_key_setting_customer_appkey";
    private String SHARED_KEY_SETTING_CUSTOMER_ACCOUNT = "shared_key_setting_customer_account";

    private String SHARED_KEY_SETTING_CURRENT_NICK = "shared_key_setting_current_nick";
    private String SHARED_KEY_SETTING_TENANT_ID = "shared_key_setting_tenant_id";
    private String SHARED_KEY_CURRENT_SESSION_USER = "";


    private HelpDeskPreferenceUtils(Context cxt) {
        mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
    }

    public static HelpDeskPreferenceUtils getInstance(Context cxt) {
        if (mPreferenceUtils == null) {
            mPreferenceUtils = new HelpDeskPreferenceUtils(cxt);
        }
        return mPreferenceUtils;
    }

    public void setSettingCustomerAppkey(String appkey) {
        editor.putString(SHARED_KEY_SETTING_CUSTOMER_APPKEY, appkey);
        editor.commit();
    }

    public String getSettingCustomerAppkey() {
        return mSharedPreferences.getString(SHARED_KEY_SETTING_CUSTOMER_APPKEY, Constant.DEFAULT_COSTOMER_APPKEY);
    }

    public void setSettingCustomerAccount(String account) {
        editor.putString(SHARED_KEY_SETTING_CUSTOMER_ACCOUNT, account);
        editor.commit();
    }

    public String getSettingCustomerAccount() {
        return mSharedPreferences.getString(SHARED_KEY_SETTING_CUSTOMER_ACCOUNT, Constant.DEFAULT_COSTOMER_ACCOUNT);
    }

    public void setSettingCurrentNick(String nick) {
        editor.putString(SHARED_KEY_SETTING_CURRENT_NICK, nick);
        editor.commit();
    }

    public String getSettingCurrentNick() {
        return mSharedPreferences.getString(SHARED_KEY_SETTING_CURRENT_NICK, "");
    }

    public String getCurrentSessionUser() {
        return mSharedPreferences.getString(SHARED_KEY_CURRENT_SESSION_USER, "");
    }

    public void saveCurrentSessionUser(String userName) {
        editor.putString(SHARED_KEY_CURRENT_SESSION_USER, userName);
        editor.commit();
    }

    public String getSettingTenantId() {
        return mSharedPreferences.getString(SHARED_KEY_SETTING_TENANT_ID, Constant.DEFAULT_TENANT_ID);
    }

    public void setSettingTenantId(String tenantId) {
        if (!TextUtils.isEmpty(tenantId)) {
            editor.putString(SHARED_KEY_SETTING_TENANT_ID, tenantId);
            editor.commit();
        }
    }

}
