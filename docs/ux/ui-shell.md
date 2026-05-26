# Android UI Shell

The initial Android shell implements the month-one product flow without pretending that scanning exists.

## Screens

- `ProjectGalleryScreen`: local-first home surface with `New Scan`, empty state, storage status, and clearly marked UI preview sample.
- `ModeSelectScreen`: object, room, and quick scene mode cards with concise mode-specific guidance.
- `CaptureShellScreen`: camera-preview placeholder with HUD indicators for tracking, depth, confidence, coverage, and motion risk.
- `SaveReviewScreen`: raw project review placeholder for future frame counts, checksum status, warnings, and processing action.
- `ProcessingScreen`: local processing placeholder for validation, keyframes, and point cloud progress.
- `SceneViewerShellScreen`: viewer placeholder with orbit/pan/zoom/reset/inspect/export surface language.
- `ExportSheet`: bottom sheet placeholder that documents temp-file-first export expectations.

## Design System Use

The shell uses `modules/design-system` for:

- Dark spatial background.
- Semantic status colors.
- Premium card surfaces.
- Primary buttons.
- Status pills.
- Metric bars.
- Typography tuned for compact operational UI.

## Boundaries

- No ARCore, CameraX, Filament, Room, analytics, cloud, or network dependencies are included.
- Sample text is marked as UI preview/sample and is not scanner data.
- Navigation is local Compose state only.
- The capture surface is intentionally a shell; it does not fake scanning, frame capture, storage, reconstruction, or rendering.

## Follow-Up UX Checks

- Validate text fit on small phones after the first emulator/device run.
- Add real accessibility labels once final control icons and semantics exist.
- Add screenshot review after the app can run in a local emulator or on OnePlus 13.
