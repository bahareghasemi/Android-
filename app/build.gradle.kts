plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.posedetectionapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.posedetectionapp"
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
    aaptOptions {
        noCompress ("tflite")
    }
}

dependencies {
    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.gpu)
    implementation(libs.tensorflow.lite.support)

    implementation(libs.appcompat.v141)
    implementation(libs.material.v150)
    implementation(libs.constraintlayout.v213)
    implementation(libs.camera.camera2.v110)
    implementation(libs.camera.lifecycle.v110)
    implementation(libs.camera.view.v100alpha31)

    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.v113)
    androidTestImplementation(libs.espresso.core.v340)
}