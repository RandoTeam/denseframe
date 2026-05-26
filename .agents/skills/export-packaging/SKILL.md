---
name: export-packaging
description: Trigger for DFRZ, PLY, GLB, diagnostics ZIP, export manifests, packaging, cancellation, and export metrics.
---

Write exports to a temp file first, then atomically publish the completed file.
Support cancellation and cleanup partial output.
Record metrics: duration, input size, output size, frame count, vertex count, and failures.
Include manifest and checksum data where practical.
Do not require cloud processing.
