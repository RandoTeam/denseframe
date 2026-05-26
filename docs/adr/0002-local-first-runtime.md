# ADR 0002: Local-First Runtime

## Status

Accepted

## Context

3D scan data can be private and large. The product promise requires predictable behavior without required remote services.

## Decision

DenseFrame app runtime must support scanning, raw project storage, local processing, viewing, and export without cloud processing or hidden network calls.

## Consequences

- Network and analytics dependencies are prohibited unless a future ADR explicitly approves an optional user-visible feature.
- Tests must include offline behavior.
- Processing must be bounded, cancellable, and restartable on-device.
