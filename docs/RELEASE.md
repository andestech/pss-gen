# Releasing PSSGen

A release is one reviewed pull request into `main`. CI checks it, and merging it tags
the commit, publishes the GitHub Release and brings the other branches up to date.

- [Branches](#branches)
- [Where to open a pull request](#where-to-open-a-pull-request)
- [Versions](#versions)
- [Cutting a release](#cutting-a-release)
- [The release note](#the-release-note)
- [Issues](#issues)
- [Comparing with the previous release](#comparing-with-the-previous-release)
- [What the release workflow does](#what-the-release-workflow-does)
- [Recovery](#recovery)

## Branches

| Branch | Content | Released as |
|---|---|---|
| `main` | Released code only. Every release is a merge commit with an annotated tag. | `vX.Y.Z` |
| `develop` | Features and fixes for the next minor or major version. | `vX.(Y+1).0` or `v(X+1).0.0` |
| `hot_fix` | Bug fixes only, for the next patch version. | `vX.Y.(Z+1)` |

- `main` receives only release pull requests whose head branch is `develop` or
  `hot_fix` of this repository. Nobody pushes to it directly.
- A release pull request is merged with "Create a merge commit": no squash, no rebase.
  The released branch then stays an ancestor of `main`.
- Nothing is force-pushed, rebased or deleted on these three branches.
- After a release, `develop` and `hot_fix` are synced to the new tag by the release
  workflow: a branch that is an ancestor of the tag is fast-forwarded, any other
  branch gets a sync merge of the tag. A `hot_fix` release therefore fast-forwards
  `hot_fix` and merges into `develop`. A `develop` release fast-forwards `develop` and
  merges into `hot_fix` when it holds unreleased fixes; those fixes later ship as a
  patch of the new version.
- One release pull request into `main` is open at a time. While it is open, its head
  branch takes only fixes for that release, so the reviewed content does not move.
  release-check requires the head branch to contain the previous release tag, which is
  the case once the sync after the last release has run.

## Where to open a pull request

| Change | Target | Needs |
|---|---|---|
| Feature or other change for the next minor or major | `develop` | |
| Bug fix | `hot_fix` | An issue; the commit subject names it |
| Release | `main` | A maintainer; head branch `develop` or `hot_fix` |

Never target `main` with anything else. A pull request from a fork is retargeted to
`develop` or `hot_fix` by a maintainer.

A commit that fixes issue `#N` names it in its subject, as `[ISSUE] Fix #N: summary`
or `summary (#N)`. Only a `#N` in a subject counts as a fix; a `#N` in the body is a
mention. On `hot_fix` every commit must name an issue, and every named issue must be a
bug. A revert subject drops the closing keyword (`Revert "[ISSUE] #N: summary"`, not
`Revert "[ISSUE] Fix #N: summary"`), because the keyword in a quoted subject would
close the issue again when the revert reaches `main`.

## Versions

`VERSION` is the only file that names the version, in the form `vMAJOR.MINOR.PATCH`
(no leading zeros, no suffix). Between releases it keeps the last release: the branches
get it from the sync, and only `release.py prepare` changes it, in the
`[RELEASE] vX.Y.Z` commit of a release pull request.

The previous release is the highest version among the tags reachable from `main`.
With previous release vX.Y.Z:

| Head branch | Allowed version |
|---|---|
| `hot_fix` | `vX.Y.(Z+1)` |
| `develop` | `vX.(Y+1).0` or `v(X+1).0.0` |

A version cannot be skipped, and a `develop` release is never a patch, even when it
holds only fixes.

The maintainer who opens the release pull request chooses major or minor; CI checks
only that the number is a legal successor.

- Major: a change of overall direction, a severe incompatibility with the previous
  version, or a major change of the LRM.
- Minor: may include partial incompatibility or changed output, but every such change
  must be listed under Notable changes (see
  [Comparing with the previous release](#comparing-with-the-previous-release)).

A jar built from a commit that is not the tagged release commit is named after
`git describe` (`pssgen_vX.Y.Z-N-gSHA.jar`, with `-dirty` for an edited tree), so a
development jar is never taken for the release jar. The release jar has the plain
name `pssgen_vX.Y.Z.jar`. `bash make/build-name.sh` prints the name; `build.gradle` and
`make/common.mk` use it.

## Cutting a release

1. Check out the release branch (`develop` or `hot_fix`), up to date, with a clean tree.
   It must contain the previous release tag; when `prepare` refuses because it does
   not, finish the pending sync first (see [Recovery](#recovery)).
2. Run the script with the kind of release:

   ```
   $ python3 .github/scripts/release/release.py prepare <major|minor|patch>
   ```

   `patch` is for `hot_fix`; `major` and `minor` are for `develop`. The script writes
   the next version to `VERSION` and prints a release-note draft whose Features and
   Bug fixes are filled from the issue range. It reads GitHub anonymously, or with
   `GITHUB_TOKEN` when that is set, and warns about references it cannot classify.
3. Commit `VERSION` as `[RELEASE] vX.Y.Z` and push the branch.
4. Open a pull request from the branch into `main` with the release template:

   ```
   https://github.com/andestech/pss-gen/compare/main...<branch>?quick_pull=1&template=release.md
   ```

   Finish the release note in the description (see [The release note](#the-release-note)). The description must not
   contain a closing keyword before an issue number (see [Issues](#issues)).
5. Wait for the release-check workflow. It fails the pull request when

   - the head branch is not `develop` or `hot_fix` of this repository;
   - the head branch does not contain the previous release tag;
   - `VERSION` is not a legal successor of the previous release for that branch;
   - the issue range has a reference GitHub does not know, or an issue that is neither
     a feature nor a bug, on `hot_fix` (a warning on `develop`);
   - the `hot_fix` range has a feature, or a commit other than the `[RELEASE]` commit
     without an issue in its subject;
   - the description misses a release-note section, leaves a section empty or at the
     placeholder `<!-- write -->`, writes `None` under Summary or Artifacts, or has a
     closing keyword before an issue number;
   - the build, the regression tests (`make -C tests`), the unit tests or the release
     script tests fail.

   It also lists, in the job summary, the issues the merge will close and whether the
   description lists each as fixed, and it prints the release-note draft again, for a
   maintainer who cannot reach GitHub from the shell. The version comparison runs as a
   separate job; its result is information and does not fail the pull request.
6. Review with the checklist at the top of the release template, then merge with
   "Create a merge commit".

## The release note

The release pull request description is the release note, and becomes the body of the
GitHub Release. It has these sections, in this order:

| Section | Content |
|---|---|
| `## Summary` | Hand-written. |
| `## Notable changes` | Hand-written: every incompatibility and every output change. The draft starts with the issues and pull requests labelled `incompatible`. `None` is allowed. |
| `## Features` | The feature issues of the range, from the draft; edit as needed. `None` is allowed. |
| `## Bug fixes` | The bug issues of the range, from the draft; edit as needed. `None` is allowed. |
| `## Known issues / limitations` | Hand-written: open issues labelled `known-issue`, and the README "Known limitations" and `docs/SUPPORT.md`. `None` is allowed. |
| `## Artifacts` | Leave the filler line; the release workflow replaces it with the checksum of each attached file. |

After the merge the edited lists in the description are the authority for what the
release fixed.

## Issues

The issue range of a release is the set of issues named in the subjects of the
non-merge commits in `<previous tag>..<head>`, plus the issues that a named pull
request closes. A fix that was reverted in the same range is not counted as fixed; the
check warns about it so that the issue can move to Known issues. A `#N` that GitHub
does not know is an error on `hot_fix` and a warning on `develop`.

GitHub closes an issue when a commit whose message has a closing keyword (`Fix #N`,
`Closes #N`, `Resolves #N`, ...) reaches `main`, whatever the release note says. The
flow handles that:

- The release pull request description must not contain a closing keyword before an
  issue number. Write `Features: #12` or `issue 12`, not `fixes #12`.
- The release-check summary lists every issue the merge will auto-close, and warns about
  those not listed under Features or Bug fixes.
- After the merge the release workflow closes every issue listed under Features or Bug
  fixes that is still open, reopens every issue the merge auto-closed that is not
  listed there, and reopens each issue under Known issues and gives it the label
  `known-issue`. An issue closed by hand before the merge stays closed. The workflow
  adds no comments to issues.
- An issue listed both as fixed and as a known issue is left as it is, with a warning.

The labels `bug` and `enhancement` (or the issue types `Bug` and `Feature`) classify an
issue; `incompatible` marks an issue or pull request for Notable changes.

## Comparing with the previous release

```
$ make -C tests compare BASE=vX.Y.Z
```

runs the regression tests with two PSSGen versions and reports the tests whose results
differ. `BASE` is a release tag, which downloads that release's jar into
`build/compare/jars/`, or the path of a jar. `NEW` is the jar of the checkout by
default (`make -C src` first). `SUITES` and `TESTS` select tests as for `make -C tests`.
The output goes to `build/compare/<base>_vs_<new>/`; `report.md` there counts the tests
per class and shows the first lines of each difference.

| Class | Meaning |
|---|---|
| `STATUS` | The exit statuses differ. |
| `OUTPUT` | `test.s` differs, or only one version writes it. |
| `MESSAGE` | Only the messages in `run.log` differ. |
| `SAME` | No difference. |
| `SKIP` | `test.cfg` says `skip`. |

With a tag as `BASE`, the report lists first the `OUTPUT` tests whose
`test.s.golden` did not change since that tag: their output changed without a
deliberate golden update. The command exits with 0 when the comparison ran, whatever
the differences are.

The release-check workflow runs the comparison against the previous release, puts
`report.md` in the job summary and uploads the directory as the artifact
`version-compare`. Use it to decide major or minor and to write Notable changes. It
compares only the repository's own tests, not users' patterns.

## What the release workflow does

`.github/workflows/release.yml` runs on every push to `main`, that is, on the merge
commit of a release pull request:

1. `release.py tag` checks that the commit is the merge of a pull request from
   `develop` or `hot_fix` of this repository, that `VERSION` is a legal successor, and
   that no tag of that name points elsewhere; it creates the annotated tag locally, so
   the jar is built with the plain name.
2. Build, regression tests and unit tests.
3. `release.py publish` pushes the tag, creates the GitHub Release as a draft with the
   release note as its body, uploads the assets, then publishes it as the latest
   release. Then it updates the issues as described under [Issues](#issues).
   The assets are `pssgen_vX.Y.Z.jar`, `antlr-4.9.3-complete.jar` (needed at run time)
   and `SHA256SUMS`; the Artifacts section lists their checksums. The native
   executable is not a release artifact.
4. `release.py sync develop` and `release.py sync hot_fix` bring both branches to the
   tag as described under [Branches](#branches). A sync merge is built and tested
   (`make -C src && make -C tests`) and pushed only if that passes. Pushes are plain
   pushes.

The two syncs are independent: a failure of the first does not skip the second.
A tag or branch pushed by the workflow starts no other workflow, which is why all of
this is in one workflow.

## Recovery

Every step first checks what is already done, so re-running the release workflow from
the Actions tab is the recovery for a failed run. A re-run builds again, and:

- A draft release gets all its assets replaced by the new build and is then published.
- A published release is never modified. A re-run only checks that it is complete
  (every asset uploaded, the checksums in its body matching) and stops with an error
  when it is not; correct such a release by hand.
- Issue state is recomputed from GitHub, so an issue changed by hand after the merge
  may be changed back.

The workflow keeps at most one run waiting behind the running one. When several release
pull requests are merged in quick succession, a newer run can replace an older pending
run, which is then cancelled and has to be re-run by hand (use the workflow's "Re-run"
for the merge commit that was skipped).

### A sync that needs a maintainer

When a sync merge changes anything under `.github/workflows/`, conflicts, fails the
tests, or is rejected by `origin` (for example because the branch moved on), the
workflow leaves the branch as it is and opens an issue "Sync vX.Y.Z into <branch>"
with the exact commands. The sync is done by hand, with a normal push and never a
force push:

```
$ git fetch origin
$ git switch <branch>
$ git merge --ff-only origin/<branch>
$ git merge --no-ff vX.Y.Z          # --ff-only when <branch> is an ancestor of the tag
$ make -C src && make -C tests      # resolve conflicts and commit the merge first
$ git push origin <branch>
```

Close the issue once the branch contains the tag. Until then no new release pull
request from that branch passes release-check.
