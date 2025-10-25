
plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.android.library") /* <- Android Gradle Plugin for libraries */
    // id("com.android.kotlin.multiplatform.library")
}

kotlin {
    androidTarget()
    /*androidLibrary {
        namespace = "dev.hugomfandrade.mediadownloader.ui.shared"
        compileSdk = 36
        minSdk = 21
    }*/

    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.components.resources)
                implementation(project(":core"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(compose.uiTooling)
            }
        }
        val desktopMain by getting
    }
}

android {
    namespace = "dev.hugomfandrade.mediadownloader.ui.shared"
    compileSdk = 36
    defaultConfig {
        minSdk = 21
    }
}