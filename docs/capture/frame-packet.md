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

`depthU16` contains `width * height * 2` bytes. The module does not define endian conversion yet; the future adapter must document it before storage integration.

## ConfidenceFrame

Fields:

- `width`
- `height`
- `confidenceU8`

`confidenceU8` contains `width * height` bytes.

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
