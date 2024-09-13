# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/Shared/resources/android-sdk-macosx/tools/proguard/proguard-android.txt
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
-ignorewarnings
-dontwarn java.lang.invoke**
-optimizationpasses 5 #proguard对你的代码进行迭代优化的次数，首先要明白optimization 会对代码进行各种优化，每次优化后的代码还可以再次优化，所以就产生了 优化次数的问题，这里面的 passes 应该翻译成 ‘次数’
-dontusemixedcaseclassnames #不使用大小写形式的混淆名
-dontskipnonpubliclibraryclasses #不跳过library的非public的类
-dontoptimize #不进行优化，优化可能会在某些手机上无法运行。
-dontpreverify #不进行预校验，该校验是java平台上的，对android没啥用处
-keepattributes Singature #避免混淆泛型
-keepattributes *Annotation* #对注解中的参数进行保留
-dontshrink #不缩减代码，需要注意，反射调用的代码会被认为是无用代码而删掉，所以要提前keep出来
-keepclassmembers enum * { #保持枚举类中的属性不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends android.view.View { # 保留我们自定义控件（继承自View）不被混淆
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
#-keepclassmembers public class xxx extends xxx { #不混淆任何view子类的get和set方法
#    void set*(***);
#    *** get*();
#}
-keepclassmembers class * implements android.os.Parcelable { #aidl文件不能去混淆
    public static final android.os.Parcelable$Creator CREATOR;
}
-keepclassmembers class **.R$* { #资源类变量需要保留
    public static *;
}

-keep public class anda.travel.driver.config.** {*;}
-keep public class anda.travel.driver.data.** {*;}
-keep public class anda.travel.driver.configurl.MyConfig {*;}
-keep public class anda.travel.driver.module.vo.** {*;}
-keep public class anda.travel.driver.socket.message.** {*;}
-keep public class anda.travel.driver.socket.SocketData {*;}
-keep public class anda.travel.driver.socket.SocketPushContent {*;}
-keep public class anda.travel.network.** {*;} #base库
-keep public class anda.travel.driver.widget.select.TimeDialog {*;}

-keep public class anda.travel.driver.client.** {*;}
-keep public class anda.travel.driver.client.constants.** {*;}
-keep public class anda.travel.driver.client.message.** {*;}
-keep public class anda.travel.driver.client.message.body.** {*;}
-keep public class anda.travel.driver.auth.** {*;}

-dontwarn io.netty.**
-keep class io.netty.** {*;}
-keep class io.netty.channel.** {*;}
-keep class io.netty.channel.nio.** {*;}
-keep class io.netty.channel.socket.** {*;}
-keep class io.netty.util.** {*;}
-keep class io.netty.util.concurrent.** {*;}
-keep class io.netty.util.internal.chmv8.ForkJoinPool {*;}


# RxJava避免混淆
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
 long producerIndex;
 long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

-dontwarn javax.annotation.**
-dontwarn javax.inject.**
# OkHttp3
-dontwarn okhttp3.logging.**
-keep class okhttp3.internal.**{*;}
-dontwarn okio.**
-keep class okio.**{*;}
# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
# shareSDK
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}

-keep class com.mob.**{*;}
-dontwarn com.mob.**
-dontwarn cn.sharesdk.**
-dontwarn **.R$*
-keep class m.framework.**{*;}

# Bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.** {*;}
# 保留源文件名及行号
-keepattributes SourceFile,LineNumberTable

# ButterKnife
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keepnames class * { @butterknife.InjectView *;}

#EventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(Java.lang.Throwable);
}

#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# LeakCanary
-keep class org.eclipse.mat.** { *; }
-keep class com.squareup.leakcanary.** { *; }

# LitePal
-keep public class org.litepal.** {*;}
-keep public class * extends org.litepal.** {*;}

#讯飞语音
-keep class com.iflytek.**{*;}

### 高德地图相关 ###
#3D地图 V5.0.0之后：
-keep class com.amap.api.maps.**{*;}
-keep class com.autonavi.**{*;}
-keep class com.amap.api.trace.**{*;}

#定位：
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}
-keep class com.loc.**{*;}

#搜索：
-keep class com.amap.api.services.**{*;}

#导航 V7.3.0及以后：
-keep class com.amap.api.navi.**{*;}
-keep class com.alibaba.mit.alitts.*{*;}
-keep class com.google.**{*;}

#导航 V8.1.0及以后：
-keep class com.amap.api.navi.**{*;}
-keep class com.alibaba.idst.nui.* {*;}
-keep class com.google.**{*;}


