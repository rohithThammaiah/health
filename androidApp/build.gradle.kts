plugins {
    alias(libs.plugins.android.application) version libs.versions.agp.get()
    alias(libs.plugins.kotlin) version libs.versions.kotlin.get()
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvmToolchain(20)
}

android {
    namespace = "dev.rohith.health"
    compileSdk = libs.versions.android.sdk.compile.get().toInt()

    defaultConfig {
        applicationId = "dev.rohith.health"
        minSdk = libs.versions.android.sdk.min.get().toInt()
        targetSdk = libs.versions.android.sdk.target.get().toInt()
        versionCode = 1
        versionName = "0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(libs.kotlinx.datetime)

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)

    implementation(libs.ui.text.google.fonts)

    implementation(libs.mavericks)
    implementation(libs.mavericks.compose)

    implementation(libs.health.connect)

    implementation(libs.arrow.core)
    implementation(libs.arrow.fx.coroutines)

    implementation(project(":shared"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
}
