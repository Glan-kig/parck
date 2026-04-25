plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.parck"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.parck"
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    // --- Interface & Design ---
    implementation("androidx.compose.material3:material3")
    implementation("io.coil-kt:coil-compose:2.5.0") // Pour l'affichage des photos

    // --- Backend (Supabase) ---
    implementation("io.github.jan-tennert.supabase:postgrest-kt:2.0.0") // Pour la table parking_sessions
    implementation("io.github.jan-tennert.supabase:storage-kt:2.0.0")   // Pour l'upload des images
    implementation(libs.ktor.client.android) // Moteur HTTP nécessaire pour Supabase

    // --- Architecture & Background ---
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0") // MVVM
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material3.common)
    implementation(libs.androidx.navigation.compose) // Pour les alertes 24h

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}