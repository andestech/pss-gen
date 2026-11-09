<!-- See CONTRIBUTING.md for what a pull request needs. -->

## What and why

<!-- Describe the change and link the issue. -->

## Tests

<!-- The regression test (tests/<suite>/<test> with its reviewed test.s.golden) or
unit test that covers the change. -->

- [ ] `make -C src unit-test` passes
- [ ] `make -C tests` passes

## Release note

<!-- One line for the release note of the next version, starting with Feature:,
Bug fix: or Notable change: (incompatible or changing output), or None. -->

## Target branch

- Features: target `develop`.
- Bug fixes: target `hot_fix`. A hot fix starts from an issue.
- Never target `main`; the repository owner releases into it.

Reference the issue in the commit subject: `[ISSUE] Fix #N: summary` or a trailing
`(#N)`. Do not change `VERSION` and do not commit jars or other build output: the
repository owner chooses the version and the release workflow builds the jar.
