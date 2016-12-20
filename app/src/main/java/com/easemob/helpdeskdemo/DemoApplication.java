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

import com.uuzuche.lib_zxing.activity.ZXingLibrary;

public class DemoApplication extends Application {

    public void onCreate() {
        super.onCreate();

        Preferences.init(this);
        DemoHelper.getInstance().init(this);

        //通过二维码扫描关联，只为测试用，APP中可以去掉
        ZXingLibrary.initDisplayOpinion(this);
    }

}
