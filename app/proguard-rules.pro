# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/liyuzhao/mysoft/androidadt/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


#mipush
-keep class com.xiaomi.push.** {*;}
-dontwarn com.xiaomi.push.**
#-dontwarn com.xiaomi.push.service.a.a
-keepclasseswithmembernames class com.xiaomi.**{*;}
-keep public class * extends com.xiaomi.mipush.sdk.PushMessageReceiver

-dontwarn com.hyphenate.**


# Keep GSON stuff
-keep class com.google.gson.** { *; }

-keep class com.hyphenate.helpdesk.easeui.ui.** { *; }

-keep class com.hyphenate.helpdesk.videokit.** { *; }

#gson
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.* { *; }
-keep class com.google.gson.examples.android.model.* { *; }
-keep class com.google.gson.* { *;}


#不混淆org.apache.http.legacy.jar
-dontwarn android.net.compatibility.**
-dontwarn android.net.http.**
-dontwarn com.android.internal.http.multipart.**
-dontwarn org.apache.commons.**
-dontwarn org.apache.http.**
-keep class org.apache.http.**{*;}
-keep class android.net.compatibility.**{*;}
-keep class android.net.http.**{*;}
-keep class com.android.internal.http.multipart.**{*;}
-keep class org.apache.commons.**{*;}


# Keep Retrofit
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.** *;
}
-keepclassmembers class * {
    @retrofit.** *;
}

-keepclassmembers class **.R$* {
  public static <fields>;
}


-keepclassmembers class * extends android.webkit.WebChromeClient{
		public void openFileChooser(...);
}


# easemob 3.x
-keep class com.hyphenate.** {*;}
-dontwarn  com.hyphenate.**
# 环信 push
-dontwarn com.hyphenate.push.***
-keep class com.hyphenate.push.*** {*;}

# Huawei push
-ignorewarning
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class com.hianalytics.android.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}

-keep class com.huawei.gamebox.plugin.gameservice.**{*;}

-keep public class com.huawei.android.hms.agent.** extends android.app.Activity { public *; protected *; }
-keep interface com.huawei.android.hms.agent.common.INoProguard {*;}
-keep class * extends com.huawei.android.hms.agent.common.INoProguard {*;}

# 音视频 声网
-keep class io.agora.**{*;}
# 电子白板
-keep class com.herewhite.** { *; }

-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

# 二维码
-keep class cn.bertsir.zbar.** {*;}



