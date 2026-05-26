# Codex Task: Implement Raw Project Store

Read `AGENTS.md`, ADR 0004, and `docs/file-format/dfr-v1.md`.

Use the `raw-project-format`, `android-architecture`, and `quality-gate` skills.

Implement DenseFrame Raw Project v1 storage with manifest writing, frame metadata, checksums, temp-file writes, flush/fsync where practical, atomic rename, and migration hooks.

Never load a large project fully into memory. Add tests for successful writes, partial writes, checksum failures, and version rejection.

Run targeted tests and relevant build checks.
