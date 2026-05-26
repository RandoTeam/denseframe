# State Machines

All state machines must be explicit and tested before production use.

## ProjectState

States:

- `New`
- `Capturing`
- `CapturePaused`
- `RawSaved`
- `ProcessingQueued`
- `Processing`
- `Processed`
- `Exporting`
- `Exported`
- `Failed`
- `Archived`

Allowed transitions:

- `New -> Capturing`
- `Capturing -> CapturePaused`
- `CapturePaused -> Capturing`
- `Capturing -> RawSaved`
- `CapturePaused -> RawSaved`
- `RawSaved -> ProcessingQueued`
- `ProcessingQueued -> Processing`
- `Processing -> Processed`
- `Processed -> Exporting`
- `Exporting -> Exported`
- Any active state -> `Failed` with recoverable diagnostics
- Stable states -> `Archived`

## CaptureSessionState

The implemented pure reducer lives in `modules/capture-api`.

States:

- `IDLE`
- `CHECKING_PERMISSIONS`
- `INITIALIZING_AR`
- `WARMING_UP_TRACKING`
- `TRACKING_READY`
- `CAPTURING`
- `PAUSED_BY_USER`
- `PAUSED_TRACKING_LOST`
- `FINALIZING`
- `COMPLETED`
- `FAILED`

Events:

- `UserStartedNewScan`
- `PermissionsGranted`
- `PermissionsDenied`
- `ArSessionReady`
- `ArSessionFailed`
- `TrackingStable`
- `TrackingLost`
- `TrackingRecovered`
- `UserPressedRecord`
- `UserPressedPause`
- `UserPressedResume`
- `UserPressedStop`
- `StoragePressure`
- `FatalError`

Reducer table:

| From | Event | To |
| --- | --- | --- |
| `IDLE` | `UserStartedNewScan` | `CHECKING_PERMISSIONS` |
| `CHECKING_PERMISSIONS` | `PermissionsGranted` | `INITIALIZING_AR` |
| `CHECKING_PERMISSIONS` | `PermissionsDenied` | `FAILED` |
| `INITIALIZING_AR` | `ArSessionReady` | `WARMING_UP_TRACKING` |
| `INITIALIZING_AR` | `ArSessionFailed` | `FAILED` |
| `WARMING_UP_TRACKING` | `TrackingStable` | `TRACKING_READY` |
| `WARMING_UP_TRACKING` | `TrackingLost` | `PAUSED_TRACKING_LOST` |
| `WARMING_UP_TRACKING` | `UserPressedStop` | `FINALIZING` |
| `TRACKING_READY` | `UserPressedRecord` | `CAPTURING` |
| `TRACKING_READY` | `TrackingLost` | `PAUSED_TRACKING_LOST` |
| `TRACKING_READY` | `UserPressedStop` | `FINALIZING` |
| `CAPTURING` | `TrackingLost` | `PAUSED_TRACKING_LOST` |
| `CAPTURING` | `UserPressedPause` | `PAUSED_BY_USER` |
| `CAPTURING` | `UserPressedStop` | `FINALIZING` |
| `CAPTURING` | `StoragePressure` | `FINALIZING` |
| `PAUSED_BY_USER` | `UserPressedResume` | `CAPTURING` |
| `PAUSED_BY_USER` | `TrackingLost` | `PAUSED_TRACKING_LOST` |
| `PAUSED_BY_USER` | `UserPressedStop` | `FINALIZING` |
| `PAUSED_BY_USER` | `StoragePressure` | `FINALIZING` |
| `PAUSED_TRACKING_LOST` | `TrackingRecovered` | `TRACKING_READY` |
| `PAUSED_TRACKING_LOST` | `UserPressedStop` | `FINALIZING` |
| `PAUSED_TRACKING_LOST` | `StoragePressure` | `FINALIZING` |
| `FINALIZING` | `UserPressedStop` | `COMPLETED` |
| Any non-terminal state | `FatalError` | `FAILED` |

Rules:

- Invalid transitions return `TransitionResult.Rejected`.
- `COMPLETED` and `FAILED` are terminal and reject further events.
- Frame acceptance is allowed only in `CAPTURING`; tracking loss immediately pauses capture.
- `StoragePressure` finalizes capture from active or paused capture states.

## ProcessingJobState

States:

- `Queued`
- `Running`
- `CancelRequested`
- `Checkpointing`
- `Cancelled`
- `Completed`
- `Failed`

Jobs must persist enough state to restart from the last valid checkpoint.
