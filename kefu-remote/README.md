## 远程控制API (Android版)


### 开发工具

> **Android Studio**

主要参考类

`RemoteManager`//对外调用类

`CtrlManager`//远程控制管理类

`CtrlMouseWidget`//远程控制下，鼠标位置显示view

`SRManager`//屏幕捕获管理类

----

## 如何接收远程指令


#### <A NAME="Guide_build_gradle"></A>初始化
>初始化远程操作SDK

```
EMediaManager.initGlobal(context);
```


#### <A NAME="Guide_build_gradle"></A>注册一个EMConferenceListener监听，通过onCtrlMessage回调接收远程指令：

```
EMConferenceManager.getInstance().addConferenceListener(new EMConferenceListener() {
            @Override public void onMemberJoined(EMConferenceMember member) {
            
                            }

            @Override public void onMemberExited(EMConferenceMember member) {
                
            }

            @Override public void onStreamAdded(EMConferenceStream stream) {
            
                            }

            @Override public void onStreamRemoved(EMConferenceStream stream) {
                
            }

            @Override public void onStreamUpdate(EMConferenceStream stream) {
                
            }

            @Override public void onPassiveLeave(int error, String message) {
                
            }

            @Override public void onConferenceState(ConferenceState state, Object object) {
               
            }

            @Override public void onStreamStatistics(EMStreamStatistics statistics) {
               
            }

            @Override public void onStreamSetup(String streamId) {
            
                           }

            @Override public void onSpeakers(List<String> speakers) {
               
            }

            @Override public void onCtrlMessage(ConferenceState state, String arg1, String arg2, Object arg3) {
                if (state == ConferenceState.STATE_CTRL_MSG) {
                    //接收远程控制的请求指令
                    CtrlManager.getInstance().parseCtrlMsg(arg1, arg2, arg3);
                } 
            }
        });

```

**注意** 
`EMConferenceListener`是本地自定义监听，具体实现可参考`EMConferenceManager`


#### <A NAME="Guide_Log"></A>接收到指令在`CtrlManager（远程控制管理类）`中解析指令

主要方法调用：

`agreeRequestCtrl`//允许远程控制

`rejectRequestCtrl`//停止远程控制

`parseCtrlMsg`//在远程监听中对接收到的指令进行解析

`execAction`//根据远程发送指令进行相应的具体操作


#### <A NAME="Guide_register"></A>申请远程协助

参考`EMConferenceManager`类

>1.加入会议

`EMConferenceManager.getInstance().joinConference`

>2.开始推流，共享桌面

`EMConferenceManager.getInstance().publish`

EMConferenceListener回调中

>3.订阅

 `EMConferenceManager.getInstance().subscribe`

>4 更新订阅

`EMConferenceManager.getInstance().updateSubscribe`

>5.停止推流

`EMConferenceManager.getInstance().unpublish`

>6.取消订阅

`EMConferenceManager.getInstance().unsubscribe`