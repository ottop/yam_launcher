plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "eu.ottop.yamlauncher"
    compileSdk = 35

    defaultConfig {
        applicationId = "eu.ottop.yamlauncher"
        minSdk = 31
        //noinspection OldTargetApi
        targetSdk = 35
        versionCode = 12
        versionName = "1.7"
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
        }
        release {
            isDebuggable = false
            isShrinkResources = true
            isMinifyEnabled = true
            isProfileable = false
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
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.recyclerview)
    implementation(libs.preference.ktx)
    implementation(libs.activity.ktx)
    implementation(libs.constraintlayout)
    implementation(libs.biometric.ktx)
}