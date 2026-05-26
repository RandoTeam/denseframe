# Codex Workflow

Use this workflow for DenseFrame tasks:

1. Read `AGENTS.md` first.
2. Read relevant ADRs before changing architecture, runtime policy, capture, storage, reconstruction, or viewer decisions.
3. Use relevant repository skills from `.agents/skills`.
4. Spawn specialized agents only when useful for verification, decomposition, or independent review.
5. Produce a short implementation plan for non-trivial work.
6. Make the smallest coherent change.
7. Add or update tests when behavior is changed.
8. Run applicable checks. Do not run Gradle before an Android project exists.
9. Summarize changed files, validation, and remaining risks.
10. Never invent APIs or dependencies. Verify official Android, ARCore, CameraX, Filament, glTF, Gradle, and NDK details before implementation.

If an API, dependency, or platform behavior cannot be verified, add a documented TODO with the verification source needed instead of writing fake code.
