# DenseFrame Raw Project v1

DFR v1 is a folder-based raw project format designed for crash-safe local capture and deterministic processing.

## Folder Layout

```text
project.dfr/
  manifest.json
  manifest.checksum
  frames/
    00000001/
      color.bin
      depth.bin
      confidence.bin
      frame.json
      checksums.json
  processing/
    checkpoints/
  diagnostics/
```

## Manifest Fields

- `format`: `denseframe.raw`
- `version`: `1`
- `projectId`
- `createdAt`
- `updatedAt`
- `device`
- `captureMode`: object or room
- `coordinateConvention`
- `frameCount`
- `frames`: ordered frame references
- `reconstructionParameters`
- `checksums`

## Frame Fields

- `frameId`
- `timestampNanos`
- `trackingState`
- `cameraPose`
- `cameraIntrinsics`
- `depthSize`
- `colorSize`
- `confidenceEncoding`
- `accepted`
- `dropReason`
- `payloadChecksums`

## Crash Safety

Writers must create temp payloads first, flush/fsync where practical, write checksums, then atomically rename into place. Manifest updates must also use temp write and atomic rename.

## Versioning

Readers must reject unknown major versions, preserve unknown compatible metadata where practical, and route migrations through documented source and target versions.
