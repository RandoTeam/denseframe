---
name: reconstruction-math
description: Trigger for unprojection, TSDF, coordinate transforms, point clouds, normals, meshing, texture projection, and reconstruction parameters.
---

Document coordinate systems, matrix storage order, units, and camera conventions before implementation.
Keep output deterministic for the same inputs and parameters.
Make reconstruction parameters serializable.
Use bounded memory and cancellable restartable jobs.
Add golden synthetic tests for unprojection, transforms, TSDF integration, and mesh extraction.
