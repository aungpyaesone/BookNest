## Summary

Describe the user-visible and architectural change.

## Required checks

- [ ] Names follow `docs/coding-standards.md`.
- [ ] Code is in the correct module and no forbidden dependency was introduced.
- [ ] Tests cover the changed behavior and error paths.
- [ ] Compact and expanded adaptive behavior was verified when UI changed.
- [ ] GitNexus impact analysis was run before editing existing symbols.
- [ ] GitNexus index was refreshed and `detect-changes --scope all` was reviewed.
- [ ] `./gradlew verifyBookNest` passes locally.
