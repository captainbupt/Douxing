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

# 百度地图
#-libraryjars libs/baidumapapi_v3_3_0.jar
-keep class com.baidu.** { *; }
-keep class vi.com.gdi.bgl.android.**{*;}

# 极光推送
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

# XUtils
-keep class * extends java.lang.annotation.Annotation { *; }


# 友盟
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

# retrofit
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions
