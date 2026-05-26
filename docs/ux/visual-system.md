# Visual System

DenseFrame should feel like a premium dark spatial tool: calm, precise, camera-first, and trustworthy.

## Visual Language

- Dark neutral surfaces that let camera and 3D content dominate.
- Crisp type, restrained density, and clear hierarchy.
- Thin separators and overlays instead of heavy cards inside camera/viewer surfaces.
- Status color is functional, not decorative.
- Project gallery feels consumer-polished but operational: fast scanning, clear project state, obvious recovery.

## Semantic Color Roles

Exact values are deferred to implementation, but roles are fixed:

- Background: app base and gallery surface.
- Surface: sheets, mode cards, metadata panels.
- Surface elevated: modal or bottom sheet surfaces.
- Primary action: start scan, start processing, export.
- Secondary action: rename, retry, keep raw.
- Success: tracking good, save complete, processing complete.
- Warning: limited tracking, low confidence, motion risk, storage caution, thermal warm.
- Error: tracking lost, write failure, unsupported device, corrupted project.
- Data overlay: coverage, depth confidence, and point cloud highlights.
- Disabled: unavailable mode or blocked action.

## Typography Intent

- Title scale for screen identity only.
- Compact headings for panels and mode cards.
- Numeric/status labels are tabular or visually stable where available.
- Capture HUD text is short and glanceable.
- Long explanations belong in sheets, not over camera preview.

## Motion and Animation Intent

- Use short transitions to clarify state changes, not decorate.
- HUD status changes should be smooth enough to avoid flicker but quick enough to warn.
- Processing progress should communicate resumable stages.
- Viewer gestures should feel direct, with no bounce or playful effects that reduce precision.
- Reduce motion where platform settings require it.

## Component List

- Gallery project row/card with status, date, size, preview/artifact indicator.
- Empty gallery state.
- New Scan button.
- Mode card for object, room, quick scene.
- Permission/support sheet.
- Capture HUD status chips and gauges.
- Primary capture control with pause/stop states.
- Save Review summary panel.
- Processing progress surface.
- Viewer toolbar.
- Export sheet.
- Error/recovery sheet.

## Capture HUD Visual Behavior

- Camera preview remains dominant.
- HUD sits at edges and avoids the scan target.
- Tracking, confidence, coverage, motion, storage, and thermal indicators use semantic color and stable labels.
- Accepted/dropped frame counts must not cause layout jumps.
- Critical warnings can interrupt with a bottom sheet only when action is required.

## Viewer Controls

- Orbit, pan, pinch zoom, reset, inspect, and export are primary controls.
- Controls should be reachable with one hand but not obscure the model.
- Metadata appears in a compact sheet or panel.
- Unsupported or missing artifact states should explain the next available action.
