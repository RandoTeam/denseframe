---
name: android-architecture
description: Trigger for Android module, lifecycle, dependency, build, permission, service, and runtime architecture decisions.
---

Use Kotlin and Jetpack Compose unless an ADR changes this.
Verify Android and Gradle APIs against official docs before implementation.
Keep native boundaries narrow and explicit.
Do not block the main thread.
Prefer local-first storage and processing.
Do not add network, cloud, analytics, or broad architecture dependencies without an ADR.
