plugins {
    alias(libs.plugins.android.application)
    // Abilita quando aggiungerai google-services.json:
    // id("com.google.gms.google-services")
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

    // MVVM / Lifecycle
    implementation(libs.lifecycle.viewmodel.v284)
    implementation(libs.lifecycle.livedata.v284)
    implementation(libs.lifecycle.runtime)

    // Nav Graph
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Google Sign-In (lascia commentato finché non serve/è nel catalog)
    // implementation(libs.play.services.auth)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //Room
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
}
