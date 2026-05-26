# Local-First Policy

DenseFrame app runtime must not require cloud processing.

## Rules

- Scan data remains local unless the user explicitly exports or shares it.
- Reconstruction, viewing, and export must work without network access.
- Hidden network calls are prohibited.
- Analytics SDKs are prohibited.
- Optional future network features require an ADR, clear UI disclosure, and user action.

## Allowed Local Behavior

- Device capability checks.
- Local project storage.
- Local reconstruction jobs.
- Local export packaging.
- Local diagnostics generation.

## Review Questions

- Does this feature work in airplane mode?
- Is any scan data leaving the device?
- Is the user clearly initiating any transfer?
- Can the feature be tested without network access?
