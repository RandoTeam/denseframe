# DenseFrame Month-One Roadmap

Month one turns the repository control plane into a polished local Android scanning MVP without expanding into cloud, social, or advanced reconstruction work.

## M0: Repo, Control Plane, and Docs

Target: week 0 baseline.

Scope:

- Repository rules, Codex workflow, agent roles, skills, prompt pack, ADRs, and initial product/architecture docs.
- Local-first policy, DFR v1 draft, state machine draft, and public competitor pattern boundaries.

Acceptance criteria:

- Required control-plane files exist.
- `.codex/config.toml` uses workspace-write sandbox and on-request approvals.
- Skills include required front matter.
- Markdown files have meaningful content.
- No Android source, Gradle modules, app dependencies, or runtime APIs are introduced.

## Week 1: M1 Android Skeleton and Design System

Scope:

- Verify official Gradle, Android plugin, Kotlin, Compose, and minimum SDK guidance before implementation.
- Create minimal Android project skeleton.
- Build polished Compose shell for Gallery, New Scan, Mode Select, Save Review placeholder, Processing placeholder, Viewer placeholder, and Export entry point.
- Define dark spatial visual system tokens and reusable UI components.

Acceptance criteria:

- App launches to a local Gallery with empty state and `New Scan` action.
- Mode Select presents object, room, and quick scene scan cards with concise mode-specific guidance.
- Navigation covers the MVP flow without real capture.
- UI has loading, empty, error, disabled, and accessibility states where relevant.
- No cloud, analytics, or hidden network dependency exists.
- Applicable Gradle checks pass.

## Week 2: M2 Raw Project Store and Capture State Machine

Scope:

- Implement DFR v1 project creation, manifest updates, frame metadata model, checksums, and crash-safe writes.
- Implement explicit `ProjectState`, `CaptureSessionState`, and `ProcessingJobState` transitions.
- Add bounded capture-to-storage queue contracts without ARCore implementation.

Acceptance criteria:

- Raw project writes use temp file, flush/fsync where practical, atomic rename, and checksum.
- Large projects are read through streaming interfaces, not full memory loads.
- State machine tests cover valid transitions, invalid transitions, cancellation, restart, and failure.
- Storage tests cover partial writes, checksum mismatch, version rejection, and manifest recovery.
- Docs remain synchronized with behavior.

## Week 3: M3 ARCore Capture to DFR

Scope:

- Verify official ARCore APIs and dependency versions.
- Implement ARCore support checks, permission flow, tracking state handling, timestamps, pose, intrinsics, raw depth, confidence, and frame acceptance policy.
- Persist accepted frames to DFR v1 through bounded queues.
- Add real-device manual test script with OnePlus 13 as the primary target.

Acceptance criteria:

- Unsupported devices and missing depth capability show clear product states.
- Tracking loss pauses frame acceptance and resumes only when policy allows.
- Accepted frames include timestamp, pose, intrinsics, depth, confidence, tracking state, and checksums.
- Capture HUD shows tracking, depth confidence, coverage, motion/blur risk, accepted/dropped frames, storage, and thermal indicators.
- OnePlus 13 manual smoke test is documented, even if full automation is not available.

## Week 4: M4 Point Cloud Processing, Viewer, and Export MVP

Scope:

- Implement deterministic validation, keyframe selection, depth unprojection, confidence filtering, and point cloud generation.
- Implement viewer MVP for point cloud artifacts with orbit, pan, zoom, reset, metadata, loading, empty, unsupported, and failure states.
- Implement first export path for raw archive and point cloud format.

Acceptance criteria:

- Reconstruction parameters are serializable.
- Golden synthetic tests cover intrinsics, unprojection, transforms, invalid depth, and confidence filtering.
- Processing is cancellable and restartable from checkpoints.
- Viewer handles large point clouds with bounded memory strategy documented.
- Export writes temp files first, supports cancellation, removes partial output, and records metrics.

## Explicitly Deferred

- TSDF mesh quality pipeline.
- Texture baking and UV workflows.
- Measurement tools.
- Gaussian splatting or neural rendering.
- Cloud sync, cloud reconstruction, accounts, and community sharing.
- Marketplace publishing or social feeds.
