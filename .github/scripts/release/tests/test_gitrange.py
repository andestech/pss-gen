"""Tests for gitrange.py."""
import unittest

import gitrange
from gitrange import Commit
from tests.gitutil import TempRepo


def c(subject, body="", merge=False, sha="0"):
    return Commit(sha, subject, body, merge)


class RefsTest(unittest.TestCase):
    def test_refs_forms(self):
        self.assertEqual(gitrange.refs(c("[ISSUE] Fix #45: x")), {45})
        self.assertEqual(gitrange.refs(c("[X] y (#48)")), {48})
        self.assertEqual(gitrange.refs(c("s", "Refs #3, #4")), {3, 4})

    def test_closing_refs(self):
        self.assertEqual(gitrange.closing_refs("Fixes #1"), {1})
        self.assertEqual(gitrange.closing_refs("fix: #2"), {2})
        self.assertEqual(gitrange.closing_refs("Resolved #3"), {3})
        self.assertEqual(gitrange.closing_refs("done (#4)"), set())
        self.assertEqual(gitrange.closing_refs("prefix#5"), set())
        self.assertEqual(gitrange.closing_refs("prefixfix #6"), set())


class FixedIssuesTest(unittest.TestCase):
    def test_revert_cancels_fix(self):
        fixed, reverted = gitrange.fixed_issues([
            c("[ISSUE] Fix #49"), c('Revert "[ISSUE] Fix #49"')])
        self.assertNotIn(49, fixed)
        self.assertIn(49, reverted)

    def test_refix_after_revert(self):
        fixed, reverted = gitrange.fixed_issues([
            c("[ISSUE] Fix #49"), c('Revert "[ISSUE] Fix #49"'),
            c("[ISSUE] Fix #49 again")])
        self.assertIn(49, fixed)
        self.assertNotIn(49, reverted)

    def test_body_mention_not_fix(self):
        fixed, reverted = gitrange.fixed_issues([
            c("[ISSUE] Fix #49"), c('Revert "[ISSUE] Fix #49"'),
            c("[DOC] Note the limits", "Known issue (#49)")])
        self.assertNotIn(49, fixed)
        self.assertIn(49, reverted)

    def test_merge_skipped(self):
        fixed, reverted = gitrange.fixed_issues([
            c("Merge branch x (#7)", merge=True)])
        self.assertEqual((fixed, reverted), (set(), set()))


class UnreferencedTest(unittest.TestCase):
    def test_unreferenced(self):
        plain = c("[BUILD] no ref")
        got = gitrange.unreferenced([
            plain, c("[BUILD] has #3"), c("[RELEASE] v2.4.2"),
            c("Merge x", merge=True)])
        self.assertEqual(got, [plain])

    def test_body_only_ref_unreferenced(self):
        found = gitrange.unreferenced([c("[X] no ref", "mentions #3")])
        self.assertEqual(len(found), 1)

    def test_is_release_commit(self):
        self.assertTrue(gitrange.is_release_commit(c("[RELEASE] v2.4.2")))
        self.assertFalse(gitrange.is_release_commit(c("[RELEASE] v2.4")))
        self.assertFalse(gitrange.is_release_commit(c("x [RELEASE] v2.4.2")))


class CommitsRangeTest(unittest.TestCase):
    def test_commits_range(self):
        r = TempRepo(self)
        r.commit("old")
        r.git("tag", "v1.0.0")
        r.commit("first", "Refs #3")
        r.commit("second")
        got = gitrange.commits(r.path, "v1.0.0..HEAD")
        self.assertEqual([x.subject for x in got], ["first", "second"])
        self.assertEqual(got[0].body.strip(), "Refs #3")
        self.assertFalse(got[0].is_merge)

    def test_merge_flag(self):
        r = TempRepo(self)
        r.commit("base")
        r.git("checkout", "-q", "-b", "side")
        r.commit("side")
        r.git("checkout", "-q", "main")
        r.commit("main")
        r.git("merge", "-q", "--no-ff", "-m", "Merge side", "side")
        got = gitrange.commits(r.path, "HEAD")
        self.assertEqual([x.is_merge for x in got].count(True), 1)
        self.assertTrue(got[-1].is_merge)


if __name__ == "__main__":
    unittest.main()
