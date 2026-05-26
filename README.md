# DenseFrame

DenseFrame is a local-first Android 3D scanning app for capturing objects or rooms, saving raw scan projects, processing them on-device, and exporting raw or 3D scene files.

## Project Goals

- Make the primary flow simple: open app, scan, save raw project, process locally, view, export.
- Preserve raw capture data before reconstruction so work can be resumed and reprocessed.
- Keep reconstruction deterministic for the same raw project, code version, and serialized parameters where practical.
- Provide a polished consumer-grade Android UX with clear capture guidance and touch-first viewing.
- Use explicit state machines, crash-safe storage, bounded queues, and restartable long-running jobs.

## Non-Goals

- Required cloud reconstruction or runtime cloud processing.
- Hidden network calls or analytics SDKs.
- Community sharing, feeds, accounts, or cloud sync.
- Gaussian splatting, neural rendering, texture baking, measurements, or TSDF mesh quality in the first MVP.
- Copying proprietary assets, product names, screenshots, icons, branding, code, or implementation details from public reference products.

## Local-First Promise

DenseFrame scan data stays local unless the user explicitly exports or shares it. Capture, raw project save, local processing, viewing, and export must work without network access. Future network behavior requires an ADR, clear user-facing disclosure, and an explicit user action.

## MVP User Flow

1. Gallery opens with recent local projects and a primary `New Scan` action.
2. User selects mode: object scan, room scan, or quick scene scan.
3. App checks device support, permissions, storage, and thermal readiness.
4. Capture HUD guides tracking, depth confidence, coverage, motion risk, accepted/dropped frames, storage, and thermal state.
5. Raw project is written continuously to DFR v1 using crash-safe writes and checksums.
6. Save Review summarizes captured frames, quality, size, and warnings.
7. User starts local processing.
8. Viewer opens with point cloud MVP, project metadata, and touch controls.
9. User exports DFRZ/raw archive and first scene formats as they become available.

## Technical Stack

- Android app target: Kotlin and Jetpack Compose.
- Capture target: ARCore depth as the first verified capture source.
- Storage target: DenseFrame Raw Project v1 folder format with manifests, frame payloads, checksums, and versioning.
- Reconstruction MVP: deterministic validation, keyframe selection, depth unprojection, confidence filtering, and point cloud generation.
- Viewer/export target: Filament and glTF/GLB after official API and dependency verification.

All platform and library APIs must be verified against official documentation or source before implementation.

Current skeleton choices:

- Android Gradle Plugin `8.13.2`.
- Kotlin `2.2.21`.
- Gradle wrapper `8.14.3`.
- Compile SDK `36`, target SDK `36`, minimum SDK `26`.
- Compose BOM `2026.02.00` and Activity Compose `1.10.1`.

These versions were selected from official Android/Gradle guidance and locally available caches. Compose and Activity versions are conservative local choices and should be rechecked against official release notes before broadening the UI stack.

## Planned Architecture Modules

Current Gradle modules:

- `app`: entry point, navigation, permissions, and dependency assembly.
- `modules/design-system`: theme, colors, typography, and reusable Compose components.
- `modules/project-store`: DFR v1 models, atomic writes, manifest/frame IO, streaming checksums, reader, and validator.
- `modules/capture-api`: pure capture contracts, frame packet models, and deterministic session reducer.
- `modules/capture-store`: deterministic bridge from pure `FramePacket` models into DFR v1 frame folders using project-store crash-safe writes.
- `modules/capture-arcore`: placeholder for the future ARCore adapter.
- `modules/capture-camerax`: placeholder for future CameraX support if approved.
- `modules/reconstruction-api`: placeholder for reconstruction contracts.
- `modules/viewer-filament`: placeholder for future Filament viewer integration.
- `modules/export`: DFRZ, PLY, GLB, and diagnostics packaging.
- `modules/diagnostics`: placeholder for diagnostics surfaces and bundles.
- `modules/testing-fixtures`: placeholder for synthetic fixtures and failure injection helpers.

## Current Status

This repository contains the control plane, product docs, architecture docs, ADRs, agent definitions, repository skills, staged Codex prompts, an initial Android multi-module Compose shell, a DFR v1 project-store implementation, pure capture API state machines, and a tested capture-to-DFR storage bridge. The shell does not implement scanning, reconstruction, Filament rendering, CameraX, ARCore, Room, analytics, cloud, or network behavior.

## Development Setup

Prerequisites:

- JDK 21.
- Android SDK with platform 36 installed.

Useful commands:

```powershell
.\gradlew.bat projects
.\gradlew.bat test
.\gradlew.bat assembleDebug
```

The first run may need dependency access if the local Gradle cache is incomplete.

## Quality Warning

Scan quality depends on device tracking, lighting, reflective or transparent surfaces, depth confidence, capture distance, capture coverage, thermal state, available storage, and user motion. The app should surface these limits clearly instead of implying every scan can produce a clean model.

## License

Apache-2.0. See `LICENSE`.
