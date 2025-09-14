plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services) // Google Services attivo (Firebase, default_web_client_id, ecc.)
}

android {
    namespace = "com.example.beagle"
    compileSdk = 36

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
    // Base UI (version catalog)
    implementation(libs.appcompat)
    implementation(libs.material)          // Material 3: usi Widget.Material3.* nei layout
    implementation(libs.activity)
    implementation(libs.constraintlayout)


    // MVVM / Lifecycle
    implementation(libs.lifecycle.livedata.v284)
    implementation(libs.lifecycle.runtime)

    // Navigation (Java API, coerente con i Fragment e il NavHost)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Firebase & Google Services
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.play.services.auth) // Google Sign-In (Play Services)

    // Credential Manager + Google Identity Services
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play)
    implementation(libs.googleid)

    // Room (Java: annotationProcessor)
    implementation(libs.room.runtime)
    implementation(libs.androidx.lifecycle.viewmodel)
    annotationProcessor(libs.androidx.room.compiler)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // JSON
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
}