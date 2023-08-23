// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
}
buildscript {
    repositories {
        maven { url = uri("https://www.jitpack.io" ) }
        google()
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:8.1.1")
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")
    }
}

