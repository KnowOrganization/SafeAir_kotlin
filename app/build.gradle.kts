plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    id ("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")

}

android {
    namespace = "com.knoworganization.safeair_kotlin"
    compileSdk = 33
    defaultConfig {
        applicationId = "com.knoworganization.safeair_kotlin"
        minSdk = 24
        targetSdk = 33
        versionCode = 12
        versionName = "1.9"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isDebuggable = false
        }
        debug {
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding= true
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
//    Navigation
    implementation (libs.androidx.navigation.fragment.ktx)
    implementation (libs.androidx.navigation.ui.ktx)
//    Location service
    implementation(libs.eventbus)
    implementation (libs.play.services.location)
//    Realtime database Firebase
    implementation(libs.firebase.database.ktx)
//    Firebase Auth
    implementation(libs.firebase.auth.ktx)
//    Caching
    implementation (libs.gson)
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation(platform("com.google.firebase:firebase-bom:32.2.2"))
//    API calls
// retrofit
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
//    Biometric
    implementation (libs.androidx.biometric.ktx)
//    Crashlytics
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-analytics-ktx")
}