"""Issue classification of a release range.

An issue is a feature or a bug by its GitHub issue type, falling back to the
`enhancement` / `bug` label; anything else is unclassified.
"""
from dataclasses import dataclass

import gitrange

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
    """(issues, prs): a PR ref contributes the issues its body closes."""
    prs = set()
    numbers = set()
    for n in sorted(refs):
        payload = gh.issue(n)
        if "pull_request" in payload:
            prs.add(n)
            numbers |= gitrange.closing_refs(payload.get("body") or "")
        else:
            numbers.add(n)
    found = [to_issue(gh.issue(n)) for n in sorted(numbers)]
    return found, prs
