plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
//    id("kotlin-android-extensions")
}

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "com.sharyuke.empty"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")

    // user this line to fix Duplicate class androidx lifecycle viewmodellazy cause by cymchad adapter lib.
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")

    implementation(netRetrofit)
    implementation(netRetrofitGson)
    implementation(netOkhttp)
    implementation(netOkhttpLogging)

    implementation(coroutinesCore)
    implementation(coroutinesAndroid)
    implementation(lifecycleKtx)

    implementation(uiRecyclerViewQuickAdapter) { exclude("androidx.lifecycle", "lifecycle-viewmodel-ktx") }
    implementation(uiRecyclerView)
    implementation(uiPicGlide)
    implementation(uiPager3)
    implementation(uiSwipeRefreshLayout)
}