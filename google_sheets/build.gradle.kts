plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-kapt")
}

android {
    namespace = "bogomolov.aa.wordstrainer.features.google_sheets"
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
    implementation(project(":core"))

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation("com.google.android.gms:play-services-auth:19.0.0")
    implementation("com.google.api-client:google-api-client-android:1.28.0") {
        exclude("com.google.api-client", "org.apache.httpcomponents")
    }
    implementation("com.google.apis:google-api-services-sheets:v4-rev571-1.25.0") {
        exclude("com.google.apis", "org.apache.httpcomponents")
    }
    implementation("com.google.http-client:google-http-client-gson:1.26.0")
    implementation("com.google.apis:google-api-services-drive:v3-rev136-1.25.0") {
        exclude("com.google.apis", "org.apache.httpcomponents")
    }
}