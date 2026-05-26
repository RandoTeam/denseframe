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

## Behavior

- Indicators must be visible without blocking the scan target.
- Warnings should be actionable.
- Capture must continue storing accepted frames safely while warning states are shown.
- Loss of tracking should pause frame acceptance until pose quality recovers.
