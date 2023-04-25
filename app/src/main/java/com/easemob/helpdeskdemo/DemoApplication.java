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

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.easemob.veckit.utils.AppStateVecCallback;
import com.heytap.msp.push.HeytapPushManager;
import com.hyphenate.helpdesk.easeui.util.EaseUiStateUtils;
import com.hyphenate.helpdesk.videokit.uitls.AppStateCecCallback;
import com.hyphenate.push.EMPushHelper;
import com.hyphenate.push.EMPushType;
import com.hyphenate.push.PushListener;
import com.hyphenate.util.EMLog;
import com.tencent.bugly.crashreport.CrashReport;

public class DemoApplication extends Application {

    public void onCreate() {
        super.onCreate();

        AppStateVecCallback.init(this);
        AppStateCecCallback.init(this);
        EaseUiStateUtils.getEaseUiStateUtils().init(this);

        Preferences.init(this);
        DemoHelper.getInstance().init(this);
        //OPPO SDK升级到2.1.0后需要进行初始化
        HeytapPushManager.init(this, true);
        // 请确保环信SDK相关方法运行在主进程，子进程不会初始化环信SDK（该逻辑在EaseUI.java中）
        if(isMainProcess(this)){
            EMPushHelper.getInstance().setPushListener(new PushListener() {
                @Override
                public void onError(EMPushType pushType, long errorCode) {
                    // TODO: 返回的errorCode仅9xx为环信内部错误，可从EMError中查询，其他错误请根据pushType去相应第三方推送网站查询。
                    EMLog.e("PushClient", "Push client occur a error: " + pushType + " - " + errorCode);
                }
            });
        }


        //注册Bugly Crash统计，用户可忽略
        CrashReport.initCrashReport(getApplicationContext(), "675c7d4ed4", false);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    public boolean isMainProcess(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return context.getApplicationInfo().packageName.equals(appProcess.processName);
            }
        }
        return false;
    }


}
