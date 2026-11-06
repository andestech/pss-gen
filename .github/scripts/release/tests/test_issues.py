"""Tests for issues.py and the slug detection of gh.py."""
import os
import unittest
from unittest import mock

import gh
import issues
from tests.fake_gh import FakeGitHub
from tests.gitutil import TempRepo


class ClassifyTest(unittest.TestCase):
    def kind(self, **kw):
        fake = FakeGitHub()
        fake.add_issue(1, title="t", **kw)
        found, prs = issues.resolve(fake, {1})
        self.assertEqual(prs, set())
        return found[0].kind

    def test_kind_from_type(self):
        self.assertEqual(self.kind(type_name="Feature"), "feature")
        self.assertEqual(self.kind(type_name="Bug"), "bug")

    def test_kind_from_label(self):
        self.assertEqual(self.kind(labels=["enhancement"]), "feature")
        self.assertEqual(self.kind(labels=["bug"]), "bug")

    def test_type_wins_over_label(self):
        self.assertEqual(self.kind(type_name="Bug", labels=["enhancement"]),
                         "bug")

    def test_unclassified(self):
        self.assertEqual(self.kind(labels=["question"]), "unclassified")

    def test_absent_type_key(self):
        fake = FakeGitHub()
        fake.add_issue(1, title="t")
        del fake.issues[1]["type"]
        found, _ = issues.resolve(fake, {1})
        self.assertEqual(found[0].kind, "unclassified")

    def test_fields(self):
        fake = FakeGitHub()
        fake.add_issue(5, title="T", labels=["bug", "x"], state="closed")
        found, _ = issues.resolve(fake, {5})
        self.assertEqual(found[0], issues.Issue(5, "T", "bug", "closed",
                                                ["bug", "x"]))

    def test_pr_ref_uses_closing_refs(self):
        fake = FakeGitHub()
        fake.add_issue(40, title="bug", type_name="Bug")
        fake.add_pull(41, body="Fixes #40")
        found, prs = issues.resolve(fake, {41})
        self.assertEqual([i.number for i in found], [40])
        self.assertEqual(prs, {41})


class SlugTest(unittest.TestCase):
    def slug(self, url, env=None):
        repo = TempRepo(self)
        repo.git("remote", "add", "origin", url)
        with mock.patch.dict(os.environ, env or {}):
            if env is None:
                os.environ.pop("GITHUB_REPOSITORY", None)
            return gh.slug_from_env(repo.path)

    def test_https(self):
        self.assertEqual(self.slug("https://github.com/o/r.git"), "o/r")
        self.assertEqual(self.slug("https://github.com/o/r"), "o/r")

    def test_ssh(self):
        self.assertEqual(self.slug("git@github.com:o/r.git"), "o/r")
        self.assertEqual(self.slug("git@github.com:o/r"), "o/r")

    def test_env_override(self):
        self.assertEqual(
            self.slug("https://github.com/o/r", {"GITHUB_REPOSITORY": "a/b"}),
            "a/b")

    def test_unparsable(self):
        with self.assertRaises(RuntimeError):
            self.slug("/some/local/path")


if __name__ == "__main__":
    unittest.main()
