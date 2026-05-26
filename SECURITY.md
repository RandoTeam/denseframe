# Security Policy

DenseFrame is early alpha software. Do not report sensitive security issues in public GitHub Issues.

## Reporting

If a report contains private data, device logs, raw scan content, credentials, signing material, or exploit details, contact the maintainers privately once a private reporting channel is configured. Until then, open a public issue only with non-sensitive reproduction information and state that private details are available.

## Secrets and Local Data

- Do not commit API keys, tokens, passwords, signing keys, keystores, or `local.properties`.
- Do not commit raw scan projects, exports, generated build output, or diagnostics that include private spaces or objects.
- DenseFrame is local-first. Hidden network calls, analytics SDKs, or required cloud processing are security and product-policy issues.

## Supported Versions

No production release is supported yet. Security handling applies to the `main` branch during early alpha.
