# Capture Frame Packet

`modules/capture-api` defines pure Kotlin capture contracts. It does not implement ARCore, CameraX, Android camera APIs, storage writes, or scanner behavior.

## CaptureSource

`CaptureSource` is a future adapter boundary. Implementations report:

- `sourceId`
- `supportedModes`
- `supports(preset)`

ARCore support checks belong in a future adapter, not in this pure API module.

## FramePacket

`FramePacket` groups one accepted capture frame:

- `timestamp`: `FrameTimestamp`
- `intrinsics`: `CameraIntrinsics`
- `pose`: `PoseMatrix4x4`
- `depth`: optional `DepthFrame`
- `confidence`: optional `ConfidenceFrame`
- `color`: optional `ColorFramePayload`
- `quality`: `FrameQualityMetrics`

The API permits missing depth, confidence, or color metadata so adapters can report unavailable data explicitly.

## Timestamp

`FrameTimestamp` fields:

- `sensorNanos`: source sensor timestamp.
- `receivedNanos`: app-side receipt timestamp.

Both values are non-negative nanosecond counters. The API does not assume a wall-clock epoch.

## CameraIntrinsics

Fields:

- `width`
- `height`
- `fx`
- `fy`
- `cx`
- `cy`

Units are pixels. Dimensions and focal values must be positive.

## PoseMatrix4x4

Convention:

```text
T_world_camera_column_major
p_world = T_world_camera * p_camera
```

`PoseMatrix4x4.valuesColumnMajor` contains exactly 16 floats. Coordinate axis signs must be verified in the ARCore adapter before implementation.

## DepthFrame

Fields:

- `width`
- `height`
- `depthU16`
- `metersPerUnit`

`depthU16` contains `width * height * 2` bytes. The pure capture API does not hard-code an ARCore byte order. The ARCore adapter supplies deterministic contiguous row-major little-endian millimeter bytes after removing row padding. The storage bridge records the DFR payload format as `DEPTH_U16_MILLIMETERS_LITTLE_ENDIAN` only when the producing adapter has already supplied bytes in that format.

## ConfidenceFrame

Fields:

- `width`
- `height`
- `confidenceU8`

`confidenceU8` contains `width * height` bytes.

When stored through `modules/capture-store`, confidence payloads are tagged as `CONFIDENCE_U8_LINEAR_0_255`.

## ColorFramePayload

Metadata only:

- `format`
- `width`
- `height`
- `byteCount`

The pure capture API does not encode images or own color payload bytes.

## FrameQualityMetrics

Fields:

- `trackingState`
- `depthConfidence`
- `coverage`
- `motionRisk`
- `blurRisk`

Metric values are normalized to `0..1`. Tracking loss is represented by `TrackingState.LOST` and must also drive the state machine to `PAUSED_TRACKING_LOST` during capture.

The ARCore MVP currently sets motion and blur risk to `0` because no verified device-side estimator is implemented yet. Tracking loss, raw depth absence, and storage drops are surfaced through ARCore result types and HUD metrics.

## DFR Storage Mapping

`modules/capture-store` maps `FramePacket` to DFR v1 without making `capture-api` depend on storage:

- `timestamp.sensorNanos` -> DFR `timestampNanos`.
- `pose.valuesColumnMajor` -> DFR `poseMatrix` unchanged, preserving `T_world_camera_column_major`.
- `intrinsics` -> DFR `cameraIntrinsics`.
- `quality.trackingState` -> DFR `trackingState` plus accepted/drop metadata.
- Present `DepthFrame` bytes -> `depth_u16.bin`; missing depth -> `payloads.depthU16: null`.
- Present `ConfidenceFrame` bytes -> `confidence_u8.bin`; missing confidence -> `payloads.confidenceU8: null`.
- `ColorFramePayload` remains metadata only until a real color payload owner exists; the bridge does not fabricate `color.yuv`.

The bridge rejects frames with invalid dimensions, invalid payload byte counts, mismatched depth/confidence dimensions, unusable tracking when policy requires tracked frames, and non-monotonic timestamps when strict mode is enabled.
