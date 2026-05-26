# Capture HUD

The capture HUD overlays real-time scan quality indicators on the camera view.

## Required Indicators

- Tracking: normal, limited, lost, relocalizing.
- Depth confidence: high, medium, low, unavailable.
- Coverage: observed regions, missing regions, and sufficient coverage threshold.
- Motion and blur risk: stable, slow down, too fast.
- Accepted and dropped frames: counts plus drop reason categories.
- Storage: remaining local space and current raw project size.
- Thermal: normal, warm, throttling risk, capture should pause.
- ARCore availability and install/update state.
- Camera permission state.
- Selected depth mode: `RAW_DEPTH_ONLY`, `AUTOMATIC` fallback, or unsupported.
- Session lifecycle: created, configured, resumed, paused, failed.
- Depth freshness: `NEW_DEPTH`, `REPROJECTED_DEPTH`, or `NO_DEPTH`.
- Current DFR project id/path.

## Behavior

- Indicators must be visible without blocking the scan target.
- Warnings should be actionable.
- Capture must continue storing accepted frames safely while warning states are shown.
- Loss of tracking should pause frame acceptance until pose quality recovers.
- The first ARCore MVP uses a minimal GL capture surface and does not render a camera preview.
- The HUD must not show fake point clouds, fake meshes, or claim scan completion until a DFR project has actually been written and finalized.
