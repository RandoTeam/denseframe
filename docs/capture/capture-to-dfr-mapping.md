# Capture to DFR Mapping

`modules/capture-store` is the integration boundary between pure capture models and crash-safe DFR v1 storage.

## Architecture

- `modules/capture-api` remains pure and has no dependency on storage.
- `modules/project-store` owns DFR models, atomic writes, checksums, readers, and validators.
- `modules/capture-store` depends on both modules and contains deterministic mapping plus write policy.
- No ARCore, CameraX, Android camera, network, analytics, or fake scanner behavior belongs in this bridge.

## Field Mapping

| Capture field | DFR field | Notes |
| --- | --- | --- |
| `FramePacket.timestamp.sensorNanos` | `FrameManifest.timestampNanos` | Sensor timestamp is preserved as a nanosecond counter. |
| `FramePacket.pose.valuesColumnMajor` | `poseMatrix` | Values are copied unchanged using `T_world_camera_column_major`. |
| `FramePacket.intrinsics` | `cameraIntrinsics` | Pixel units are preserved. |
| `FrameQualityMetrics.trackingState` | `trackingState` | Stored as lowercase enum name. |
| `FrameQualityMetrics` | `quality` | Depth confidence, coverage, motion risk, accepted/drop metadata. |
| `DepthFrame.depthU16` | `depth_u16.bin` | Written only when present. Format: `DEPTH_U16_MILLIMETERS_LITTLE_ENDIAN`. |
| `ConfidenceFrame.confidenceU8` | `confidence_u8.bin` | Written only when present. Format: `CONFIDENCE_U8_LINEAR_0_255`. |
| `ColorFramePayload` | `payloads.colorFormat` | Metadata only until a real color byte owner exists; no `color.yuv` is fabricated. |

For the ARCore MVP, only tracked frames with both raw depth and confidence are written by default. No-depth and tracking-lost events update HUD metrics but are not fabricated into DFR payloads.

## Validation

The bridge rejects writes when:

- pose matrix size is not 16 values;
- intrinsics `fx` or `fy` are non-positive;
- depth or confidence dimensions are invalid;
- payload byte counts do not match declared dimensions and format;
- depth and confidence dimensions differ when both are present;
- tracking is not `TRACKING` while policy requires tracked frames;
- timestamp is not strictly greater than the prior accepted frame timestamp while strict monotonic mode is enabled.

Some invalid shapes are already rejected by `capture-api` constructors. The bridge keeps equivalent validation checks so the storage boundary remains explicit.

## Write Policy

`CaptureToDfrWritePolicy` controls:

- whether tracked frames are required;
- whether missing depth, confidence, or color is allowed;
- whether timestamps must be strictly monotonic;
- storage payload format names for depth and confidence.

Every accepted write goes through `DfrProjectWriter.appendFrame`, so frame folders inherit temp-file writes, atomic frame-directory publication, manifest updates, and SHA-256 checksums.
