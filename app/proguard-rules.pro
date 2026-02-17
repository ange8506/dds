-keep class com.dds.assistant.** { *; }
-keepclassmembers class * extends android.accessibilityservice.AccessibilityService {
    public <init>(...);
}

