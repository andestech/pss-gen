# Contributing to PSSGen

PSSGen takes bug reports, feature requests and pull requests on GitHub. This page
says what each needs; [docs/RELEASE.md](docs/RELEASE.md) has the full rules of
branches, versions and releases.

## Reporting an issue

Open an issue with the "Bug report" or "Feature request" template. For a bug, give

- the smallest PSS input that shows it, with the command line you ran;
- the jar you ran, for example `pssgen_v3.0.0.jar` (a jar built from a working tree is
  named after `git describe`, which identifies the commit);
- the output you got and the output you expected, with the LRM section when the
  expectation comes from it.

Check [docs/SUPPORT.md](docs/SUPPORT.md) and the README section "Known limitations"
first: a construct listed there as unsupported or partial is a known gap, not a new bug.

A bug fix on `hot_fix` starts from such an issue.

## Building and testing

The README lists the requirements (JDK 17 to 27, GNU Make, bash). Before you open a
pull request, run

```
$ make -C src            # build the jar
$ make -C src unit-test  # unit tests
$ make -C tests          # regression tests
```

and add a test that covers the change: a regression test under `tests/` (a directory
with `test.pss` and, when the test produces output, its reviewed `test.s.golden`) or a
unit test under `src/test/java/`. The README sections "Unit tests" and "Regression
tests" describe both.

## Opening a pull request

| Change | Target branch |
|---|---|
| Feature or other change | `develop` |
| Bug fix | `hot_fix`, with an issue |

Never target `main`; the repository owner releases into it. The pull request template
asks for:

- **What and why**, with a link to the issue.
- **Tests**: the test that covers the change, and that both test commands pass.
- **Release note**: one line starting with `Feature:`, `Bug fix:` or
  `Notable change:` (an incompatibility or a change of output), or `None`. The line goes
  into the draft of the next release note.

In commit subjects, name the issue a commit fixes as `[ISSUE] Fix #N: summary` or
`summary (#N)`. Only a `#N` in a subject counts as a fix. On `hot_fix` every commit names
a bug issue.

Leave `VERSION` and build output (jars, class files) out of the pull request: the
repository owner chooses the version when a release is cut, and the release workflow
builds the jar. The CI job "Contribution rules" fails a pull request that changes
`VERSION` or adds a `.jar` or `.class` file.

When a change alters how PSSGen handles a construct, update its entry in
[docs/SUPPORT.md](docs/SUPPORT.md).

## License

Contributions are released under the MIT License of the repository (see `LICENSE`).
