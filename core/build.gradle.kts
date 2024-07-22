plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-kapt")
}

android {
    namespace = "bogomolov.aa.wordstrainer.features.core"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
        //noinspection EditedTargetSdkVersion
        targetSdk = 34
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    lint {
        abortOnError = false
    }
}

dependencies {

    implementation("androidx.preference:preference-ktx:1.1.1")

    api("org.jetbrains.kotlin:kotlin-stdlib:1.9.23")
    api("androidx.appcompat:appcompat:1.2.0")
    api("androidx.core:core-ktx:1.3.2")
    api("androidx.constraintlayout:constraintlayout:2.0.4")
    api("com.google.android.material:material:1.4.0-alpha01")
    api("androidx.recyclerview:recyclerview:1.1.0")
    api("androidx.navigation:navigation-fragment-ktx:2.3.4")
    api("androidx.navigation:navigation-ui-ktx:2.3.4")
    api("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.0")
    api("androidx.lifecycle:lifecycle-livedata-ktx:2.3.0")
    api("androidx.lifecycle:lifecycle-extensions:2.2.0")
    api("androidx.fragment:fragment-ktx:1.3.1")
    api("com.android.support:multidex:1.0.3")
    api("com.google.dagger:dagger:2.51.1")
    api("androidx.annotation:annotation:1.1.0")


    api("io.reactivex.rxjava3:rxjava:3.1.6")
    api("io.reactivex.rxjava3:rxandroid:3.0.2")

}