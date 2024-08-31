plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.thucfb.qw.android.lawnchairdoubleclickrecentfix"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.thucfb.qw.android.lawnchairdoubleclickrecentfix"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
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
}

dependencies {
    compileOnly(libs.api)
}