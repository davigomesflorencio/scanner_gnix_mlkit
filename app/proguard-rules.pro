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

# Google ML Kit rules
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.internal.mlkit_vision_* { *; }
-dontwarn com.google.android.gms.internal.mlkit_vision_*

# Flutter Camera Plugin rules
-keep class io.flutter.plugins.camera.** { *; }
-keep class androidx.camera.core.** { *; }
-keep class androidx.camera.camera2.** { *; }
-keep class androidx.camera.lifecycle.** { *; }
-keep class androidx.camera.view.** { *; }

-dontwarn com.fasterxml.jackson.annotation.JsonInclude$Include
-dontwarn com.fasterxml.jackson.core.JsonGenerator$Feature
-dontwarn com.fasterxml.jackson.core.JsonGenerator
-dontwarn com.fasterxml.jackson.core.JsonParser$Feature
-dontwarn com.fasterxml.jackson.core.JsonParser
-dontwarn com.fasterxml.jackson.core.JsonProcessingException
-dontwarn com.fasterxml.jackson.core.JsonToken
-dontwarn com.fasterxml.jackson.core.PrettyPrinter
-dontwarn com.fasterxml.jackson.core.type.TypeReference
-dontwarn com.fasterxml.jackson.core.util.DefaultIndenter
-dontwarn com.fasterxml.jackson.core.util.DefaultPrettyPrinter$Indenter
-dontwarn com.fasterxml.jackson.core.util.DefaultPrettyPrinter
-dontwarn com.fasterxml.jackson.databind.DeserializationContext
-dontwarn com.fasterxml.jackson.databind.DeserializationFeature
-dontwarn com.fasterxml.jackson.databind.JavaType
-dontwarn com.fasterxml.jackson.databind.JsonDeserializer
-dontwarn com.fasterxml.jackson.databind.JsonNode
-dontwarn com.fasterxml.jackson.databind.JsonSerializer
-dontwarn com.fasterxml.jackson.databind.Module
-dontwarn com.fasterxml.jackson.databind.ObjectMapper
-dontwarn com.fasterxml.jackson.databind.ObjectWriter
-dontwarn com.fasterxml.jackson.databind.SerializationFeature
-dontwarn com.fasterxml.jackson.databind.SerializerProvider
-dontwarn com.fasterxml.jackson.databind.module.SimpleModule
-dontwarn com.itextpdf.bouncycastle.BouncyCastleFactory
-dontwarn sharpen.config.MappingConfiguration
-dontwarn sharpen.config.MappingConfigurator
-dontwarn sharpen.config.MemberKind
-dontwarn sharpen.config.ModuleOption
-dontwarn sharpen.config.ModulesConfigurator
-dontwarn sharpen.config.OptionsConfigurator