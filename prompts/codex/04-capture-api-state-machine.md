# Codex Task: Capture API and State Machine

Read `AGENTS.md`, `docs/architecture/state-machines.md`, and ADR 0003.

Use the `arcore-capture`, `api-verification`, `raw-project-format`, and `quality-gate` skills.

Define capture API contracts and explicit capture session state machine without binding directly to ARCore implementation details. Include frame acceptance rules, bounded queues, cancellation, tracking state handling, and storage handoff.

Add unit tests for allowed and rejected transitions.

Run targeted tests and relevant build checks.
