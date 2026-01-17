# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ===== Compose 관련 ProGuard 규칙 =====

# Compose UI 컴포넌트 보존
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }

# onClick 람다 함수 보존
-keepclassmembers class * {
    *** onClick*(...);
}

# Composable 함수 보존
-keep @androidx.compose.runtime.Composable class * { *; }
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}

# Material3 컴포넌트 보존
-keep class androidx.compose.material3.** { *; }

# 클릭 이벤트 핸들러 보존
-keepclassmembers class * {
    public void on*Click(...);
    public void on*(...);
}

# Kotlin 람다 보존
-keepclassmembers class kotlin.jvm.functions.** { *; }

# ===== 기존 규칙 유지 =====
