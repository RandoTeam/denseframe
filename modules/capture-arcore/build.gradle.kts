plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.denseframe.capturearcore"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }
}

dependencies {
    implementation(project(":modules:capture-api"))
    implementation(libs.google.ar.core)

    testImplementation(libs.junit)
}
