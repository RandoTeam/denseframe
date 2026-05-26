# Future Module Map

This document defines intended boundaries only. Do not create Gradle modules until the Android skeleton task.

## Planned Areas

- `app`: Android entry point, navigation, permissions, and dependency assembly.
- `core-model`: shared immutable models, state machines, result types, and serialized parameters.
- `capture-api`: capture contracts independent of ARCore.
- `capture-arcore`: ARCore adapter for depth, confidence, pose, intrinsics, and tracking.
- `storage-dfr`: DenseFrame Raw Project read/write, manifests, checksums, migrations.
- `reconstruction-core`: deterministic unprojection, point clouds, TSDF, mesh extraction.
- `viewer`: touch-first 3D viewer and render integration.
- `export`: DFRZ, PLY, GLB, and diagnostics packaging.
- `testing-support`: synthetic frames, golden fixtures, failure injection helpers.

## Boundary Rules

- Capture does not depend on UI.
- Reconstruction reads storage through streaming interfaces.
- Viewer consumes processed artifacts, not live ARCore sessions.
- Storage owns crash safety and checksums.
