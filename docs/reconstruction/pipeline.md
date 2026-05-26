# Reconstruction Pipeline

DenseFrame reconstruction is local and deterministic for the same raw project, code version, and serialized parameters where practical.

## Mathematical Conventions

- Units: meters for depth and world coordinates.
- Pixel coordinates: `u` increases right, `v` increases down, origin at the top-left pixel center convention documented by the capture adapter.
- Camera intrinsics matrix `K`:

```text
K = [ fx  0  cx
      0  fy  cy
      0   0   1 ]
```

- Transform convention: `T_world_camera` maps homogeneous camera-space points into world space.
- Matrix storage order must be documented in code before implementation and covered by tests.

## Depth Unprojection

For a depth sample `z` at pixel `(u, v)`:

```text
x_camera = (u - cx) * z / fx
y_camera = (v - cy) * z / fy
z_camera = z
p_camera = [x_camera, y_camera, z_camera, 1]
p_world = T_world_camera * p_camera
```

The sign and axis convention must be verified against the capture source before code is written.

## Confidence Filtering

- Each depth sample is accepted only if depth is finite, positive, inside configured min/max range, and confidence meets the serialized threshold.
- Low-confidence samples may be counted for diagnostics but must not enter deterministic point generation unless parameters explicitly allow it.
- Confidence thresholds are part of serialized reconstruction parameters.

## Invalid or Missing Depth

- Missing depth maps mark the frame as unusable for depth reconstruction but still useful for diagnostics.
- Invalid samples are skipped, counted, and surfaced in processing metrics.
- Large invalid regions can disqualify a keyframe through deterministic thresholds.

## Deterministic MVP Pipeline

1. Load DFR v1 manifest through streaming readers.
2. Validate format version, checksums, timestamps, dimensions, intrinsics, pose, and tracking state.
3. Select keyframes with deterministic thresholds for spacing, tracking, blur/motion risk, and depth confidence.
4. Unproject accepted depth samples using `K`.
5. Transform points with `T_world_camera`.
6. Filter by confidence, depth range, and optional voxel downsample parameters.
7. Write point cloud artifact and metrics.
8. Open viewer/export flow.

MVP outputs:

- Point cloud artifact.
- Processing metrics.
- Diagnostics for rejected frames and invalid samples.

## Deterministic Mesh Pipeline v2

Deferred until after MVP.

Planned stages:

- TSDF or equivalent volumetric integration.
- Deterministic truncation and weighting policy.
- Mesh extraction.
- Normal estimation.
- Optional simplification.
- Optional texture projection after a separate ADR.

Required before implementation:

- Golden synthetic TSDF tests.
- Memory tiling plan.
- Mesh quality acceptance criteria.

## Experimental Neural or Splat Pipeline Later

Gaussian splatting, neural rendering, and learned reconstruction are explicitly out of month-one scope.

Any future exploration requires:

- Separate ADR.
- Local-first runtime review.
- Device performance and memory budget.
- Clear distinction from deterministic MVP artifacts.
