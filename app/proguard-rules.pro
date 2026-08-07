# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep Room
-keep class androidx.room.** { *; }
-dontwarn androidx.room.paging.**

# Keep WebView
-keep class android.webkit.** { *; }

# Keep Gson
-keep class com.google.gson.** { *; }

# Keep OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
