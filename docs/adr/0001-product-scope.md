# ADR 0001: Product Scope

## Status

Accepted

## Context

DenseFrame needs a narrow month-one scope to avoid building a broad 3D platform before proving capture, storage, local reconstruction, viewing, and export.

## Decision

DenseFrame is a local-first consumer Android 3D scanning app with this primary flow: open app, scan object or room, save raw project, process locally, view by touch, export raw or 3D scene.

## Consequences

- Cloud reconstruction, social features, and marketplace publishing are out of scope.
- Raw project safety and recovery are product requirements, not implementation details.
- UX quality matters from the first Android skeleton.
