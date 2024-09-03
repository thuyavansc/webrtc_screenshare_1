plugins {
    alias(libs.plugins.android.application)
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "au.com.softclient.webrtc_screenshare_1"
    compileSdk = 34

    defaultConfig {
        applicationId = "au.com.softclient.webrtc_screenshare_1"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.google.code.gson:gson:2.10.1") // Gson for JSON parsing
    implementation("com.mesibo.api:webrtc:1.0.5") // WebRTC dependency
    implementation("com.google.dagger:hilt-android:2.44") // Hilt dependency
    annotationProcessor("com.google.dagger:hilt-compiler:2.44") // Hilt compiler for Java
    implementation("org.java-websocket:Java-WebSocket:1.5.3") // WebSocket library
}