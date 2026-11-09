"""Release-note draft, parsing and validation.

The note lives in the release PR description and later in the GitHub Release
body; it has six fixed sections.
"""
import re
from dataclasses import dataclass

import gitrange

SECTIONS = ["Summary", "Notable changes", "Features", "Bug fixes",
            "Known issues / limitations", "Artifacts"]
PLACEHOLDER = "<!-- write -->"
ARTIFACTS_FILLER = "Filled in by the release workflow."
NONE_OK = {"Notable changes", "Features", "Bug fixes",
           "Known issues / limitations"}
HEADING_RE = re.compile(r"^## +(.*?) *$")
REF_RE = re.compile(r"#(\d+)")
# The "Release note" section of a contribution PR (pull_request_template.md)
# holds one line per change, or None.
CONTRIBUTION_SECTION = "Release note"
CONTRIBUTION_KINDS = {"Feature": "Features", "Bug fix": "Bug fixes",
                      "Notable change": "Notable changes"}
CONTRIBUTION_RE = re.compile(r"^(Feature|Bug fix|Notable change): *(\S.*)$")
COMMENT_RE = re.compile(r"<!--.*?-->", re.S)


@dataclass
class Contribution:
    """A release-note line of the contribution PR #pr, for `section`."""
    pr: int
    section: str
    text: str


def _title(title):
    """A closing keyword before a #N in a title would close that issue."""
    if gitrange.closing_refs(title):
        return REF_RE.sub(r"issue \1", title)
    return title


def _items(issues, contributions=()):
    lines = ["- #%d %s" % (i.number, _title(i.title)) for i in issues]
    lines += ["- %s (#%d)" % (_title(c.text), c.pr) for c in contributions]
    return "\n".join(lines) or "None"


def draft(features, bugs, incompatible, contributions=()):
    def extra(section):
        return [c for c in contributions if c.section == section]

    content = {
        "Summary": PLACEHOLDER,
        "Notable changes": _items(incompatible, extra("Notable changes")),
        "Features": _items(features, extra("Features")),
        "Bug fixes": _items(bugs, extra("Bug fixes")),
        "Known issues / limitations": PLACEHOLDER,
        "Artifacts": ARTIFACTS_FILLER,
    }
    return "\n\n".join("## %s\n\n%s" % (s, content[s]) for s in SECTIONS) + "\n"


def parse(body):
    """Map each `## ` heading to the stripped text below it."""
    sections, current, lines = {}, None, []
    for line in body.replace("\r\n", "\n").split("\n"):
        m = HEADING_RE.match(line)
        if m:
            if current is not None:
                sections[current] = "\n".join(lines).strip()
            current, lines = m.group(1), []
        elif current is not None:
            lines.append(line)
    if current is not None:
        sections[current] = "\n".join(lines).strip()
    return sections


def contribution_lines(body):
    """([(section, text)], ok) of the release-note lines of a contribution PR.

    ok is False when the PR has no such line, or a line that is neither None
    nor starts with Feature:, Bug fix: or Notable change:.
    """
    text = COMMENT_RE.sub("", parse(body or "").get(CONTRIBUTION_SECTION, ""))
    lines, ok, seen = [], True, False
    for raw in text.split("\n"):
        line = raw.strip()
        if line[:2] in ("- ", "* "):
            line = line[2:].strip()
        if not line:
            continue
        seen = True
        m = CONTRIBUTION_RE.match(line)
        if m:
            lines.append((CONTRIBUTION_KINDS[m.group(1)], m.group(2).strip()))
        elif line != "None":
            ok = False
    return lines, ok and seen


def validate(body, closing_keywords=True):
    """Errors of the release note in a release PR description.

    closing_keywords=False skips the closing-keyword check, for a PR that is
    already merged: its keywords have taken effect.
    """
    found = parse(body or "")
    errors = []
    closing = gitrange.closing_refs(body or "") if closing_keywords else set()
    if closing:
        errors.append("release note: a closing keyword before %s would close "
                      "the issue when the PR merges; reword it"
                      % ", ".join("#%d" % n for n in sorted(closing)))
    for name in SECTIONS:
        if name not in found:
            errors.append("release note: section '## %s' is missing" % name)
            continue
        text = found[name]
        if PLACEHOLDER in text:
            errors.append("release note: section '%s' still holds the "
                          "placeholder" % name)
        elif not text:
            errors.append("release note: section '%s' is empty" % name)
        elif text == "None" and name not in NONE_OK:
            errors.append("release note: section '%s' cannot be None" % name)
    return errors


def listed(body, section):
    return {int(n) for n in REF_RE.findall(parse(body or "").get(section, ""))}


def fill_artifacts(body, sums):
    lines = "\n".join("- `%s`  %s" % (name, digest) for name, digest in sums)
    out, skipping, seen = [], False, False
    for line in body.replace("\r\n", "\n").split("\n"):
        m = HEADING_RE.match(line)
        if m:
            skipping = m.group(1) == "Artifacts"
            if skipping:
                seen = True
                out += [line, "", lines, ""]
                continue
        if not skipping:
            out.append(line)
    text = "\n".join(out).rstrip("\n") + "\n"
    if not seen:
        text += "\n## Artifacts\n\n%s\n" % lines
    return text
