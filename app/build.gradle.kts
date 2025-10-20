// Kairo To-Do App build.gradle.kts
plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.kairo.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kairo.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 220
        versionName = "2.2.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8" // 對應 Compose UI 版本
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/DEPENDENCIES"
        }
    }
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}

dependencies {
    // Compose 核心 UI
    implementation("androidx.compose.ui:ui:1.6.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.0")

    // Compose Foundation
    implementation("androidx.compose.foundation:foundation:1.6.0")

    // Material3 元件
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.material:material-icons-extended:1.6.0")

    // Activity Compose
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.fragment:fragment-ktx:1.8.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Date Picker
    implementation("io.github.vanpra.compose-material-dialogs:datetime:0.9.0")

    // Work Manager (optional placeholder for reminders)
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // DataStore for settings
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // No Hilt - using manual DI via AppContainer

    // Google Calendar API
    implementation("com.google.api-client:google-api-client:2.4.0")
    implementation("com.google.api-client:google-api-client-android:2.4.0") {
        exclude(group = "org.apache.http.legacy")
    }
    implementation("com.google.http-client:google-http-client-android:1.45.1")
    implementation("com.google.apis:google-api-services-calendar:v3-rev20250404-2.0.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
}
