# DenseFrame Repository Rules

These rules apply repository-wide.

## Product Constraints

1. DenseFrame is local-first. The app runtime must not require cloud processing for scanning, reconstruction, viewing, or export.
2. No hidden network calls. Any future network feature must be explicit, user-visible, optional, documented, and reviewed.
3. No analytics SDKs.
4. No API hallucinations. Official documentation or source must be checked before implementing Android, ARCore, CameraX, Filament, glTF, Gradle, or NDK APIs.
5. No large scan data committed. Keep raw scans, exports, samples, and benchmarks out of Git unless a tiny documented fixture is intentionally added.
6. All long-running jobs must be cancellable and safely restartable.
7. All raw project writes must be crash-safe: write a temp file, flush/fsync where practical, atomic rename, and checksum.
8. Never load a large project fully into memory.
9. Use bounded queues for capture and processing.
10. All state machines must be explicit and tested.
11. Reconstruction parameters must be serializable.
12. Coordinate systems and matrix conventions must be documented before code.
13. UI must be product-quality, not default placeholder screens.
14. Each PR must update tests, docs, or explain why not applicable.

## Public Product References

KIRI Engine, Polycam, Scaniverse, Luma, RealityScan, and similar apps may be used only as public product and UX references. Do not copy proprietary UI assets, names, code, icons, screenshots, branding, or private implementation details.

## Engineering Discipline

- Prefer small, verifiable changes.
- Use local deterministic behavior by default.
- Keep storage, capture, reconstruction, and viewer contracts documented before implementation.
- Validate with tests or documented manual checks whenever code changes.
