# ADR 0004: DenseFrame Raw Format

## Status

Accepted

## Context

Raw capture must survive crashes and support deterministic reprocessing. A documented project format is needed before code.

## Decision

Use DenseFrame Raw Project v1 as a folder-based local format with manifest, frame metadata, payload files, checksums, and versioning.

## Consequences

- Writes must use temp files, flush/fsync where practical, atomic rename, and checksums.
- Large projects must be streamed instead of fully loaded into memory.
- Schema changes require migrations and documentation.
