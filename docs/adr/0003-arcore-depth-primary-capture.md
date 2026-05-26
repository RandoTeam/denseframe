# ADR 0003: ARCore Depth as Primary Capture

## Status

Accepted

## Context

DenseFrame needs a practical Android depth source for month one. ARCore provides public Android APIs for motion tracking and depth on supported devices.

## Decision

Use ARCore depth as the primary capture path for the first implementation, after official API and device support verification.

## Consequences

- Runtime support checks are mandatory.
- Capture must handle tracking loss, depth unavailability, low confidence, and relocalization.
- Frame records must include timestamps, pose, intrinsics, depth, confidence, and tracking state.
