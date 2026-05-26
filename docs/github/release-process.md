# Release Process

DenseFrame has no production release process yet.

## Alpha Tags

Future alpha releases should use annotated tags:

```text
v0.1.0-alpha.1
v0.1.0-alpha.2
```

Each tag should point to a commit that has passed Android CI and the relevant real-device notes for the release scope.

## Changelog

Update `CHANGELOG.md` before each tag. Summaries should focus on user-visible behavior, file-format changes, storage compatibility, capture changes, reconstruction output changes, and known risks.

## Build Artifacts

Do not commit APKs, AABs, signing keys, keystores, generated reports, raw scans, or exports to the repository.

GitHub Releases may host alpha APKs later after signing, provenance, and release notes are defined. Signed builds are not implemented yet.

## Distribution

There is no Play Store process yet. Any future store distribution requires a separate release ADR covering signing, privacy disclosures, local-first guarantees, and device support.
