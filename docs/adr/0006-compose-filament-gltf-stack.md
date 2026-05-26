# ADR 0006: Compose, Filament, and glTF Stack

## Status

Accepted

## Context

DenseFrame needs a native Android UI, a 3D viewer, and export-friendly scene formats.

## Decision

Use Kotlin and Jetpack Compose for app UI. Use Filament for 3D viewing and glTF/GLB as the primary scene interchange target, after official API and dependency verification.

## Consequences

- Compose UI must be polished and touch-first.
- Filament and glTF integration must be verified against official docs before implementation.
- Viewer and export artifacts should be designed around bounded memory and clear failure states.
