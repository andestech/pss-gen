"""Tag and publish a release PR that was merged into main.

The release workflow runs this on the merge commit: tag_local before the
build (so the jar carries the plain version name), run after it. Every step
first checks whether it is already done, so a re-run after a partial failure
completes the missing steps and repeats none. Everything that can refuse the
release is checked before the first push or API write.

The GitHub Release stays a draft until all of its assets are uploaded. The
jar is rebuilt on every run and need not be byte-identical, so a draft gets
the assets and the checksums of the current build, all replaced together.
A published release is never changed: its assets are the released build, so
a re-run checks the release against itself, not against its own rebuild.
"""
import hashlib
import os
import re
import subprocess
import sys
from datetime import datetime

import gitrange
import notes
import version
from gh import GitHubError

SUMS = "SHA256SUMS"
FIXED_SECTIONS = ("Features", "Bug fixes")
KNOWN_SECTION = "Known issues / limitations"
KNOWN_LABEL = "known-issue"
SUM_LINE_RE = re.compile(r"^- `([^`]+)` +([0-9a-f]{64})$")


class PublishError(RuntimeError):
    pass


def _warn(msg):
    """A warning on stderr and, in a workflow, in the job summary."""
    print("warning: " + msg, file=sys.stderr)
    summary = os.environ.get("GITHUB_STEP_SUMMARY")
    if summary:
        with open(summary, "a") as f:
            f.write("- warning: %s\n" % msg)


def _git(repo, *args):
    r = subprocess.run(["git", "-C", repo, *args], capture_output=True,
                       text=True)
    if r.returncode != 0:
        raise PublishError("git %s failed: %s" % (" ".join(args),
                                                  r.stderr.strip()))
    return r.stdout


def _local_tag(repo, tag):
    r = subprocess.run(["git", "-C", repo, "rev-parse", "-q", "--verify",
                        "refs/tags/%s^{commit}" % tag],
                       capture_output=True, text=True)
    return r.stdout.strip() if r.returncode == 0 else None


def _remote_tag(repo, tag):
    """Commit the tag names on origin, or None when origin has no such tag."""
    found = {}
    for line in _git(repo, "ls-remote", "--tags", "origin").splitlines():
        obj, ref = line.split("\t", 1)
        found[ref] = obj
    ref = "refs/tags/" + tag
    return found.get(ref + "^{}", found.get(ref))


def release_pr(gh, sha):
    """The PR whose merge into main created sha."""
    for pr in gh.pulls_of_commit(sha) or []:
        if pr.get("merge_commit_sha") == sha and pr["base"]["ref"] == "main":
            return pr
    raise PublishError("no pull request into main was merged as %s; a "
                       "release is only published from a merged release PR"
                       % sha)


def _check(repo, gh, sha):
    """(tag, pr, remote tag commit) of the release merged as sha."""
    try:
        new = version.parse(_git(repo, "show", "%s:VERSION" % sha))
    except ValueError as e:
        raise PublishError("VERSION at %s: %s" % (sha, e)) from None
    tag = version.fmt(new)
    remote = _remote_tag(repo, tag)
    if remote is not None and remote != sha:
        raise PublishError("tag %s on origin points at %s, not at the merge "
                           "commit %s" % (tag, remote, sha))
    local = _local_tag(repo, tag)
    if local is not None and local != sha:
        raise PublishError("local tag %s points at %s, not at the merge "
                           "commit %s" % (tag, local, sha))
    pr = release_pr(gh, sha)
    head_repo = ((pr["head"].get("repo") or {}).get("full_name")
                 or "an unknown repository")
    base_repo = pr["base"]["repo"]["full_name"]
    if head_repo != base_repo:
        raise PublishError("PR #%d comes from %s, not from %s"
                           % (pr["number"], head_repo, base_repo))
    prev = version.previous_release(repo, "%s^1" % sha)
    if prev is None:
        raise PublishError("main before %s has no release tag" % sha)
    errors = version.check_successor(prev, new, pr["head"]["ref"])
    if errors:
        raise PublishError("; ".join(errors))
    return tag, pr, remote


def tag_local(repo, gh, sha):
    """Create the annotated release tag on sha unless it exists.

    Returns the action done, or None when the tag was already there.
    """
    tag, pr, _ = _check(repo, gh, sha)
    if _local_tag(repo, tag) is not None:
        return None
    _git(repo, "tag", "-a", tag, "-m", "%s (#%d)" % (tag, pr["number"]), sha)
    return "create tag %s" % tag


def _sha256(path):
    h = hashlib.sha256()
    with open(path, "rb") as f:
        for chunk in iter(lambda: f.read(1 << 16), b""):
            h.update(chunk)
    return h.hexdigest()


def _assets(dist, tag):
    """[(name, path, sha256)] of dist, SHA256SUMS last, after checking it."""
    names = sorted(n for n in os.listdir(dist)
                   if os.path.isfile(os.path.join(dist, n)))
    jar = "pssgen_%s.jar" % tag
    for need in (jar, SUMS):
        if need not in names:
            raise PublishError("%s is missing from %s" % (need, dist))
    files = [(n, os.path.join(dist, n)) for n in names if n != SUMS]
    files.append((SUMS, os.path.join(dist, SUMS)))
    assets = [(n, p, _sha256(p)) for n, p in files]
    listed = {}
    with open(os.path.join(dist, SUMS)) as f:
        for line in f:
            if line.strip():
                digest, name = line.split(None, 1)
                listed[name.strip().lstrip("*")] = digest
    actual = {n: d for n, _, d in assets if n != SUMS}
    if listed != actual:
        raise PublishError("%s does not match the files in %s"
                           % (SUMS, dist))
    return assets


