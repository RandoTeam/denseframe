# Product Flow

DenseFrame month-one UX follows:

Gallery -> New Scan -> Mode Select -> Capture -> Save Review -> Processing -> Viewer -> Export.

## Gallery

User actions:

- Start `New Scan`.
- Open a recent project.
- Retry a failed or interrupted project recovery.
- Inspect local storage status.

App states:

- Empty: no projects yet, primary `New Scan` action.
- Populated: local project list with status, preview placeholder/artifact, date, size, and last processing state.
- Recoverable: project has valid raw data but incomplete processing.
- Failed: manifest or checksum issue needs diagnostics.

Error states:

- Storage unavailable.
- Project cannot be opened.
- Manifest checksum mismatch.

## New Scan and Mode Select

User actions:

- Choose object scan, room scan, or quick scene scan.
- Review concise mode-specific instructions.
- Grant camera and required runtime permissions.

App states:

- Ready: device support and permissions pass.
- Permission required: explain why camera access is needed.
- Unsupported: ARCore/depth support missing or unverified.
- Low storage or thermal caution: user can fix before capture.

Empty states:

- No modes hidden behind setup; unsupported modes explain why they are unavailable.

## Capture

User actions:

- Start capture.
- Move around object or room following HUD guidance.
- Pause or stop capture.
- Discard or save raw project.

App states:

- Checking support.
- Tracking.
- Limited tracking.
- Tracking lost.
- Depth unavailable.
- Paused.
- Saving frame.
- Stopping.

Error states:

- Camera permission denied.
- ARCore session failed.
- Storage write failed.
- Thermal state requires pause.
- Too many dropped frames for useful scan.

## Save Review

User actions:

- Review frame count, dropped frames, warnings, storage size, and estimated quality.
- Rename project.
- Start local processing.
- Keep raw only.
- Delete project.

App states:

- Raw saved.
- Raw saved with warnings.
- Raw validation failed.
- Processing available.
- Processing blocked until repair or retry.

## Processing

User actions:

- Start, cancel, retry, or resume processing.
- Keep using the device while progress is checkpointed.

App states:

- Queued.
- Running validation.
- Selecting keyframes.
- Generating point cloud.
- Checkpointing.
- Cancel requested.
- Cancelled.
- Completed.
- Failed with diagnostics.

Error states:

- Checksum failure.
- Unsupported format version.
- Out of memory risk.
- Insufficient storage for artifacts.

## Viewer

User actions:

- Orbit, pan, zoom, reset view, inspect metadata, and open export.
- Switch between raw summary and processed artifact when available.

App states:

- Loading artifact.
- Empty artifact.
- Point cloud ready.
- Mesh placeholder/deferred.
- Unsupported artifact.
- Render failure.

## Export

User actions:

- Choose raw archive, point cloud, or scene export when available.
- Cancel export.
- Share or save through explicit user action.

App states:

- Preparing temp output.
- Writing.
- Finalizing atomic publish.
- Completed.
- Cancelled with cleanup.
- Failed with diagnostics.
