# Module Map

This document defines the current Android skeleton boundaries. Placeholder modules exist to reserve ownership without adding implementation dependencies yet.

## Current Modules

- `app`: Android entry point, navigation, permissions, and dependency assembly.
- `modules/design-system`: Compose theme, semantic colors, typography, and reusable UI components.
- `modules/project-store`: DenseFrame Raw Project read/write, manifests, checksums, atomic writes, optional payload references, validation, and migrations.
- `modules/capture-api`: pure capture contracts independent of ARCore, CameraX, Android camera APIs, and storage.
- `modules/capture-store`: deterministic adapter from `capture-api` `FramePacket` values to `project-store` DFR v1 frame writes. It may depend on `capture-api` and `project-store`; neither dependency points back to it.
- `modules/capture-arcore`: real early-alpha ARCore raw-depth adapter. Owns ARCore availability, install/update mapping, depth support selection, session control, image packing, pose/intrinsics/tracking mapping, and frame packet creation.
- `modules/capture-camerax`: placeholder for future CameraX integration if needed. No CameraX dependency yet.
- `modules/reconstruction-api`: placeholder for deterministic reconstruction contracts and serializable parameters.
- `modules/viewer-filament`: placeholder for future Filament viewer integration. No Filament dependency yet.
- `modules/export`: placeholder for DFRZ, PLY, GLB, and diagnostics packaging.
- `modules/diagnostics`: placeholder for diagnostics models, reports, and failure summaries.
- `modules/testing-fixtures`: placeholder for synthetic frames, golden fixtures, and failure injection helpers.

## Deferred Modules or Renames

- `core-model` is deferred until shared models have a real second consumer.
- `storage-dfr` remains the conceptual storage boundary, but the initial module name is `modules/project-store` to match user-facing product language.
- `reconstruction-core` remains the future implementation boundary, while `modules/reconstruction-api` reserves contracts first.
- `viewer` remains the conceptual UX boundary, while `modules/viewer-filament` reserves the future renderer integration.

## Boundary Rules

- Capture does not depend on UI.
- `capture-api` does not depend on storage.
- `capture-store` does not depend on ARCore, CameraX, Android camera APIs, or UI.
- Reconstruction reads storage through streaming interfaces.
- Viewer consumes processed artifacts, not live ARCore sessions.
- Storage owns crash safety and checksums.
- Placeholder modules must not grow hidden runtime dependencies.
