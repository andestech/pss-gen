"""Gate of a release PR into main.

Applies the version rules and the release-note rules to the PR and builds
the report that is shown in the job summary: errors, warnings, the
release-note draft and the issues the merge would auto-close.
"""
import subprocess
from dataclasses import dataclass, field

import gitrange
import issues
import notes
import version
from issues import Issue

RELEASE_BRANCHES = ("develop", "hot_fix")


@dataclass
class Report:
    errors: list = field(default_factory=list)
    warnings: list = field(default_factory=list)
    autoclose: list = field(default_factory=list)
    draft: str = ""


@dataclass
class Range:
    features: list
    bugs: list
    incompatible: list
    unclassified: list
    missing: set
    empty_prs: list
    reverted: set
    commits: list


def _git(repo, *args):
    return subprocess.run(["git", "-C", repo, *args], capture_output=True,
                          text=True)


def collect(repo, gh, rev_range):
    """Classify the issues of a commit range for the release note."""
    cs = gitrange.commits(repo, rev_range)
    fixed, reverted = gitrange.fixed_issues(cs)
    found, prs, missing = issues.resolve(gh, fixed)
    incompatible = [i for i in found if "incompatible" in i.labels]
    empty_prs = []
    for n in sorted(prs):
        payload = gh.issue(n)
        if not gitrange.closing_refs(payload.get("body") or ""):
            empty_prs.append(n)
        pr_issue = issues.to_issue(payload)
        if "incompatible" in pr_issue.labels:
            incompatible.append(pr_issue)
    return Range(
        [i for i in found if i.kind == "feature"],
        [i for i in found if i.kind == "bug"],
        incompatible,
        [i for i in found if i.kind == "unclassified"],
        missing, empty_prs, reverted, cs)


def _refs(numbers):
    return ", ".join("#%d" % n for n in sorted(numbers))


def run(repo, gh, pr):
    rep = Report()
    head_ref = pr["head_ref"]
    if pr["head_repo"] != pr["base_repo"]:
        rep.errors.append("the head branch is in %s, not in this repository "
                          "%s (a fork's branch cannot be released)"
                          % (pr["head_repo"], pr["base_repo"]))
        return rep
    if head_ref not in RELEASE_BRANCHES:
        rep.errors.append("the head branch is '%s'; a PR into main comes from "
                          "develop or hot_fix" % head_ref)
        return rep

    prev = version.previous_release(repo, pr["base_sha"])
    if prev is None:
        rep.errors.append("the base of the PR has no release tag")
        return rep
    prev_tag = version.fmt(prev)

    synced = _git(repo, "merge-base", "--is-ancestor", prev_tag,
                  pr["head_sha"]).returncode == 0
    if not synced:
        rep.errors.append("%s does not contain the previous release %s; "
                          "merge main into it first" % (head_ref, prev_tag))

    shown = _git(repo, "show", "%s:VERSION" % pr["head_sha"])
    try:
        if shown.returncode != 0:
            raise ValueError("VERSION is missing at the head of the PR")
        rep.errors += version.check_successor(
            prev, version.parse(shown.stdout), head_ref)
    except ValueError as e:
        rep.errors.append(str(e))

    rng = collect(repo, gh, "%s..%s" % (prev_tag, pr["head_sha"]))
    rep.draft = notes.draft(rng.features, rng.bugs, rng.incompatible)

    if rng.missing:
        msg = "referenced numbers GitHub does not know: %s" % _refs(rng.missing)
        (rep.errors if head_ref == "hot_fix" else rep.warnings).append(msg)
    if rng.unclassified:
        msg = ("issues that are neither a feature nor a bug: %s"
               % _refs({i.number for i in rng.unclassified}))
        (rep.errors if head_ref == "hot_fix" else rep.warnings).append(msg)
    if head_ref == "hot_fix":
        if rng.features:
            rep.errors.append("a hot fix cannot carry features: %s"
                              % _refs({i.number for i in rng.features}))
        bare = gitrange.unreferenced(rng.commits)
        if bare:
            rep.errors.append("commits without an issue in a hot fix: %s"
                              % ", ".join(c.sha[:9] for c in bare))
        empty = [c for c in rng.commits
                 if gitrange.named_refs(c)
                 and gitrange.named_refs(c) <= set(rng.empty_prs)]
        if empty:
            rep.errors.append("commits that name only a PR whose description "
                              "closes no issue, so they name no bug: %s"
                              % ", ".join(c.sha[:9] for c in empty))
    if rng.reverted:
        rep.warnings.append("fixes reverted in this range, not counted as "
                            "fixed (move to Known issues if still open): %s"
                            % _refs(rng.reverted))

    rep.errors += notes.validate(pr.get("body") or "")

    body = pr.get("body") or ""
    in_note = notes.listed(body, "Features") | notes.listed(body, "Bug fixes")
    closing = gitrange.closing_refs(body)
    for c in rng.commits:
        closing |= gitrange.closing_refs(c.subject + "\n" + c.body)
    rep.autoclose = [(n, n in in_note) for n in sorted(closing)]
    stray = [n for n, listed in rep.autoclose if not listed]
    if stray:
        rep.warnings.append("merging closes issues that are not listed under "
                            "Features or Bug fixes: %s" % _refs(stray))
    return rep


def render(rep):
    out = ["# Release check", ""]
    out.append("Result: %s" % ("FAILED" if rep.errors else "passed"))
    if rep.errors:
        out += ["", "## Errors", ""] + ["- " + e for e in rep.errors]
    if rep.warnings:
        out += ["", "## Warnings", ""] + ["- " + w for w in rep.warnings]
    out += ["", "## Issues closed by the merge", ""]
    if rep.autoclose:
        out += ["| Issue | Listed as fixed |", "| --- | --- |"]
        out += ["| #%d | %s |" % (n, "yes" if ok else "no")
                for n, ok in rep.autoclose]
    else:
        out.append("None")
    out += ["", "## Release note draft", "", "````markdown",
            rep.draft.rstrip("\n"), "````", ""]
    return "\n".join(out)
