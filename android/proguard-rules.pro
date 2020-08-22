# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
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

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/* 
 
 
 
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
 
# http://stackoverflow.com/questions/4525661/android-proguard-cant-find-dynamically-referenced-class-javax-swing
-dontwarn java.awt.**
-dontnote java.awt.**
#-dontwarn com.badlogic.gdx.jnigen.**
-dontwarn com.badlogic.gdx.**
-dontwarn com.google.ads.**
-dontwarn java.lang.management.**
-dontwarn com.apperhand.**
-dontwarn android.support.**
-dontwarn com.google.android.gms.**
 
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends com.badlogic.gdx.backends.android.AndroidApplication
-keep public class * extends com.badlogic.gdx.controllers
-keep public class com.android.vending.licensing.ILicensingService
-keep class com.badlogic.gdx.controllers.android.AndroidControllers
-keep class com.badlogic.** { *; }
-keep class com.chartboost.** { *; }
-keep class com.bda.** { *; }


-keep class com.apperhand.** { *; }
-keep class com.google.mygson.** { *; }
-keep public class com.google.** {*;}

-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile, LineNumberTable, *Annotation*, EnclosingMethod
-dontwarn android.webkit.JavascriptInterface

 
-keepclasseswithmembernames class * {
    native <methods>;
}
 
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
 
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
 
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
 
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
 
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keepclassmembers class com.badlogic.gdx.backends.android.AndroidInput* {
   <init>(com.badlogic.gdx.Application, android.content.Context, java.lang.Object, com.badlogic.gdx.backends.android.AndroidApplicationConfiguration);
}

-keepclassmembers class com.badlogic.gdx.physics.box2d.World {
   boolean contactFilter(long, long);
   void    beginContact(long);
   void    endContact(long);
   void    preSolve(long, long);
   void    postSolve(long, long);
   boolean reportFixture(long);
   float   reportRayFixture(long, float, float, float, float, float);
}