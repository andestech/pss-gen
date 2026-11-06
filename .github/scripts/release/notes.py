"""Release-note draft, parsing and validation.

The note lives in the release PR description and later in the GitHub Release
body; it has six fixed sections.
"""
import re

SECTIONS = ["Summary", "Notable changes", "Features", "Bug fixes",
            "Known issues / limitations", "Artifacts"]
PLACEHOLDER = "<!-- write -->"
ARTIFACTS_FILLER = "Filled in by the release workflow."
NONE_OK = {"Notable changes", "Features", "Bug fixes",
           "Known issues / limitations"}
HEADING_RE = re.compile(r"^## +(.*?) *$")
REF_RE = re.compile(r"#(\d+)")


def _items(issues):
    if not issues:
        return "None"
    return "\n".join("- #%d %s" % (i.number, i.title) for i in issues)


def draft(features, bugs, incompatible):
    content = {
        "Summary": PLACEHOLDER,
        "Notable changes": _items(incompatible),
        "Features": _items(features),
        "Bug fixes": _items(bugs),
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


def validate(body):
    found = parse(body or "")
    errors = []
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
