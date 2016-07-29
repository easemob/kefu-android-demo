package com.easemob.helpdeskdemo.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.bottomnavigation.BottomNavigation;
import com.easemob.bottomnavigation.OnBottomNavigationSelectedListener;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.DemoHelper;
import com.easemob.helpdeskdemo.R;

import java.util.List;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity implements EMEventListener, OnBottomNavigationSelectedListener {
    private ShopFragment shopFragment;
    private SettingFragment settingFragment;
    private TicketListFragment ticketListFragment;
    private Fragment[] fragments;
    private int currentTabIndex = 0;
    private MyConnectionListener connectionListener = null;
    private BottomNavigation mBottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            currentTabIndex = savedInstanceState.getInt("selectedIndex", 0);
        }
        setContentView(R.layout.em_activity_main);
        init();
    }

    private void init() {
        mBottomNav = $(R.id.bottom_navigation);
        mBottomNav.setBottomNavigationSelectedListener(this);
        shopFragment = new ShopFragment();
        ticketListFragment = new TicketListFragment();
        settingFragment = new SettingFragment();
        fragments = new Fragment[]{shopFragment, ticketListFragment, settingFragment};
        // 把shopFragment设为选中状态
        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
        trx.add(R.id.fragment_container, shopFragment)
                .add(R.id.fragment_container, ticketListFragment)
                .add(R.id.fragment_container, settingFragment)
                .hide(shopFragment)
                .hide(ticketListFragment)
                .hide(settingFragment).show(fragments[currentTabIndex]);
        trx.commit();

        //注册一个监听连接状态的listener
        connectionListener = new MyConnectionListener();
        EMChatManager.getInstance().addConnectionListener(connectionListener);

        //内部测试方法，请忽略
        registerInternalDebugReceiver();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedIndex", currentTabIndex);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentTabIndex = savedInstanceState.getInt("selectedIndex", 0);
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
            trx.show(fragments[index]).commit();
        }
        currentTabIndex = index;
    }

    public class MyConnectionListener implements EMConnectionListener {

        @Override
        public void onConnected() {

        }

        @Override
        public void onDisconnected(final int error) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        //账号被移除
                        DemoHelper.getInstance().logout(true, null);
                        if (ChatActivity.activityInstance != null) {
                            ChatActivity.activityInstance.finish();
                        }
                    } else if (error == EMError.CONNECTION_CONFLICT) {
                        //账号在其他地方登录
                        DemoHelper.getInstance().logout(true, null);
                        if (ChatActivity.activityInstance != null) {
                            ChatActivity.activityInstance.finish();
                        }
                    } else {
                        //连接不到服务器


                    }

                }
            });
        }

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
    protected void onResume() {
        super.onResume();
        DemoHelper.getInstance().pushActivity(this);
        //register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(this,
                new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage,
                        EMNotifierEvent.Event.EventOfflineMessage});
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 把此activity 从foreground activity 列表里移除
        DemoHelper.getInstance().popActivity(this);
        EMChatManager.getInstance().unregisterEventListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectionListener != null) {
            EMChatManager.getInstance().removeConnectionListener(connectionListener);
        }
        try {
            unregisterReceiver(internalDebugReceiver);
        } catch (Exception e) {
        }
    }

    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage:
                EMMessage message = (EMMessage) event.getData();
                //提示新消息
                DemoHelper.getInstance().getNotifier().onNewMsg(message);
                break;
            case EventOfflineMessage:
                //处理离线消息
                List<EMMessage> messages = (List<EMMessage>) event.getData();
                //消息提醒或只刷新UI
                DemoHelper.getInstance().getNotifier().onNewMesg(messages);
                break;
            default:
                break;
        }

    }

    private BroadcastReceiver internalDebugReceiver;

    /**
     * 内部测试代码，开发者请忽略
     */
    private void registerInternalDebugReceiver() {
        internalDebugReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                DemoHelper.getInstance().logout(true, null);
                if (ChatActivity.activityInstance != null) {
                    ChatActivity.activityInstance.finish();
                }
            }
        };
        IntentFilter filter = new IntentFilter(getPackageName() + ".em_internal_debug");
        registerReceiver(internalDebugReceiver, filter);
    }

}
