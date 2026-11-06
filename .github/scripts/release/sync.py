"""Bring a long-lived branch up to a new release tag.

After a release is tagged on main, develop and hot_fix are synced to the tag
and never rewritten: a branch that is an ancestor of the tag is moved to it,
any other branch gets a merge of the tag, which is pushed only when it merges
cleanly and the test command passes on it. Every push is a plain push, so a
branch that moved on origin meanwhile rejects it. When the sync cannot be
done here, an issue asks the maintainer to do the sync merge by hand.

The merge is made in a temporary worktree, so the checkout of the caller
stays where it is.
"""
import os
import shlex
import shutil
import subprocess
import tempfile

UP_TO_DATE = "up-to-date"
FAST_FORWARD = "fast-forward"
MERGED = "merged"
NEEDS_MANUAL = "needs-manual"
MANUAL_TEST = "make -C src && make -C tests"


class SyncError(RuntimeError):
    pass


def _run(repo, *args):
    return subprocess.run(["git", "-C", repo, *args], capture_output=True,
                          text=True)


def _git(repo, *args):
    r = _run(repo, *args)
    if r.returncode != 0:
        raise SyncError("git %s failed: %s" % (" ".join(args),
                                               r.stderr.strip()))
    return r.stdout.strip()


def _is_ancestor(repo, a, b):
    return _run(repo, "merge-base", "--is-ancestor", a, b).returncode == 0


def _push(repo, sha, branch):
    """Plain push of sha to branch on origin; the error text, or None."""
    r = _run(repo, "push", "origin", "%s:refs/heads/%s" % (sha, branch))
    if r.returncode != 0:
        return r.stderr.strip()
    return None


def issue_title(tag, branch):
    return "Sync %s into %s" % (tag, branch)


def issue_body(tag, branch, reason):
    return """The release workflow could not sync `{branch}` to the release \
`{tag}`. {reason}

`{branch}` was left as it is on origin. Make the sync merge by hand:

```
git fetch origin
git switch {branch}
git merge --ff-only origin/{branch}
git merge --no-ff {tag}
# resolve the conflicts, if any, and commit the merge
{test}
git push origin {branch}
```

Push only when the build and the regression pass. Never rebase or force-push
`{branch}`. Close this issue once `{branch}` contains `{tag}`.
""".format(branch=branch, tag=tag, reason=reason, test=MANUAL_TEST)


def _needs_manual(gh, tag, branch, reason):
    title = issue_title(tag, branch)
    for issue in gh.open_issues():
        if issue.get("title") == title:
            print("issue #%d is open already: %s" % (issue["number"], title))
            return NEEDS_MANUAL
    n = gh.create_issue(title, issue_body(tag, branch, reason))
    print("opened issue #%d: %s" % (n, title))
    return NEEDS_MANUAL


def _merge(repo, branch, tag, tag_sha, base, test_cmd):
    """(merge commit, None) of a tested merge, or (None, reason)."""
    tmp = tempfile.mkdtemp(prefix="sync-")
    work = os.path.join(tmp, branch)
    try:
        _git(repo, "worktree", "add", "-q", "--detach", work, base)
        r = _run(work, "merge", "--no-ff", "--no-edit", "-m",
                 "Merge %s into %s" % (tag, branch), tag_sha)
        if r.returncode != 0:
            conflicts = _run(work, "diff", "--name-only",
                             "--diff-filter=U").stdout.split()
            if not conflicts:
                raise SyncError("git merge %s failed: %s"
                                % (tag, (r.stderr or r.stdout).strip()))
            return None, ("Merging `%s` into `%s` conflicts in:\n\n%s"
                          % (tag, branch,
                             "\n".join("- `%s`" % f for f in conflicts)))
        merge = _git(work, "rev-parse", "HEAD")
        if test_cmd is not None:
            print("testing the merge of %s into %s: %s"
                  % (tag, branch, shlex.join(test_cmd)), flush=True)
            code = subprocess.run(test_cmd, cwd=work).returncode
            if code != 0:
                return None, ("`%s` merges cleanly into `%s`, but `%s` "
                              "failed on the merge (exit %d), so the merge "
                              "was not pushed. The log of the workflow run "
                              "has the test output."
                              % (tag, branch, shlex.join(test_cmd), code))
        return merge, None
    finally:
        shutil.rmtree(tmp, ignore_errors=True)
        _run(repo, "worktree", "prune")


def run(repo, gh, branch, tag, test_cmd):
    """Sync branch on origin to tag; returns the outcome.

    The outcome is up-to-date, fast-forward, merged or needs-manual. A sync
    merge is tested with test_cmd (an argument list run in the merge result)
    and pushed untested when test_cmd is None.
    """
    tag_sha = _git(repo, "rev-parse", "--verify", "%s^{commit}" % tag)
    _git(repo, "fetch", "-q", "origin", "refs/heads/%s" % branch)
    base = _git(repo, "rev-parse", "--verify", "FETCH_HEAD^{commit}")
    if _is_ancestor(repo, tag_sha, base):
        print("%s contains %s already" % (branch, tag))
        return UP_TO_DATE
    if _is_ancestor(repo, base, tag_sha):
        err = _push(repo, tag_sha, branch)
        if err is not None:
            return _needs_manual(gh, tag, branch, (
                "The fast-forward of `%s` to `%s` was rejected; `%s` moved "
                "on origin meanwhile:\n\n```\n%s\n```" % (branch, tag, branch,
                                                         err)))
        print("fast-forwarded %s to %s" % (branch, tag))
        return FAST_FORWARD
    merge, reason = _merge(repo, branch, tag, tag_sha, base, test_cmd)
    if merge is None:
        return _needs_manual(gh, tag, branch, reason)
    err = _push(repo, merge, branch)
    if err is not None:
        return _needs_manual(gh, tag, branch, (
            "The tested merge of `%s` into `%s` was rejected on push; `%s` "
            "moved on origin meanwhile:\n\n```\n%s\n```" % (tag, branch,
                                                           branch, err)))
    print("merged %s into %s as %s" % (tag, branch, merge))
    return MERGED
