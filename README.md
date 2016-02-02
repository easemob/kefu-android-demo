HuanXin-KefuDemo
=====

环信客服Demo是基于环信[easeui][1]库. 此Demo主要为了演示客服的一些功能。


集成配置
-------

Demo中appkey是在代码中设置，也可以在AndroidManifest.xml中设置。其中appkey的获得在[环信后台][2]。

获取Appkey后，需要在[环信客服平台][3]配置你获取的Appkey。位置在管理员模式-->渠道管理-->手机App。

此项目为Android Studio版本，由于Google已经放弃Eclipse，还是建议大家早日切换。

Demo中用到了环信SDK,获取从官网[下载][4]。SDK中含有libeasemobservice.so和easemobchat_x.x.x.jar 

为增加小米手机存活率，需要添加小米的SDK, MiPush_SDK_Client_x_x_x.jar 在代码中的libs文件夹内。


app打包混淆
------------
Depending on your proguard config and usage, you may need to include the following lines in your proguard.cfg:

```pro
-keep class com.easemob.** {*;}
-keep class org.jivesoftware.** {*;}
-keep class org.apache.** {*;}
-dontwarn  com.easemob.**
#如果使用easeui库，需要这么写
-keep class com.easemob.easeui.utils.EaseSmileUtils {*;}
-keep class net.java.sip.** {*;}
-keep class org.webrtc.voiceengine.** {*;}
```

Build
---------
clean 项目：./gradlew clean 

打包Debug版本的apk：./gradlew assembleDebug 打包出来的apk位于app->build->output->apk->app-debug.apk




[1]: https://github.com/easemob/easeui
[2]: http://console.easemob.com
[3]: https://kefu.easemob.com
[4]: https://www.easemob.com/downloads

