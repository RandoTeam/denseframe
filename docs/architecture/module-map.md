# Module Map

This document defines the current Android skeleton boundaries. Placeholder modules exist to reserve ownership without adding implementation dependencies yet.

## Current Modules

- `app`: Android entry point, navigation, permissions, and dependency assembly.
- `modules/design-system`: Compose theme, semantic colors, typography, and reusable UI components.
- `modules/project-store`: placeholder for DenseFrame Raw Project read/write, manifests, checksums, atomic writes, and migrations.
- `modules/capture-api`: placeholder for capture contracts independent of ARCore and CameraX.
- `modules/capture-arcore`: placeholder for future ARCore depth adapter. No ARCore dependency yet.
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
- Reconstruction reads storage through streaming interfaces.
- Viewer consumes processed artifacts, not live ARCore sessions.
- Storage owns crash safety and checksums.
- Placeholder modules must not grow hidden runtime dependencies.
