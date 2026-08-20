plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.mdzahidalam.myfinancetracker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mdzahidalam.myfinancetracker"
        minSdk = 26
        targetSdk = 35
        versionCode = 19
        versionName = "6.0"
    }

    val stableStorePath = System.getenv("MFT_SIGNING_STORE_FILE")
    val stableStorePassword = System.getenv("MFT_SIGNING_PASSWORD")

    signingConfigs {
        if (!stableStorePath.isNullOrBlank() && !stableStorePassword.isNullOrBlank()) {
            create("stable") {
                storeFile = file(stableStorePath)
                storePassword = stableStorePassword
                keyAlias = "myfinancetracker"
                keyPassword = stableStorePassword
                storeType = "PKCS12"
            }
        }
    }

    buildTypes {
        debug {
            // Debug builds intentionally use the normal Android debug key.
        }
        release {
            isDebuggable = false
            isMinifyEnabled = false
            signingConfigs.findByName("stable")?.let { signingConfig = it }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.biometric:biometric:1.1.0")
    implementation("androidx.core:core-ktx:1.15.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
