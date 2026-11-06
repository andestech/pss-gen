"""Commit range and issue references of a release.

Reads the commits between the previous release and the release branch and
extracts the `#N` references that drive the release notes.
"""
import re
import subprocess
from dataclasses import dataclass

REF_RE = re.compile(r"#(\d+)")
CLOSING_RE = re.compile(
    r"\b(?:close[sd]?|fix(?:e[sd])?|resolve[sd]?)\b:?[ \t]*#(\d+)",
    re.IGNORECASE)
RELEASE_RE = re.compile(r"^\[RELEASE\] v\d+\.\d+\.\d+$")
REVERT_PREFIX = 'Revert "'


@dataclass
class Commit:
    sha: str
    subject: str
    body: str
    is_merge: bool


def commits(repo, rev_range):
    """Commits of rev_range, oldest first."""
    out = subprocess.run(
        ["git", "-C", repo, "log", "--reverse", "--topo-order",
         "--format=%H%x1f%P%x1f%s%x1f%b%x1e", rev_range],
        check=True, capture_output=True, text=True).stdout
    result = []
    for rec in out.split("\x1e"):
        rec = rec.strip("\n")
        if not rec:
            continue
        sha, parents, subject, body = rec.split("\x1f", 3)
        result.append(Commit(sha, subject, body, len(parents.split()) > 1))
    return result


def refs(c):
    return {int(n) for n in REF_RE.findall(c.subject + "\n" + c.body)}


def closing_refs(text):
    return {int(n) for n in CLOSING_RE.findall(text)}


def fixed_issues(cs):
    """(fixed, reverted): the last commit that mentions an issue decides."""
    fixed, reverted = set(), set()
    for c in cs:
        if c.is_merge:
            continue
        found = refs(c)
        if c.subject.startswith(REVERT_PREFIX):
            reverted |= found
            fixed -= found
        else:
            fixed |= found
            reverted -= found
    return fixed, reverted


def is_release_commit(c):
    return bool(RELEASE_RE.match(c.subject))


def unreferenced(cs):
    return [c for c in cs
            if not c.is_merge and not is_release_commit(c) and not refs(c)]
