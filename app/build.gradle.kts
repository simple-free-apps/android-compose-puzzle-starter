plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.simplefreeapps.chromacircuit"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.simplefreeapps.chromacircuit"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions += "store"
    productFlavors {
        create("noAds") {
            dimension = "store"
            buildConfigField("boolean", "ADS_ENABLED", "false")
        }
        create("withAds") {
            dimension = "store"
            buildConfigField("boolean", "ADS_ENABLED", "true")
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    implementation(libs.compose.material.icons.extended)

    add("withAdsImplementation", libs.play.services.ads)
    add("withAdsImplementation", libs.user.messaging.platform)

    debugImplementation(libs.compose.ui.tooling)
    testImplementation(libs.junit)
}
