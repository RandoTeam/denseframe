# Codex Task: Export MVP

Read `AGENTS.md`, ADR 0004, ADR 0006, and export-related docs.

Use the `export-packaging`, `raw-project-format`, `filament-viewer`, and `quality-gate` skills.

Implement export MVP for raw archive and first 3D scene formats, prioritizing DFRZ, PLY, and GLB where supported by verified APIs. Exports must write temp files first, support cancellation, clean partial output, and record metrics.

Add tests for success, cancellation, and checksum or manifest failure.

Run targeted tests and relevant build checks.
