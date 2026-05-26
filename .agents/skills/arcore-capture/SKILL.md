---
name: arcore-capture
description: Trigger for ARCore capture, raw depth, confidence, tracking, pose, frame timestamps, and camera intrinsics.
---

Verify runtime ARCore support before enabling capture.
Handle tracking loss, relocalization, low light, motion blur, and depth unavailability explicitly.
Store timestamps, pose, intrinsics, depth, confidence, and tracking state with each accepted frame.
Use bounded queues between capture and storage.
Document coordinate systems before implementation.
