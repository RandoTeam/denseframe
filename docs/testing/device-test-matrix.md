# Device Test Matrix

## Primary Real Device

- OnePlus 13: primary month-one real-device test target.
- Manual ARCore raw-depth checklist: `docs/testing/manual-arcore-capture-oneplus13.md`.

## Manual Test Categories

- Install and first launch.
- ARCore support detection.
- Camera permission handling.
- Object capture.
- Room capture.
- Tracking loss and relocalization.
- Low light and motion blur.
- Depth confidence variation.
- Storage pressure during capture.
- Thermal warning and throttling behavior.
- Process death during capture.
- Process death during processing.
- Raw project validation and recovery.
- ARCore availability, install/update, permission, selected depth mode, tracking, depth freshness, and DFR payload validation.
- Point cloud preview.
- Mesh viewer gestures.
- Export success and cancellation.

## Documentation

Each manual run should record device, OS version, app build, scenario, result, logs location, and any raw project diagnostic bundle path.