def release_body(pr_body, sums):
    """The PR description from its first section on, Artifacts filled."""
    lines = (pr_body or "").replace("\r\n", "\n").split("\n")
    start = next((i for i, line in enumerate(lines)
                  if notes.HEADING_RE.match(line)), len(lines))
    return notes.fill_artifacts("\n".join(lines[start:]), sums)


def _time(stamp):
    return datetime.fromisoformat(stamp.replace("Z", "+00:00"))


def _closed_by_merge(issue, merged_at):
    """True when the issue was closed at or after the merge.

    An issue closed before the merge was closed by hand and stays closed.
    """
    if issue["state"] != "closed":
        return False
    if not issue.get("closed_at") or not merged_at:
        return True
    return _time(issue["closed_at"]) >= _time(merged_at)


def _issue(gh, n):
    """The issue #n, or None for a pull request or an unknown number."""
    try:
        payload = gh.issue(n)
    except GitHubError as e:
        if e.status != 404:
            raise
        _warn("#%d is not known to GitHub; skipped" % n)
        return None
    if "pull_request" in payload:
        return None
    return payload


def _issue_plan(repo, gh, sha, pr):
    """[(action, write)] of the issue handling, from the current states."""
    body = pr.get("body") or ""
    fixed = set()
    for section in FIXED_SECTIONS:
        fixed |= notes.listed(body, section)
    known = notes.listed(body, KNOWN_SECTION)
    both = fixed & known
    if both:
        _warn("listed both as fixed and as known issues, left as they are: "
              "%s" % ", ".join("#%d" % n for n in sorted(both)))
    closing = gitrange.closing_refs(body)
    for c in gitrange.commits(repo, "%s^1..%s" % (sha, sha)):
        closing |= gitrange.closing_refs(c.subject + "\n" + c.body)

    plan = []
    for n in sorted(fixed - both):
        issue = _issue(gh, n)
        if issue and issue["state"] != "closed":
            plan.append(("close #%d" % n, lambda n=n: gh.set_issue_state(
                n, "closed", "completed")))
    for n in sorted(closing - fixed - known):
        issue = _issue(gh, n)
        if issue and _closed_by_merge(issue, pr.get("merged_at")):
            plan.append(("reopen #%d" % n, lambda n=n: gh.set_issue_state(
                n, "open", "reopened")))
    for n in sorted(known - both):
        issue = _issue(gh, n)
        if not issue:
            continue
        if issue["state"] == "closed":
            plan.append(("reopen #%d" % n, lambda n=n: gh.set_issue_state(
                n, "open", "reopened")))
        if KNOWN_LABEL not in [x["name"] for x in issue.get("labels") or []]:
            plan.append(("label #%d %s" % (n, KNOWN_LABEL),
                         lambda n=n: gh.add_labels(n, [KNOWN_LABEL])))
    return plan


def _body_sums(body):
    """{name: sha256} listed in the Artifacts section of a release body."""
    text = notes.parse(body or "").get("Artifacts", "")
    found = {}
    for line in text.split("\n"):
        m = SUM_LINE_RE.match(line.strip())
        if m:
            found[m.group(1)] = m.group(2)
    return found


def _check_published(release, assets):
    """Refuse when a published release is not consistent in itself.

    Every asset name of the build must be uploaded, and the checksum its
    body lists must be the digest GitHub reports for that asset.
    """
    present = {a["name"]: a for a in release.get("assets") or []}
    listed = _body_sums(release.get("body"))
    problems = []
    for name, _, _ in assets:
        a = present.get(name)
        if a is None:
            problems.append("%s is missing" % name)
        elif a.get("state") != "uploaded":
            problems.append("%s is not fully uploaded (state %s)"
                            % (name, a.get("state")))
        elif name not in listed:
            problems.append("the body lists no checksum of %s" % name)
        elif a.get("digest") and a["digest"] != "sha256:" + listed[name]:
            problems.append("the checksum of %s in the body is not the "
                            "uploaded file's" % name)
    if problems:
        raise PublishError(
            "release %s is published and is left as it is: %s; the "
            "maintainer decides how to correct it"
            % (release["tag_name"], "; ".join(problems)))


def run(repo, gh, sha, dist, dry_run):
    """Publish the release merged as sha; returns the actions done.

    With dry_run the same actions are computed and returned, and nothing is
    pushed or written to GitHub.
    """
    tag, pr, remote = _check(repo, gh, sha)
    if not dry_run and remote is None and _local_tag(repo, tag) is None:
        raise PublishError("the local tag %s is missing; create it with "
                           "'release.py tag' before the build" % tag)
    assets = _assets(dist, tag)
    sums = [(name, digest) for name, _, digest in assets]
    body = release_body(pr.get("body"), sums)
    release = gh.release_by_tag(tag)
    published = release is not None and not release.get("draft")
    if published:
        _check_published(release, assets)
    issue_plan = _issue_plan(repo, gh, sha, pr)

    done = []
    if remote is None:
        if not dry_run:
            _git(repo, "push", "origin", "refs/tags/%s" % tag)
        done.append("push tag %s" % tag)
    if not published:
        stale = (release or {}).get("assets") or []
        if release is None:
            if not dry_run:
                release = gh.create_release(tag, tag, body, False, draft=True)
            done.append("create draft release %s" % tag)
        for a in stale:
            if not dry_run:
                gh.delete_asset(a["id"])
            done.append("delete %s" % a["name"])
        for name, path, _ in assets:
            if not dry_run:
                gh.upload_asset(release, path)
            done.append("upload %s" % name)
        if not dry_run:
            gh.publish_release(release["id"], body)
        done.append("publish release %s" % tag)
    for action, write in issue_plan:
        if not dry_run:
            write()
        done.append(action)
    return done
