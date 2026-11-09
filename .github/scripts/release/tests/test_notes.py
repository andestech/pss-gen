"""Tests for notes.py."""
import unittest

import gitrange
import notes
from issues import Issue


def issue(n, title="t", kind="feature"):
    return Issue(n, title, kind, "open", [])


class DraftTest(unittest.TestCase):
    def test_draft_has_sections_in_order(self):
        text = notes.draft([issue(1, "feat")], [issue(2, "bug", "bug")], [])
        heads = [ln[3:] for ln in text.splitlines() if ln.startswith("## ")]
        self.assertEqual(heads, notes.SECTIONS)
        sections = notes.parse(text)
        self.assertEqual(sections["Features"], "- #1 feat")
        self.assertEqual(sections["Bug fixes"], "- #2 bug")
        self.assertEqual(sections["Notable changes"], "None")
        self.assertIn("<!-- write -->", sections["Summary"])

    def test_draft_no_closing_keywords(self):
        text = notes.draft([issue(1, "Fix the thing")],
                           [issue(2, "Resolve crash", "bug")],
                           [issue(3, "Close gap")])
        self.assertEqual(gitrange.closing_refs(text), set())

    def test_title_closing_keyword_neutralised(self):
        text = notes.draft([issue(1, "Fix #5 crash")], [], [])
        self.assertEqual(gitrange.closing_refs(text), set())
        self.assertIn("- #1 Fix issue 5 crash", text)

    def test_validate_closing_keyword_in_body(self):
        body = notes.draft([], [], []).replace("<!-- write -->", "text")
        self.assertEqual(notes.validate(body), [])
        bad = body.replace("text", "Closes #7", 1)
        errs = notes.validate(bad)
        self.assertEqual(len(errs), 1)
        self.assertIn("#7", errs[0])
        self.assertEqual(notes.validate(bad, closing_keywords=False), [])

    def test_validate_missing_and_placeholder(self):
        body = notes.draft([], [], [])
        self.assertTrue(any("Summary" in e for e in notes.validate(body)))
        no_artifacts = body.split("## Artifacts")[0]
        self.assertTrue(any("Artifacts" in e and "missing" in e
                            for e in notes.validate(no_artifacts)))
        ok = body.replace("<!-- write -->", "text")
        self.assertEqual(notes.validate(ok), [])
        emptied = ok.replace("Filled in by the release workflow.", "")
        self.assertTrue(any("Artifacts" in e for e in notes.validate(emptied)))

    def test_none_allowed(self):
        body = notes.draft([], [], []).replace("<!-- write -->", "None")
        errs = notes.validate(body)
        self.assertEqual(errs, [e for e in errs if "Summary" in e])
        self.assertEqual(len(errs), 1)

    def test_contribution_lines(self):
        template = ("## What and why\n\nFixes #3\n\n## Release note\n\n"
                    "<!-- One line for the release note, starting with Feature:,\n"
                    "Bug fix: or Notable change:, or None. -->\n\n%s\n\n"
                    "## Target branch\n\n- Features: target `develop`.\n")
        cases = [
            ("Bug fix: crash on empty input",
             ([("Bug fixes", "crash on empty input")], True)),
            ("- Feature: option -x\nNotable change: output of y changes",
             ([("Features", "option -x"),
               ("Notable changes", "output of y changes")], True)),
            ("None", ([], True)),
            ("", ([], False)),
            ("Fixed the parser", ([], False)),
            ("Feature: a\nsomething else", ([("Features", "a")], False)),
        ]
        for line, expected in cases:
            with self.subTest(line=line):
                self.assertEqual(notes.contribution_lines(template % line),
                                 expected)
        self.assertEqual(notes.contribution_lines(None), ([], False))
        self.assertEqual(notes.contribution_lines("Fixes #3"), ([], False))

    def test_draft_contributions(self):
        bug = notes.Contribution(12, "Bug fixes", "Fixes #4 crash")
        notable = notes.Contribution(13, "Notable changes", "output changes")
        found = notes.parse(notes.draft([], [], [], [bug, notable]))
        self.assertEqual(found["Bug fixes"], "- Fixes issue 4 crash (#12)")
        self.assertEqual(found["Notable changes"], "- output changes (#13)")
        self.assertEqual(found["Features"], "None")

    def test_listed(self):
        text = notes.draft([issue(1), issue(8)], [issue(2, kind="bug")], [])
        self.assertEqual(notes.listed(text, "Features"), {1, 8})
        self.assertEqual(notes.listed(text, "Bug fixes"), {2})
        self.assertEqual(notes.listed(text, "Nope"), set())

    def test_fill_artifacts(self):
        text = notes.draft([], [], [])
        out = notes.fill_artifacts(text, [("a.jar", "aa"), ("b.jar", "bb")])
        art = notes.parse(out)["Artifacts"]
        self.assertIn("a.jar", art)
        self.assertIn("bb", art)
        self.assertNotIn("Filled in", art)
        again = notes.fill_artifacts(out, [("c.jar", "cc")])
        self.assertNotIn("a.jar", again)
        self.assertEqual(notes.parse(again)["Summary"],
                         notes.parse(text)["Summary"])


class PreambleTest(unittest.TestCase):
    CHECKLIST = "- [ ] Read the comparison report\n\n"

    def test_text_before_first_heading_ignored(self):
        body = self.CHECKLIST + notes.draft([], [], []).replace(
            "<!-- write -->", "text")
        self.assertEqual(notes.validate(body), [])
        self.assertNotIn("Read the comparison", " ".join(
            notes.parse(body).values()))

    def test_release_body_drops_checklist(self):
        import publish
        body = self.CHECKLIST + "## Summary\n\ntext\n"
        out = publish.release_body(body, [("a.jar", "00")])
        self.assertNotIn("comparison report", out)
        self.assertTrue(out.startswith("## Summary"))


if __name__ == "__main__":
    unittest.main()
