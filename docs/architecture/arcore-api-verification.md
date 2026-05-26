# ARCore API Verification

This document records the ARCore APIs used by the first DenseFrame raw-depth capture adapter.

Sources used:

- Official Google ARCore Android docs and Java reference.
- Google Maven metadata for `com.google.ar:core`.
- Local Gradle dependency resolution with `com.google.ar:core:1.54.0`.

## Dependency

- Coordinate: `com.google.ar:core:1.54.0`.
- Version source: Google Maven metadata.
- Added only to `modules/capture-arcore`.

## Manifest Entries

- Camera permission: `android.permission.CAMERA`.
- AR Optional metadata:

```xml
<meta-data android:name="com.google.ar.core" android:value="optional" />
```

DenseFrame uses AR Optional so gallery, docs, local projects, and non-capture surfaces can open on unsupported devices. Capture itself still checks ARCore availability and depth support at runtime.

## Runtime Install and Update Flow

Verified APIs:

- `ArCoreApk.getInstance().checkAvailability(context)`
- `ArCoreApk.getInstance().requestInstall(activity, userRequestedInstall)`
- `ArCoreApk.Availability`
- `ArCoreApk.InstallStatus`

## Session Lifecycle

Verified APIs:

- `Session(context)`
- `Session.resume()`
- `Session.pause()`
- `Session.close()`
- `Session.update()`
- `Session.setCameraTextureName(int)`
- `Session.setDisplayGeometry(displayRotation, widthPx, heightPx)`

`Session.update()` requires a GL context and camera texture. The MVP uses a minimal `GLSurfaceView` pump and does not render a camera preview yet.

## Depth Configuration

Verified APIs:

- `Session.isDepthModeSupported(Config.DepthMode.RAW_DEPTH_ONLY)`
- `Session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)`
- `Config.depthMode`
- `Session.configure(config)`

DenseFrame prefers `RAW_DEPTH_ONLY`. It falls back to `AUTOMATIC` only when raw depth is unsupported and automatic depth is supported.

## Raw Depth and Confidence

Verified APIs:

- `Frame.acquireRawDepthImage16Bits()`
- `Frame.acquireRawDepthConfidenceImage()`

Depth is `HardwareBuffer.D_16`, one 16-bit plane, little-endian millimeters, sparse, with missing depth represented as zero. Confidence is Y8 unsigned `0..255`.

All acquired `Image` objects must be closed. DenseFrame strips row padding and rejects unexpected pixel stride.

## Pose, Tracking, and Intrinsics

Verified APIs:

- `Frame.getCamera()`
- `Camera.getTrackingState()`
- `Camera.getTrackingFailureReason()`
- `Camera.getPose()`
- `Pose.toMatrix(floatArray, offset)`
- `Camera.getImageIntrinsics()`
- `CameraIntrinsics.getImageDimensions()`
- `CameraIntrinsics.getFocalLength()`
- `CameraIntrinsics.getPrincipalPoint()`

`Camera.getPose()` is used only when tracking is `TRACKING`. `Pose.toMatrix` writes a 4x4 column-major matrix. DenseFrame stores it unchanged as `T_world_camera_column_major`.

## Exceptions Handled

The adapter maps verified ARCore/runtime exceptions into typed errors, including camera permission, install/update, unsupported device/configuration, camera unavailable, paused session, missing GL context, missing texture, no data yet, not tracking, resource exhaustion, deadline exceeded, illegal state, and fatal ARCore errors.

## Deferred

- Camera preview rendering.
- CPU color image capture.
- Persisting raw depth timestamp/freshness in DFR metadata.
- Instrumented ARCore device tests.
- Shared camera, CameraX, Sceneform, Cloud Anchors, Geospatial APIs, Filament, reconstruction, and export.
