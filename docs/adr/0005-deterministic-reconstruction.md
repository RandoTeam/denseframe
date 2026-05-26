# ADR 0005: Deterministic Reconstruction

## Status

Accepted

## Context

Local reconstruction should be debuggable, repeatable, and testable across development iterations.

## Decision

Reconstruction outputs should be deterministic for the same raw project, code version, and serialized parameters where practical.

## Consequences

- Parameters must be serializable.
- Coordinate systems and matrix conventions must be documented before implementation.
- Golden synthetic tests are required for reconstruction math.
- Parallelism must not introduce uncontrolled nondeterminism.
