"""Issue classification of a release range.

An issue is a feature or a bug by its GitHub issue type, falling back to the
`enhancement` / `bug` label; anything else is unclassified.
"""
from dataclasses import dataclass

import gitrange
from gh import GitHubError

TYPE_KINDS = {"Feature": "feature", "Bug": "bug"}
LABEL_KINDS = {"enhancement": "feature", "bug": "bug"}


@dataclass
class Issue:
    number: int
    title: str
    kind: str
    state: str
    labels: list


def classify(payload):
    type_name = (payload.get("type") or {}).get("name")
    if type_name in TYPE_KINDS:
        return TYPE_KINDS[type_name]
    for label in payload.get("labels") or []:
        if label["name"] in LABEL_KINDS:
            return LABEL_KINDS[label["name"]]
    return "unclassified"


def to_issue(payload):
    return Issue(payload["number"], payload.get("title") or "",
                 classify(payload), payload["state"],
                 [label["name"] for label in payload.get("labels") or []])


def resolve(gh, refs):
    """(issues, prs, missing): a PR ref contributes the issues its body closes.

    A ref GitHub answers with 404 is reported in missing; other errors raise.
    """
    cache = {}

    def fetch(n):
        if n not in cache:
            try:
                cache[n] = gh.issue(n)
            except GitHubError as e:
                if e.status != 404:
                    raise
                cache[n] = None
        return cache[n]

    prs, missing, numbers = set(), set(), set()
    for n in sorted(refs):
        payload = fetch(n)
        if payload is None:
            missing.add(n)
        elif "pull_request" in payload:
            prs.add(n)
            numbers |= gitrange.closing_refs(payload.get("body") or "")
        else:
            numbers.add(n)
    found = []
    for n in sorted(numbers):
        payload = fetch(n)
        if payload is None:
            missing.add(n)
        else:
            found.append(to_issue(payload))
    return found, prs, missing
