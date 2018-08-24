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
package com.easemob.helpdeskdemo;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

public class DemoApplication extends Application {

    public void onCreate() {
        super.onCreate();

        //初始化华为HMS推送服务
        HMSPushHelper.getInstance().initHMSAgent(this);

        Preferences.init(this);
        DemoHelper.getInstance().init(this);

        //注册Bugly Crash统计，用户可忽略
        CrashReport.initCrashReport(getApplicationContext(), "900012496", false);
    }
}
