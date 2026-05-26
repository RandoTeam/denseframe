---
name: raw-project-format
description: Trigger for DenseFrame Raw Project files, manifests, checksums, atomic IO, schema versioning, and migrations.
---

Follow the DFR v1 document before coding.
Write temp files first, flush/fsync where practical, then atomic rename.
Record checksums for frame payloads and manifest references.
Never load a large project fully into memory.
Plan migrations with explicit source and target versions.
