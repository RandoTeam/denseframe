plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.denseframe.capturecamerax"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }
}
