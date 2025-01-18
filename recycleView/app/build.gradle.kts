plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.recycleviewforbes2024ai"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.recycleviewforbes2024ai"
        minSdk = 24
        targetSdk = 34
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
}

dependencies {
    // AppCompat for backward compatibility with older Android versions
    implementation(libs.appcompat)

    // Material Design components for UI elements
    implementation(libs.material)

    // Activity component for managing lifecycle of activities
    implementation(libs.activity)

    // ConstraintLayout for flexible layouts
    implementation(libs.constraintlayout)

    // RecyclerView for lists and grid layouts
    implementation(libs.recyclerview)

    // Glide for image loading
    implementation(libs.glide)

    // Caverock for svg image rendering
    implementation(libs.caverock)

    // Unit testing dependencies
    testImplementation(libs.junit)

    // Android test dependencies for UI testing
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}