#内置语音 V5.6.0之后
-keep class com.alibaba.idst.nls.** {*;}
-keep class com.google.**{*;}
-keep class com.nlspeech.nlscodec.** {*;}
-keep public class com.alibaba.mit.alitts.*{*;}
### 支付宝 ###
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.taobao.** {*;}
-keep class com.alibaba.** {*;}
-keep class com.alipay.** {*;}
-dontwarn com.taobao.**
-dontwarn com.alibaba.**
-dontwarn com.alipay.**
-keep class com.ut.** {*;}
-dontwarn com.ut.**
-keep class com.ta.** {*;}
-dontwarn com.ta.**
-keep class anet.**{*;}
-keep class org.android.spdy.**{*;}
-keep class org.android.agoo.**{*;}
-dontwarn anet.**
-dontwarn org.android.spdy.**
-dontwarn org.android.agoo.**

### 环信IM ###
-keep class com.hyphenate.** {*;}
-dontwarn  com.hyphenate.**


### 避免混淆openFileChooser() ###
-keepclassmembers class * extends android.webkit.WebChromeClient {
       public void openFileChooser(...);
}


### 极光推送im相关 ###
-dontoptimize
-dontpreverify
-keepattributes  EnclosingMethod,Signature
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }

-dontwarn cn.jmessage.**
-keep class cn.jmessage.**{ *; }

-keepclassmembers class ** {
    public void onEvent*(**);
}

#========================gson================================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}

#========================protobuf================================
-keep class com.google.protobuf.** {*;}


### 腾讯云通信 ###
-keep class com.tencent.**{*;}
-dontwarn com.tencent.**
-keep class tencent.**{*;}
-dontwarn tencent.**
-keep class qalsdk.**{*;}
-dontwarn qalsdk.**

### 华为推送 ###
-keep class com.huawei.android.**{*;}
-keep class com.huawei.deviceCloud.microKernel.core.intf.**{*;}

### 小米推送 ###
-keep class com.xiaomi.**{*;}
-keep class com.xiaomi.push.service.XMPushService{*;}


-keep class com.tencent.qcloud.ui.**{*;}
-keep class com.tencent.qcloud.tlslibrary.**{*;}
-keep class com.tencent.qcloud.timchat.**{*;}
-keep class com.tencent.qcloud.sdk.**{*;}
-keep class com.tencent.qcloud.presentation.**{*;}
-keep class com.huawei.deviceCloud.microKernel.core.intf.IPluginActivator{*;}
-keep class com.huawei.deviceCloud.microKernel.core.intf.IPluginContext{*;}
-keep class android.os.SystemProperties{*;}
-keep class android.os.ServiceManager{*;}
-keep class android.app.Notification{*;}

-dontnote android.net.http.**
-dontnote org.apache.http.**


-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }


# 百度语音
-keep class com.baidu.tts.**{*;}
-keep class com.baidu.speechsynthesizer.**{*;}

#阿里身份验证
-keep class com.taobao.securityjni.**{*;}
-keep class com.taobao.wireless.security.**{*;}
-keep class com.ut.secbody.**{*;}
-keep class com.taobao.dp.**{*;}
-keep class com.alibaba.wireless.security.**{*;}
-keep class com.alibaba.security.rp.**{*;}
-keep class com.alibaba.security.cloud.**{*;}
-keep class com.alibaba.security.realidentity.**{*;}
-keep class com.alibaba.security.biometrics.**{*;}
-keep class com.alibaba.sdk.android.**{*;}
-keep class android.taobao.windvane.**{*;}
-keep class android.webkit.JavascriptInterface

# talkingdata
-dontwarn com.tendcloud.tenddata.**
-keep class com.tendcloud.** {*;}
-keep public class com.tendcloud.tenddata.** { public protected *;}
-keepclassmembers class com.tendcloud.tenddata.**{
public void *(***);
}
-keep class com.talkingdata.sdk.TalkingDataSDK {public *;}
-keep class com.apptalkingdata.** {*;}
-keep class dice.** {*; }
-dontwarn dice.**

#CacheWebview
-dontwarn ren.yale.android.cachewebviewlib.**
-keep class ren.yale.android.cachewebviewlib.**{*;}

# universal-image-loader 混淆
-dontwarn com.nostra13.universalimageloader.**
-keep class com.nostra13.universalimageloader.** { *; }


# fastjson
-keep class com.alibaba.fastjson.** { *; }
-dontwarn com.alibaba.fastjson.**
-keepnames class * implements java.io.Serializable

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep class com.youth.banner

#PictureSelector 2.0
-keep class com.luck.picture.lib.** { *; }

#Ucrop
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

#Okio
-dontwarn org.codehaus.mojo.animal_sniffer.*

# oss混淆
-keep class com.alibaba.sdk.android.oss.** { *; }
-dontwarn okio.**
-dontwarn org.apache.commons.codec.binary.**