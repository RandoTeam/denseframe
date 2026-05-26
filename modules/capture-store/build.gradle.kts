plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.denseframe.capturestore"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }
}

dependencies {
    implementation(project(":modules:capture-api"))
    implementation(project(":modules:project-store"))

    testImplementation(libs.junit)
}
