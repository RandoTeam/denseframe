# ARCore Runtime

DenseFrame treats ARCore as an optional runtime capture capability.

## AR Optional Decision

The Android manifest uses:

```xml
<meta-data android:name="com.google.ar.core" android:value="optional" />
```

This allows the app to open on devices where ARCore is missing or unsupported. The capture screen reports availability, install/update state, camera permission, depth support, and session status explicitly.

## Runtime Flow

1. Gallery opens without creating an ARCore session.
2. Capture screen requests camera permission only when the user starts capture.
3. The app checks ARCore availability.
4. If install/update is required, the user-visible ARCore install flow is requested.
5. A `Session` is created only after permission and ARCore readiness.
6. Depth mode is configured: prefer `RAW_DEPTH_ONLY`, fallback to `AUTOMATIC` only when supported, otherwise report depth unsupported.
7. A minimal GL pump binds an external camera texture and calls `Session.update()`.
8. Accepted tracked raw-depth frames are mapped to `FramePacket` and written to DFR through `capture-store`.
9. Stop capture pauses the session and validates the DFR project.

## Preview

The MVP does not render a camera preview. The capture surface exists to provide the GL context and camera texture required by ARCore. Camera preview rendering is a follow-up and must not be faked.

## Local-First Policy

ARCore and Google Play Services for AR availability/install/update checks are OS/service-level runtime dependencies. DenseFrame does not add app-owned cloud processing, hidden app network calls, analytics, Cloud Anchors, Geospatial APIs, or shared camera features.
