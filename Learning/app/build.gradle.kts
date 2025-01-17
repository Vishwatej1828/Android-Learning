import org.gradle.internal.impldep.bsh.commands.dir

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.learnigCustomViews"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.learnigCustomViews"
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

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.squareup.picasso:picasso:2.5.1")

}


//dependencies {
//    implementation fileTree(dir: 'libs', include: ['*.jar'])
//    implementation 'com.android.support:appcompat-v7:28.0.0'
//    implementation 'com.android.support.constraint:constraint-layout:1.1.3'
//    testImplementation 'junit:junit:4.12'
//    implementation 'com.squareup.picasso:picasso:2.5.1'
//    androidTestImplementation 'com.android.support.test:runner:1.0.2'
//    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
//}