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

States:

- `CheckingSupport`
- `Ready`
- `Starting`
- `Tracking`
- `LimitedTracking`
- `Paused`
- `Stopping`
- `Stopped`
- `Failed`

Frame acceptance is allowed only in `Tracking` and limited cases explicitly approved by capture policy.

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
