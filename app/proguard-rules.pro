# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Android\sdk\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

# �ٶȵ�ͼ
#-libraryjars libs/baidumapapi_v3_3_0.jar
-keep class com.baidu.** { *; }
-keep class vi.com.gdi.bgl.android.**{*;}

# ��������
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

# XUtils
-keep class * extends java.lang.annotation.Annotation { *; }


# ����
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keep public class com.badou.mworking.R$*{
public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# volley
-keep class com.android.volley.**{*;}

# Pulltorefresh
-keep class com.handmark.pulltorefresh.**{*;}

# nineoldandroid
#-libraryjars libs/nineoldandroids-2.4.0.jar
-keep class com.nineoldandroids.**{*;}

# butterknife
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keepnames class * { @butterknife.InjectView *;}

# easemob
-keep class com.easemob.** {*;}
-keep class org.jivesoftware.** {*;}
-keep class org.apache.** {*;}
-dontwarn  com.easemob.**
#2.0.9��Ĳ���Ҫ���������keep
#-keep class org.xbill.DNS.** {*;}
#���⣬demo�з��ͱ����ʱ��ʹ�õ����䣬��Ҫkeep SmileUtils,ע��ǰ��İ�����
#��ҪSmileUtils���Ƶ��Լ�����Ŀ��keep��ʱ����д��demo��İ���
-keep class com.easemob.chatuidemo.utils.SmileUtils {*;}

#2.0.9���������ͨ�����ܣ�����ʹ�ô˹��ܵ�api����������keep
-dontwarn ch.imvs.**
-dontwarn org.slf4j.**
-keep class org.ice4j.** {*;}
-keep class net.java.sip.** {*;}
-keep class org.webrtc.voiceengine.** {*;}
-keep class org.bitlet.** {*;}
-keep class org.slf4j.** {*;}
-keep class ch.imvs.** {*;}
