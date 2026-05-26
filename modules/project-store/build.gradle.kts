plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.denseframe.projectstore"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }
}

dependencies {
    testImplementation(libs.junit)
}
