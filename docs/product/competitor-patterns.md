# Public Competitor Patterns

This document uses only public product and UX observations. It does not claim internal knowledge of KIRI Engine, Polycam, Scaniverse, or any other product. DenseFrame must not copy proprietary assets, names, branding, screenshots, icons, code, or private implementation details.

## Structured Matrix

| Reference | Public patterns to study | DenseFrame adopts | DenseFrame does not copy | Deferred |
| --- | --- | --- | --- | --- |
| KIRI Engine | Clear scan entry points, mode-oriented capture, export-aware workflows, user-facing scan guidance | Simple mode cards, clear export outcomes, concise mode-specific instructions | Brand language, proprietary UI visuals, icons, screenshots, paywall or account patterns | Advanced mode catalog and cloud-style workflows |
| Polycam | Strong project gallery, visible processing step, polished previews, edit/export mental model | Local project gallery, processing button after save review, project status, crop/edit/export as understandable later surfaces | Visual design, asset style, names, proprietary editing interactions, cloud/account assumptions | Rich editor, measurements, sharing, cloud sync |
| Scaniverse | Fast capture loop, immediate spatial feedback, local viewer feel, crop/edit/export flow | Capture HUD with live quality indicators, quick point cloud preview, touch-first viewer, explicit export step | Proprietary viewer visuals, icons, screenshots, brand tone, internal reconstruction behavior | High-quality mesh editing, advanced cropping, photorealistic polish |

## DenseFrame Adaptation Plan

- Gallery is the home base: recent projects, empty state, local storage status, and `New Scan`.
- Mode Select uses three guided cards: object, room, quick scene.
- Capture HUD focuses on tracking, depth confidence, coverage, motion/blur risk, accepted/dropped frames, storage, and thermal state.
- Save Review appears before processing so users understand raw project health.
- Processing is a deliberate local action with restartable progress.
- Viewer launches as point cloud MVP first, with mesh workflows deferred.
- Export is explicit and includes raw archive first.

## UX Lessons

- Consumer scanning apps succeed when capture guidance is immediate and visual.
- A project gallery reduces anxiety because scans feel saved and recoverable.
- Processing should be understandable as a step, not a hidden background mystery.
- Crop/edit/export are expected concepts, but month one should keep them scoped to viewer/export MVP and defer full editing.

## Explicit Non-Copying Boundary

DenseFrame may learn from public interaction patterns such as guided modes, gallery-first project organization, processing CTA, local viewer, and export flow. It must not copy product names, proprietary layouts, visual assets, screenshots, icons, brand voice, code, reconstruction implementation, pricing mechanics, or cloud assumptions.
