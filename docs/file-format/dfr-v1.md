# DenseFrame Raw Project v1

DFR v1 is a folder-based raw project format designed for crash-safe local capture and deterministic processing. The initial implementation lives in `modules/project-store`.

## Folder Layout

```text
project.dfr/
  manifest.json
  frames/
    00000001/
      frame.json
      color.yuv
      depth_u16.bin
      confidence_u8.bin
      checksum.sha256
  reconstruction/
  thumbnails/
```

## Schema

The root manifest uses:

- `format`: always `denseframe.raw`.
- `schemaVersion`: explicit integer schema version. Current value is `1`.
- `projectId`: stable local project identifier.
- `createdAtEpochMillis`: project creation time.
- `updatedAtEpochMillis`: last manifest update time.
- `device`: `DeviceInfo`.
- `capture`: `CaptureInfo`.
- `reconstructionDefaults`: `ReconstructionDefaults`.
- `frames`: ordered `FrameManifest` entries.

Unknown future manifest fields must be ignored by v1 readers when known required fields are still valid.

## DeviceInfo

- `manufacturer`
- `model`
- `osVersion`

## CaptureInfo

- `mode`: scan mode such as `object`, `room`, or `quick_scene`.
- `coordinateConvention`: documented pose convention, for example `world_from_camera_column_major`.

## ReconstructionDefaults

- `minDepthMeters`
- `maxDepthMeters`
- `minConfidence`

These are defaults only. Future reconstruction jobs must serialize their exact parameters separately.

## FrameManifest

Each frame has a `frame.json` and a matching entry in `manifest.json`.

- `frameId`: one-based integer, rendered as an 8-digit directory name.
- `timestampNanos`
- `trackingState`
- `cameraIntrinsics`
- `poseMatrix`
- `quality`
- `payloads`
- `checksums`

## CameraIntrinsics

- `width`
- `height`
- `fx`
- `fy`
- `cx`
- `cy`

## PoseMatrix

`poseMatrix` is a 16-number array. The initial convention is named by `capture.coordinateConvention`; code must not infer row/column order from this document alone.

## FrameQuality

- `depthConfidence`
- `coverage`
- `motionRisk`
- `accepted`
- `dropReason`

## FramePayloadRefs

- `colorYuv`: `color.yuv`
- `depthU16`: `depth_u16.bin`
- `confidenceU8`: `confidence_u8.bin`

Payloads are referenced by relative path and must be read as streams. Readers must not load full projects into memory.

## Checksum

Each checksum entry uses:

- `path`
- `algorithm`: currently `sha256`.
- `value`: lowercase hex digest.

`checksum.sha256` repeats the frame payload checksums in line format:

```text
sha256 <hex> <relative-path>
```

## Crash Safety

Writers must:

1. Write payload files through temp files.
2. Flush/fsync files where practical.
3. Write `frame.json`.
4. Write `checksum.sha256`.
5. Move the temporary frame directory into `frames/00000001` only after required files exist.
6. Update `manifest.json` through temp file and atomic rename.

A frame directory is considered complete only when `frame.json`, payload files, and `checksum.sha256` are present.

## Reader and Validator Behavior

- Reject unsupported `format` or `schemaVersion`.
- Detect incomplete frame folders.
- Detect frame folders not referenced by manifest.
- Verify checksums by streaming payload files.
- Ignore unknown future manifest fields.

## Versioning

Readers must reject unknown major schema versions. Migrations must be explicit source-to-target conversions and preserve raw payloads.
