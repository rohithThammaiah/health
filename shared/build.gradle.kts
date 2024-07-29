plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.plugin)
}

kotlin {
    jvmToolchain(20)

    androidTarget {

    }
    iosArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)

            implementation(libs.arrow.core)
            implementation(libs.arrow.fx.coroutines)

            implementation(libs.bundles.compose)
            implementation(compose.components.resources)

            implementation(libs.decompose)
            implementation(libs.essenty)
            implementation(libs.jetbrains.lifecycle.viewmodel.compose)
        }

        commonTest.dependencies {

        }

        androidMain.dependencies {
            implementation(libs.health.connect)

            implementation(libs.activity.compose)
        }

        iosMain.dependencies {

        }
    }
}

android {
    compileSdk = libs.versions.android.sdk.compile.get().toInt()
    namespace = "dev.rohith.health.shared"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = libs.versions.android.sdk.min.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "dev.rohith.health.resources"
    generateResClass = always
}