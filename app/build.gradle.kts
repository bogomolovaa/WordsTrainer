@file:Suppress("UnstableApiUsage")

import com.android.build.api.dsl.Packaging
import java.io.FileInputStream
import java.util.*

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-kapt")
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
    namespace = "bogomolov.aa.wordstrainer"
    compileSdk = 34
    buildToolsVersion = "34.0.0"
    defaultConfig {
        applicationId = "bogomolov.aa.wordstrainer"
        minSdk = 24
        //noinspection EditedTargetSdkVersion
        targetSdk = 34
        versionCode = 3
        versionName = "1.2"
        multiDexEnabled = true
        javaCompileOptions {
            annotationProcessorOptions {
                argument("room.expandProjection", "true")
            }
        }
    }

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
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
    packaging {
        resources.excludes.add("META-INF/DEPENDENCIES")
    }
}
dependencies {
    implementation(project(":google_sheets"))
    implementation(project(":core"))

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.moshi:moshi:1.8.0")

    implementation("androidx.room:room-runtime:2.3.0")
    implementation("androidx.room:room-rxjava3:2.3.0")
    kapt("androidx.room:room-compiler:2.3.0")

    kapt("com.google.dagger:dagger-compiler:2.51.1")

}