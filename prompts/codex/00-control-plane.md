# Codex Task: Bootstrap DenseFrame Control Plane

Initialize the DenseFrame repository control plane.

Create repository rules, Codex workflow docs, README, roadmap, contribution guide, Apache-2.0 license, Android-aware `.gitignore`, Codex config, specialized agent TOMLs, repository skills, product/UX/architecture/file-format/reconstruction/testing/risk docs, ADRs, and staged Codex prompts.

Do not create Android source code, Gradle modules, cloud dependencies, network dependencies, or invented APIs.

Set `.codex/config.toml` to model `gpt-5.5`, sandbox `workspace-write`, approvals `on-request`, `agents.max_threads = 6`, and `agents.max_depth = 1`.

Validate with basic shell checks only: list files, show git status, ensure markdown files are meaningful, TOML files are plausible, and skill files contain `name` and `description` front matter. If this is a Git repository, commit with message `docs: bootstrap DenseFrame control plane`.
