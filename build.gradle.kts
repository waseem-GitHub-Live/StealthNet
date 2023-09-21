// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
}
buildscript {
    repositories {
        maven { url = uri("https://www.jitpack.io" ) }
        maven { url = uri("https://maven.google.com") }
        maven { url = uri("https://plugins.gradle.org/m2/")}
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:8.1.1")
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.2")
        classpath ("com.google.gms:google-services:4.4.0")
        classpath ("gradle.plugin.com.onesignal:onesignal-gradle-plugin:0.14.0")
        classpath ("com.android.tools.build:gradle:7.0.4")
        classpath ("gradle.plugin.com.onesignal:onesignal-gradle-plugin:[0.13.4, 0.99.99]")
    }
}


