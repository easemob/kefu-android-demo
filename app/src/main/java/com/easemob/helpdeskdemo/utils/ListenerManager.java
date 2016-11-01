package com.easemob.helpdeskdemo.utils;

import com.easemob.helpdeskdemo.interfaces.IListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 添加此类的缘由是:为了通知留言界面的刷新
 *
 * 用观察者模式代替繁重的广播通知刷新界面
 * 用法:
 * <p>
 *     // 注册
 *     ListenerManager.getInstance().registerListener(this);
 *     // 注销
 *     ListenerManager.getInstance().unRegisterListener(this);
 *     // 发送广播
 *     ListenerManager.getInstance().sendBroadCast("大家能收到我的信息吗");
 *
 * </p>
 *
 * 用于通知 界面刷新
 */
public class ListenerManager {

    /**
     * 单例模式
     */
    public static ListenerManager instance;

    /**
     * 注册的接口集合,发送广播的时候就能收到
     */
    private List<IListener> iListenerList = new CopyOnWriteArrayList<IListener>();

    /**
     * 获取单例对象
     * @return
     */
    public static ListenerManager getInstance(){
        if (instance == null){
            instance = new ListenerManager();
        }
        return instance;
    }

    /**
     * 注册监听
     * @param iListener
     */
    public void registerListener(IListener iListener){
        if (!iListenerList.contains(iListener)){
            iListenerList.add(iListener);
        }
    }


    /**
     * 注销监听
     */
    public void unRegisterListener(IListener iListener){
        if (iListenerList.contains(iListener)){
            iListenerList.remove(iListener);
        }
    }

    /**
     * 发送广播
     * @param event
     */
    public void sendBroadCast(String event, Object obj) {
        for (IListener iListener :
                iListenerList) {
            iListener.notifyEvent(event, obj);
        }
    }


}
