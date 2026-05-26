# DFR v1 JSON Examples

These examples show field names used by the initial `modules/project-store` implementation.

## Empty Manifest

```json
{
  "format": "denseframe.raw",
  "schemaVersion": 1,
  "projectId": "project-test",
  "createdAtEpochMillis": 1700000000000,
  "updatedAtEpochMillis": 1700000000000,
  "device": {
    "manufacturer": "DenseFrame",
    "model": "JUnit",
    "osVersion": "test"
  },
  "capture": {
    "mode": "object",
    "coordinateConvention": "world_from_camera_column_major"
  },
  "reconstructionDefaults": {
    "minDepthMeters": 0.2,
    "maxDepthMeters": 5.0,
    "minConfidence": 128
  },
  "frames": []
}
```

## Frame Manifest

```json
{
  "frameId": 1,
  "timestampNanos": 123456789,
  "trackingState": "tracking",
  "cameraIntrinsics": {
    "width": 2,
    "height": 2,
    "fx": 100.0,
    "fy": 101.0,
    "cx": 1.0,
    "cy": 1.5
  },
  "poseMatrix": [
    1.0, 0.0, 0.0, 0.0,
    0.0, 1.0, 0.0, 0.0,
    0.0, 0.0, 1.0, 0.0,
    0.0, 0.0, 0.0, 1.0
  ],
  "quality": {
    "depthConfidence": 0.75,
    "coverage": 0.5,
    "motionRisk": 0.1,
    "accepted": true,
    "dropReason": null
  },
  "payloads": {
    "colorYuv": "color.yuv",
    "depthU16": "depth_u16.bin",
    "confidenceU8": "confidence_u8.bin"
  },
  "checksums": [
    {
      "path": "color.yuv",
      "algorithm": "sha256",
      "value": "039058c6f2c0cb492c533b0a4d14ef77cc0f78abccced5287d84a1a2011cfb81"
    }
  ]
}
```

## Checksum File

```text
sha256 039058c6f2c0cb492c533b0a4d14ef77cc0f78abccced5287d84a1a2011cfb81 color.yuv
sha256 c6d44cf418f610e3fe9e1d9294ff43def81c6cdcad6cbb1820cff48d3aa4355d depth_u16.bin
sha256 73907589101a7e8ab83178e7db2997aab7272cd02d364e8e3ecc2beccda4b631 confidence_u8.bin
```
