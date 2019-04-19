
## 环信客服SDK (Android版)


## Introduction


### 开发工具

> **Android Studio**


----

## 目录

* [入门指南](#Getting_started_guide)
    1.[build.gradle配置](#Guide_build_gradle)
    2.[初始化](#Guide_init)
    3.[注册](#Guide_register)
    4.[登录](#Guide_login)
    5.[退出](#Guide_logout)
    6.[会话](#Guide_Chat)
    7.[日志](#Guide_Log)
    8.[登录状态](#Guide_Login_Status)
* [高级选项](#Advanced_Option)



#### <A NAME="Guide_build_gradle"></A>添加依赖，在app的build.gradle 中dependencies中加入如下(添加后需同步gradle)：
```
android{
   ......
   defaultConfig {
     ndk {
        //选择要添加的对应cpu类型的.so库
        abiFilters 'armeabi', 'arm64-v8a'
        //还可以添加 'armeabi-v7a', 'x86'
     }
   }
}


dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    //环信客服SDK
    compile 'com.hyphenate:kefu-easeui-android:latest.release' //或者 compile 'com.hyphenate:kefu-easeui-android:1.1.9'
    //EaseUI中 头像获取用到了glide，请添加glide库
    compile 'com.github.bumptech.glide:glide:4.7.0' //其他版本也可以
    //EaseUI中，fragment用到了android-support-v4包
    compile 'com.android.support:support-v4:27.1.1' //其他版本也可以

}
```
**注意** 如果在添加以上abiFilter配置后AndroidStudio有如下提示：
```
NDK integration is deprecated in the current plugin. Consider trying the new experimental plugin.
```

则需要在Project根目录的gradle.properties文件中添加:
```
android.useDeprecatedNdk=true
```

#### <A NAME="Guide_build_gradle"></A>初始化
>初始化需要在Application中调用，ChatClient的其他方法也都需要在他后面调用
>例如：DebugMode、自定义通知栏、消息通知等

```
ChatClient.Options options = new ChatClient.Options();
options.setAppkey("Your appkey");(必填项;)//appkey获取地址：console.easemob.com
options.setTenantId("Your tenantId");(必填项;)//tenantId获取地址：kefu.easemob.com

// Kefu SDK 初始化
if (!ChatClient.getInstance().init(this, options)){
    return;
}
// Kefu EaseUI的初始化
UIProvider.getInstance().init(this);
//后面可以设置其他属性
```
```
//Kefu sdk 初始化简写方式：
  ChatClient.getInstance().init(this, new ChatClient.Options().setAppkey("zdxd#ksf").setTenantId("35"));
```

#### <A NAME="Guide_Log"></A>设置调试模式
```
// 设置为true后，将打印日志到logcat, 发布APP时应关闭该选项
ChatClient.getInstance().setDebugMode(true|false);
```

#### <A NAME="Guide_register"></A>注册

>注册建议在服务端创建，而不要放到APP中，可以在登录自己APP时从返回的结果中获取环信账号再登录环信服务器

```
ChatClient.getInstance().createAccount("username", "password", new Callback(){});

//ErrorCode:
Error.NETWORK_ERROR 网络不可用
Error.USER_ALREADY_EXIST  用户已存在
Error.USER_AUTHENTICATION_FAILED 无开放注册权限（后台管理界面设置[开放|授权]）
Error.USER_ILLEGAL_ARGUMENT 用户名非法

```

#### <A NAME="Guide_login"></A>登录

```
ChatClient.getInstance().login("username", "password", new Callback(){});

```

#### <A NAME="Guide_Chat"></A>打开会话页面

```
Intent intent = new IntentBuilder({Activity}.this)
						.setServiceIMNumber("客服关联的IM服务号")
						.build();
				startActivity(intent);
```


#### <A NAME="Guide_Login_Status"></A>判断是否已经登录

```
if(ChatClient.getInstance().isLoggedInBefore()){
    //已经登录，可以直接进入会话界面
}else{
    //未登录，需要登录后，再进入会话界面
}

```


#### <A NAME="Guide_logout"></A>登出
>登出后则无法收到客服发来的消息

```
ChatClient.getInstance().logout(new Callback(){});
```


### <A NAME="Advanced_Option"></A>高级选项

#### 添加小米推送

* [申请推送证书并添加后台](#MiPush_Console)
* [AndroidManifest.xml配置](#MiPush_AndroidManifest)
* [添加Mipush.jar到项目的libs文件夹中](#MiPush_jar)
* [Application初始化中option配置](#MiPush_Option)

#### <A NAME="MiPush_Console"></A>申请推送证书并添加后台

>进入[小米推送后台](http://dev.xiaomi.com/mipush/xmpush/app/applist?userId=913566583),创建一个应用，完成后得到AppId、AppKey、AppSecret。
>登录[环信管理后台](http://console.easemob.com/),选择你的应用->选择推送证书->Xiaomi->新增证书。
>证书名称为从小米推送后台得到的AppID,证书密钥为：AppSecret,以及填上你当前应用的包名,点击上传即可.

####  <A NAME="MiPush_AndroidManifest"></A>AndroidManifest.xml配置
```
<manifest>
     ...
     <!--例如: com.easemob.helpdeskdemo.permission.MIPUSH_RECEIVE -->
    <permission
        android:name="你的包名.permission.MIPUSH_RECEIVE"
        android:protectionLevel="signature" />
    <uses-permission android:name="你的包名.permission.MIPUSH_RECEIVE" />

	<application>
	...
	<service
            android:name="com.xiaomi.mipush.sdk.PushMessageHandler"
            android:enabled="true"
            android:exported="true" />
        <service
            android:name="com.xiaomi.mipush.sdk.MessageHandleService"
            android:enabled="true" />

        <receiver
            android:name="com.hyphenate.chat.EMMipushReceiver"
            android:exported="true" >
            <intent-filter>
                <action android:name="com.xiaomi.mipush.RECEIVE_MESSAGE" />
            </intent-filter>
            <intent-filter>
                <action android:name="com.xiaomi.mipush.MESSAGE_ARRIVED" />
            </intent-filter>
            <intent-filter>
                <action android:name="com.xiaomi.mipush.ERROR" />
            </intent-filter>
        </receiver>
	...
	</application>
</mainfest>

```

#### <A NAME="MiPush_Option"></A>Application初始化中option配置
```
ChatClient.Options options = new ChatClient.Options();
options.setAppkey("Your appkey");//appkey获取地址：console.easemob.com
options.setTenantId("Your tenantId");//tenantId获取地址：kefu.easemob.com
options.setMIPushConfig("mipushAppId", "mipushAppkey");
// Huanxin Kefu SDK 初始化
if (!ChatClient.getInstance().init(this, options)){
    return;
}
//后面可以设置其他属性

```

#### 添加网络监听,可以显示当前是否连接服务器

```
ChatClient.getInstance().addConnectionListener(new ChatClient.ConnectionListener() {
			@Override
			public void onConnected() {
                   //成功连接到服务器
			}

			@Override
			public void onDisconnected(int errorcode) {

			}
		});

```
#### 添加消息监听

```
ChatClient.getInstance().getChat().addMessageListener(new ChatManager.MessageListener() {
            @Override
            public void onMessage(List<Message> list) {
                //收到普通消息
            }

            @Override
            public void onCmdMessage(List<Message> list) {
                 //收到命令消息，命令消息不存数据库，一般用来作为系统通知，例如留言评论更新，
                 //会话被客服接入，被转接，被关闭提醒
            }

            @Override
            public void onMessageStatusUpdate() {
            //消息的状态修改，一般可以用来刷新列表，显示最新的状态

            }

            @Override
            public void onMessageSent() {
            //发送消息后，会调用，可以在此刷新列表，显示最新的消息

            }
        });
```

#  其他更多属性请进入[官网文档](http://docs.easemob.com/cs/300visitoraccess/androidsdk)查询

