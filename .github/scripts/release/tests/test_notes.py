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


if __name__ == "__main__":
    unittest.main()
