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
package com.easemob.helpdeskdemo.ui;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.hyphenate.helpdesk.Error;
import com.easemob.bottomnavigation.BottomNavigation;
import com.easemob.bottomnavigation.OnBottomNavigationSelectedListener;
import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.DemoHelper;
import com.easemob.helpdeskdemo.HMSPushHelper;
import com.easemob.helpdeskdemo.Preferences;
import com.easemob.helpdeskdemo.R;
import com.hyphenate.agora.FunctionIconItem;
import com.hyphenate.chat.AgoraMessage;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.ChatManager;
import com.hyphenate.chat.Message;
import com.hyphenate.chat.VecConfig;
import com.hyphenate.helpdesk.callback.ValueCallBack;
import com.hyphenate.helpdesk.easeui.runtimepermission.PermissionsManager;
import com.hyphenate.helpdesk.easeui.runtimepermission.PermissionsResultAction;
import com.hyphenate.helpdesk.easeui.util.FlatFunctionUtils;
import com.hyphenate.helpdesk.videokit.permission.FloatWindowManager;
import com.hyphenate.util.EasyUtils;

import java.io.File;
import java.util.List;


public class MainActivity extends DemoBaseActivity implements OnBottomNavigationSelectedListener {

    private Fragment shopFragment = null;
    private Fragment settingFragment = null;
    private Fragment ticketListFragment = null;
    private Fragment conversationsFragment = null;
    private Fragment[] fragments;
    private int currentTabIndex = 0;
    private MyConnectionListener connectionListener = null;
    private BottomNavigation mBottomNav;

    private View mContent;
    private WindowManager mWm;
    private Point mPoint;

