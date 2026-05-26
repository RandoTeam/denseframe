# Android Skeleton Verification Notes

These setup assumptions were checked before creating the initial Android skeleton.

## Official Sources Checked

- Android Gradle Plugin release notes: `https://developer.android.com/studio/releases/gradle-plugin`
- Gradle 8.14.3 release notes: `https://docs.gradle.org/8.14.3/release-notes.html`
- Jetpack Compose release notes and BOM guidance: `https://developer.android.com/jetpack/androidx/releases/compose`
- AndroidX Activity release notes: `https://developer.android.com/jetpack/androidx/releases/activity`

## Local Environment Observed

- JDK: Microsoft OpenJDK 21.
- Android SDK: `ANDROID_HOME` and `ANDROID_SDK_ROOT` were configured locally.
- Installed SDK platforms include API 36.
- No system `gradle` executable was on PATH.
- A local Gradle 8.14.3 distribution was available under the Gradle wrapper cache and was used to generate the project wrapper.

## Selected Versions

- Android Gradle Plugin: `8.13.2`.
- Gradle wrapper: `8.14.3`.
- Kotlin: `2.2.21`.
- Compose BOM: `2026.02.00`.
- Activity Compose: `1.10.1`.
- Compile SDK: `36`.
- Target SDK: `36`.
- Minimum SDK: `26`.

## Uncertainty

Compose BOM `2026.02.00` and Activity Compose `1.10.1` were selected because they were available locally and stable enough for a shell. Before implementing production UI behavior, recheck current official AndroidX stable-channel guidance and update deliberately.

No ARCore, CameraX, Filament, Room, analytics, cloud, or network runtime dependencies were added.
