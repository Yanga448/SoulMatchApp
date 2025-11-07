

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)

    id("kotlin-kapt")

}




android {
    namespace = "com.example.soulmatchapp"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.soulmatchapp"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    // ✅ Enable ViewBinding
    buildFeatures {

        viewBinding = true
        dataBinding = true    // ✅ Add this line
    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

// Retrofit (for API calls)
    implementation(libs.retrofit)

// Gson converter for JSON parsing
    implementation(libs.retrofit.gson)

// OkHttp (for HTTP client)
    implementation(libs.okhttp)

// OkHttp logging interceptor (for debugging network calls)
    implementation(libs.okhttp.logging)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)
    implementation(libs.androidx.preference)
    implementation(libs.firebase.messaging)

    implementation(platform(libs.firebase.bom))

    implementation(libs.google.play.services.auth)









}
