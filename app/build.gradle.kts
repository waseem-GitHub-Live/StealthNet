plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("androidx.navigation.safeargs.kotlin")
    id ("com.onesignal.androidsdk.onesignal-gradle-plugin")
}

android {
    namespace = "com.xilli.stealthnet"
    compileSdk = 34
    packagingOptions {
        pickFirst("**/*.so")
    }
    defaultConfig {
        applicationId = "com.xilli.stealthnet"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        val newManifestPlaceholders = manifestPlaceholders.toMutableMap()

        newManifestPlaceholders["onesignal_app_id"] = "a2be7720-a32b-415a-9db1-d50fdc54f069"
        newManifestPlaceholders["onesignal_google_project_number"] = "REMOTE"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    buildTypes {
        release {

            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.airbnb.android:lottie:4.2.0")

    implementation("androidx.navigation:navigation-fragment-ktx:2.7.2")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.2")
//    "androidx.navigation:navigation-safe-args-gradle-plugin:2.3.5"

    implementation("androidx.core:core-splashscreen:1.0.1")

    implementation ("androidx.drawerlayout:drawerlayout:1.2.0")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.recyclerview:recyclerview:1.3.1")

    implementation ("com.squareup.okhttp3:okhttp:4.9.1")

    implementation ("com.squareup.picasso:picasso:2.71828")

   implementation ("com.onesignal:OneSignal:4.4.1")

    implementation ("com.github.oneconnectapi:OneConnectLib:v1.1.0")

    implementation ("com.intuit.sdp:sdp-android:1.0.5")
//
//    implementation ("com.github.GrenderG:Toasty:1.2.5")

    implementation ("com.github.bumptech.glide:glide:4.10.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.9.0")


    implementation ("com.android.volley:volley:1.1.1")
}