plugins {
    alias(libs.plugins.android.application)
    // Abilita il plugin di Google Services (richiede google-services.json in app/)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.beagle"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.beagle"
        minSdk = 24
        targetSdk = 35
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
        debug {
            isMinifyEnabled = false
        }
    }

    // AGP 8.x → Java 17
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Base UI (via version catalog)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // MVVM / Lifecycle (coordinate DIRETTE per evitare mismatch di catalog)
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.4")
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.4")
    implementation("androidx.lifecycle:lifecycle-runtime:2.8.4")

    // Navigation
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // --- Firebase ---
    implementation(platform("com.google.firebase:firebase-bom:34.1.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")

    // Google Sign-In (necessario se usi login con Google)
    implementation("com.google.android.gms:play-services-auth:21.4.0")

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
