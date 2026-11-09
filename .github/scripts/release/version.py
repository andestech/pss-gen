"""Version rules of the release flow.

VERSION holds one `vX.Y.Z` string; this module parses it, bumps it and
checks that a new version is a legal successor of the previous release on
the branch the release is cut from.
"""
import re
import subprocess

VERSION_RE = re.compile(r"^v(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)$")

Version = tuple


def parse(text):
    m = VERSION_RE.match(text.strip())
    if not m:
        raise ValueError("not a version of the form vX.Y.Z: %r" % text)
    return tuple(int(g) for g in m.groups())


def fmt(v):
    return "v%d.%d.%d" % v


def previous_release(repo, rev):
    """Highest version tag reachable from rev, or None when there is none."""
    out = subprocess.run(
        ["git", "-C", repo, "tag", "--merged", rev],
        check=True, capture_output=True, text=True).stdout
    found = [parse(t) for t in out.split() if VERSION_RE.match(t)]
    return max(found) if found else None


def next_version(prev, kind):
    major, minor, patch = prev
    if kind == "major":
        return (major + 1, 0, 0)
    if kind == "minor":
        return (major, minor + 1, 0)
    if kind == "patch":
        return (major, minor, patch + 1)
    raise ValueError("kind must be major, minor or patch: %r" % kind)


def check_successor(prev, new, branch):
    """Errors for releasing `new` from `branch` on top of `prev`."""
    if branch == "hot_fix":
        allowed = [next_version(prev, "patch")]
    elif branch == "develop":
        allowed = [next_version(prev, "minor"), next_version(prev, "major")]
    else:
        return ["releases are cut from develop or hot_fix, not %s" % branch]
    if new in allowed:
        return []
    return ["%s is not a valid successor of %s on %s (expected %s)" % (
        fmt(new), fmt(prev), branch, " or ".join(fmt(a) for a in allowed))]
