# DenseFrame Product Brief

DenseFrame is a local-first Android 3D scanning app for consumers who want private, restartable, on-device capture and reconstruction.

## Target User

- Makers and hobbyists who need quick object scans for reference, repair, prototyping, or creative reuse.
- Home users who want simple room or scene captures without uploading private spaces.
- Developers and technical users who value raw project access and deterministic local processing.
- Early testers with supported ARCore depth devices, with OnePlus 13 as the primary month-one real-device target.

## Main Use Cases

Month one supports three scanning intents with different guidance and quality expectations.

### Object Scan

Capture a small to medium object by walking around it. The app emphasizes coverage, steady motion, depth confidence, and reflective/transparent surface warnings.

### Room Scan

Capture a room-scale space for reference. The app emphasizes tracking stability, broad coverage, storage growth, thermal state, and resumable local processing.

### Quick Scene Scan

Capture a fast spatial snapshot when perfect reconstruction is not required. The app prioritizes short guidance, raw save reliability, and immediate point cloud preview.

## Product Promise

- The raw project is saved locally before processing.
- Capture quality limits are visible during capture.
- Processing is local, cancellable, deterministic where practical, and restartable.
- The viewer is touch-first and polished enough for consumer use.
- Export is explicit and user controlled.

## UX Principles

- Start from the Gallery and make `New Scan` obvious.
- Use guided mode cards for object, room, and quick scene scans.
- Keep the camera view dominant during capture.
- Show quality feedback as actionable states, not vague warnings.
- Prefer one primary action per step.
- Preserve user trust by showing local storage, processing, and export status clearly.
- Never imitate competitor branding, screenshots, icons, or proprietary visual language.

## MVP Boundaries

Included:

- Gallery, mode select, capture HUD, save review, local processing, point cloud viewer, raw/scene export entry points.
- DFR v1 raw project format.
- Explicit state machines.
- ARCore depth as the first verified capture source.
- Deterministic point cloud MVP.

Excluded:

- Required cloud processing.
- TSDF mesh quality target.
- Texture baking.
- Measurements.
- Gaussian splatting or neural rendering.
- Community sharing, accounts, or cloud sync.

## Success Metrics

- First launch to `New Scan` is understandable without onboarding screens.
- A supported device can create a DFR raw project without corrupting files after interruption.
- Capture HUD gives actionable feedback for tracking, depth, motion, storage, and thermal issues.
- Processing can be cancelled and restarted without losing the raw project.
- A deterministic point cloud can be regenerated from the same raw project and parameters.
- Viewer supports smooth touch inspection for MVP-scale projects.
- Export completes through a temp-file-first path and reports failures clearly.
