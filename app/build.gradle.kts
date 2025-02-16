plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "id.co.softorb.app.offinemapoptions"
    compileSdk = 35

    defaultConfig {
        applicationId = "id.co.softorb.app.offinemapoptions"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "0.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation ("org.mapsforge:mapsforge-core:0.23.0")
    implementation ("org.mapsforge:mapsforge-map:0.23.0")
    implementation ("org.mapsforge:mapsforge-map-reader:0.23.0")
    implementation ("org.mapsforge:mapsforge-themes:0.23.0")
    implementation ("org.mapsforge:mapsforge-map-android:0.23.0")
    implementation ("com.caverock:androidsvg:1.4")
    implementation ("org.mapsforge:mapsforge-core:0.23.0")
    implementation ("org.mapsforge:mapsforge-poi:0.23.0")
    implementation ("org.mapsforge:mapsforge-poi-android:0.23.0")
}