# Changelog

This project follows a Keep a Changelog inspired structure. DenseFrame has not shipped a public release yet.

## Unreleased

### Added

- Public repository presentation, issue templates, support/security docs, and original SVG identity assets.
- Android multi-module Kotlin/Compose skeleton with a polished local app shell.
- DFR v1 raw project storage with manifests, frame metadata, atomic writes, streaming checksums, reader, validator, and tests.
- Pure capture API models and deterministic capture session state reducer.
- Capture-to-DFR bridge module that maps synthetic `FramePacket` values to DFR frame folders through crash-safe project-store APIs.
- First early-alpha ARCore raw-depth adapter with runtime availability checks, raw depth/confidence packing, FramePacket mapping, DFR write orchestration, and real capture HUD status.

### Deferred

- Deterministic point cloud reconstruction implementation.
- TSDF mesh, texture baking, measurements, Gaussian splatting, cloud sync, accounts, and sharing.

## Initial Architecture Milestones

- Repository control plane, ADRs, local-first policy, agent rules, skills, and staged prompts.
- Product and UX documentation for gallery, mode select, capture HUD, save review, processing, viewer, and export flow.
- Month-one roadmap and risk register.
