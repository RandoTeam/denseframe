# GitHub Repository Setup

Repository name: `RandoTeam/denseframe`

Description:

```text
Local-first deterministic Android 3D scanning: ARCore raw depth, DFR raw projects, point clouds, TSDF/GLB roadmap.
```

## Topics

Use these lowercase GitHub topics:

`android`, `kotlin`, `jetpack-compose`, `arcore`, `depth-camera`, `rgbd`, `3d-scanning`, `point-cloud`, `gltf`, `filament`, `local-first`, `computer-vision`, `deterministic`, `mobile-scanning`, `open-source`, `ndk`

## Remote Setup

After GitHub CLI authentication:

```powershell
gh auth login
gh repo create RandoTeam/denseframe --public --source=. --remote=origin --push --description "Local-first deterministic Android 3D scanning: ARCore raw depth, DFR raw projects, point clouds, TSDF/GLB roadmap."
```

If the repository already exists:

```powershell
gh repo view RandoTeam/denseframe --json sshUrl,url
git remote add origin <chosen-url>
git push -u origin main
git push --follow-tags
```

Prefer SSH when the account has SSH authentication configured; otherwise use HTTPS.

## Push Workflow

- Work from `main`.
- Keep commits focused.
- Run `.\gradlew.bat test --no-daemon` before pushing when Gradle is available.
- Do not push secrets, signing keys, local properties, raw scans, exports, APKs, AABs, or build output.

## Repository Settings

Configure through GitHub CLI where supported:

- public visibility;
- issues enabled;
- discussions enabled;
- wiki disabled;
- squash merge enabled;
- rebase merge enabled;
- merge commits disabled;
- delete branch on merge enabled;
- topics set as listed above.

## Manual Follow-Up

Configure these in the GitHub UI if the CLI or account plan does not support them:

- branch protection or ruleset for `main`;
- required Android CI check;
- secret scanning and push protection;
- repository social preview image using `docs/assets/denseframe-banner.svg` or a rendered derivative;
- private vulnerability reporting once available.
