# Contributing to DenseFrame

DenseFrame changes should be small, local-first, and verifiable.

## Before Coding

- Read `AGENTS.md`.
- Read relevant ADRs in `docs/adr`.
- Verify official APIs before using Android, ARCore, CameraX, Filament, glTF, Gradle, or NDK features.
- Use repository skills in `.agents/skills` when they apply.

## Pull Request Expectations

- Keep the diff focused on the requested task.
- Update tests, docs, or explain why neither applies.
- Do not commit large scan data or generated benchmark output.
- Do not add cloud, analytics, or hidden network behavior.
- Document state transitions, coordinate conventions, and file-format changes before or with code.

## Validation

Run the narrowest checks that prove the change. Add broader checks when touching shared architecture, state machines, storage, capture, reconstruction, or viewer behavior.