    private final ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            DemoHelper.sNavHeight = getNav(mWm, mContent, mPoint);
            mContent.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
        }
    };

    private int getNav(WindowManager wm, View content, Point point){
        Display display = wm.getDefaultDisplay();
        display.getRealSize(point);
        if (content.getBottom() == 0){
            return 0;
        }
        return point.y - content.getBottom();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            try {
                assert pm != null;
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                    startActivity(intent);
                }
            } catch (Exception ignored) {
                //锤子手机会报找不到这个Activity
                //android.content.ActivityNotFoundException: No Activity found to handle Intent { act=android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS dat=package:com.easemob.helpdeskdemo }
            }

        }

        mContent = getWindow().getDecorView().findViewById(android.R.id.content);
        mWm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        mPoint = new Point();
        mContent.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);

        // 获取可用功能
        getTenantIdFunctionIcons();

        setContentView(R.layout.em_activity_main);
        String customerAccount = Preferences.getInstance().getCustomerAccount();

        if (!TextUtils.isEmpty(customerAccount)){
            AgoraMessage.newAgoraMessage().setVecImServiceNumber(customerAccount);
            AgoraMessage.newAgoraMessage().setCecImServiceNumber(customerAccount);
        }

        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();

        if (savedInstanceState != null) {
            currentTabIndex = savedInstanceState.getInt("selectedIndex", 0);
            //Activity被杀死的时候，有些情况Fragment不被销毁
            if (shopFragment == null) {
                shopFragment = getSupportFragmentManager().findFragmentByTag("shopFragment");
                settingFragment = getSupportFragmentManager().findFragmentByTag("settingFragment");
                ticketListFragment = getSupportFragmentManager().findFragmentByTag("ticketListFragment");
                conversationsFragment = getSupportFragmentManager().findFragmentByTag("conversationsFragment");
            }
        }

        if (shopFragment == null) {
            shopFragment = new ShopFragment();
            trx.add(R.id.fragment_container, shopFragment, "shopFragment");
        }

        if (ticketListFragment == null) {
            ticketListFragment = new TicketListFragment();
            trx.add(R.id.fragment_container, ticketListFragment, "ticketListFragment");
        }

        if (conversationsFragment == null) {
            conversationsFragment = new ConversationListFragment();
            trx.add(R.id.fragment_container, conversationsFragment, "conversationsFragment");
        }

        if (settingFragment == null) {
            settingFragment = new SettingFragment();
            trx.add(R.id.fragment_container, settingFragment, "settingFragment");
        }

        fragments = new Fragment[]{shopFragment, ticketListFragment, conversationsFragment, settingFragment};

        // 把shopFragment设为选中状态
        trx.hide(settingFragment)
                .hide(ticketListFragment)
                .hide(conversationsFragment)
                .hide(shopFragment)
                .show(fragments[currentTabIndex])
                .commit();


        mBottomNav = $(R.id.bottom_navigation);
        mBottomNav.setBottomNavigationSelectedListener(this);
        //注册一个监听连接状态的listener
        connectionListener = new MyConnectionListener();
        ChatClient.getInstance().addConnectionListener(connectionListener);
        //6.0运行时权限处理，target api设成23时，demo这里做的比较简单，直接请求所有需要的运行时权限
        requestPermissions();


        // 检查华为推送服务
        HMSPushHelper.getInstance().getHMSToken(this);

        // 给新版vec呼叫页面设置用户名称
        VecConfig.newVecConfig().setUserName(Preferences.getInstance().getNickName());


        /*IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
            @Override
            public void binderDied() {
                Log.e("ooooooooooooooo","binderDied");
            }
        };


        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                try {
                    //注册死亡回调
                    service.linkToDeath(deathRecipient,0);
                    Log.e("ooooooooooooooo","onServiceConnected");
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.e("ooooooooooooooo","error = "+e.getMessage());
                }

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.e("ooooooooooooooo","onServiceDisconnected");
            }
        };
        Intent intent = new Intent(this, TestService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);*/
        

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!FloatWindowManager.getInstance().checkPermission(this)){
            FloatWindowManager.getInstance().applyPermission(this);
        }
    }

    private void getTenantIdFunctionIcons(){
        // 动态获取功能按钮，在视频页面使用到
        AgoraMessage.asyncGetTenantIdFunctionIcons(ChatClient.getInstance().tenantId(), new ValueCallBack<List<FunctionIconItem>>() {
            @Override
            public void onSuccess(List<FunctionIconItem> value) {
                FlatFunctionUtils.get().setIconItems(value);
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }

    @TargetApi(23)
    private void requestPermissions() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
            }

            @Override
            public void onDenied(String permission) {
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);

    }


    @Override
    public void onValueSelected(int index) {
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager()
                    .beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commitAllowingStateLoss();
        }
        currentTabIndex = index;
    }

    public class MyConnectionListener implements ChatClient.ConnectionListener {

        @Override
        public void onConnected() {

        }

        @Override
        public void onDisconnected(final int errorCode) {
            if (errorCode == Error.USER_NOT_FOUND || errorCode == Error.USER_LOGIN_ANOTHER_DEVICE
                    || errorCode == Error.USER_AUTHENTICATION_FAILED
                    || errorCode == Error.USER_REMOVED) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //demo中为了演示当用户被删除或者修改密码后验证失败,跳出会话界面
                        //正常APP应该跳到登录界面或者其他操作
                        if (ChatActivity.instance != null) {
                            ChatActivity.instance.finish();
                        }
                        ChatClient.getInstance().logout(false, null);
                    }
                });
            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void contactCustomer(View view) {
        switch (view.getId()) {
            case R.id.ll_setting_list_customer:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, LoginActivity.class);
                intent.putExtra(Constant.MESSAGE_TO_INTENT_EXTRA, Constant.MESSAGE_TO_DEFAULT);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectionListener != null) {
            ChatClient.getInstance().removeConnectionListener(connectionListener);
        }
        FlatFunctionUtils.get().clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DemoHelper.getInstance().pushActivity(this);
        ChatClient.getInstance().chatManager().addMessageListener(messageListener);
        DemoHelper.getInstance().showNotificationPermissionDialog();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedIndex", currentTabIndex);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentTabIndex = savedInstanceState.getInt("selectedIndex", 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ChatClient.getInstance().chatManager().removeMessageListener(messageListener);
        DemoHelper.getInstance().popActivity(this);

    }

    ChatManager.MessageListener messageListener = new ChatManager.MessageListener() {

        @Override
        public void onMessage(final List<Message> msgs) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    // 未读数可以显示在UI上
                    // int unreadMsgCount = ChatClient.getInstance().chatManager().getUnreadMsgsCount();
                    if (EasyUtils.isAppRunningForeground(MainActivity.this)) {
                        DemoHelper.getInstance().getNotifier().onNewMesg(msgs);
                    }


                    if (conversationsFragment != null) {
                        ((ConversationListFragment) conversationsFragment).refresh();
                    }
                }
            });
        }

        @Override
        public void onCmdMessage(List<Message> msgs) {

        }

        @Override
        public void onMessageStatusUpdate() {

        }

        @Override
        public void onMessageSent() {

        }
    };
}

