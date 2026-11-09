"""Tests for check.py: the gate of a release PR into main."""
import os
import unittest

import check
import notes
from tests.fake_gh import FakeGitHub
from tests.gitutil import TempRepo


def write_version(repo, text):
    with open(os.path.join(repo.path, "VERSION"), "w") as f:
        f.write(text + "\n")
    repo.git("add", "VERSION")


class CheckTest(unittest.TestCase):
    def setUp(self):
        self.repo = TempRepo(self)
        write_version(self.repo, "v1.0.0")
        self.repo.commit("init")
        self.repo.git("tag", "-a", "-m", "v1.0.0", "v1.0.0")
        self.gh = FakeGitHub()

    def release(self, branch, version, *commits):
        """Commit on `branch`, then the VERSION commit; return the PR dict."""
        base = self.repo.git("rev-parse", "main")
        self.repo.git("checkout", "-q", "-b", branch)
        for c in commits:
            self.repo.commit(*c) if isinstance(c, tuple) else self.repo.commit(c)
        write_version(self.repo, version)
        self.repo.commit("[RELEASE] " + version)
        return self.pr(branch, base)

    def pr(self, branch, base, body=None, head_repo="o/r"):
        if body is None:
            body = self.good_body()
        return {"head_ref": branch, "head_repo": head_repo, "base_repo": "o/r",
                "head_sha": self.repo.git("rev-parse", "HEAD"),
                "base_sha": base, "body": body}

    def good_body(self):
        return notes.draft([], [], []).replace("<!-- write -->", "text")

    def run_check(self, pr):
        return check.run(self.repo.path, self.gh, pr)

    def test_fork_develop_rejected(self):
        pr = self.release("develop", "v1.1.0")
        pr["head_repo"] = "someone/r"
        self.assertTrue(any("fork" in e or "repository" in e
                            for e in self.run_check(pr).errors))

    def test_other_head_rejected(self):
        pr = self.release("feature-x", "v1.1.0")
        self.assertTrue(any("develop" in e for e in self.run_check(pr).errors))

    def test_hotfix_feature_rejected(self):
        self.gh.add_issue(5, "new", type_name="Feature")
        pr = self.release("hot_fix", "v1.0.1", "Add thing (#5)")
        errs = self.run_check(pr).errors
        self.assertTrue(any("#5" in e for e in errs), errs)

    def test_hotfix_unreferenced_rejected(self):
        pr = self.release("hot_fix", "v1.0.1", "no issue here")
        errs = self.run_check(pr).errors
        self.assertTrue(any("without an issue" in e for e in errs), errs)

    def test_hotfix_unclassified_rejected(self):
        self.gh.add_issue(6, "q", labels=["question"])
        pr = self.release("hot_fix", "v1.0.1", "Fix #6: thing")
        errs = self.run_check(pr).errors
        self.assertTrue(any("#6" in e for e in errs), errs)

    def test_hotfix_missing_ref_rejected(self):
        pr = self.release("hot_fix", "v1.0.1", "Fix #99: thing")
        errs = self.run_check(pr).errors
        self.assertTrue(any("#99" in e for e in errs), errs)

    def merge_pr(self, number, branch):
        self.repo.git("checkout", "-q", "-b", branch)
        self.repo.commit("work on %s" % branch)
        self.repo.git("checkout", "-q", "-")
        self.repo.git("merge", "-q", "--no-ff", "-m",
                      "Merge pull request #%d from a/%s" % (number, branch),
                      branch)

    def test_pr_merge_commit_resolved(self):
        self.gh.add_issue(9, "bug", type_name="Bug")
        self.gh.add_pull(12, body="Fixes #9")
        base = self.repo.git("rev-parse", "main")
        self.repo.git("checkout", "-q", "-b", "develop")
        self.merge_pr(12, "contrib")
        write_version(self.repo, "v1.1.0")
        self.repo.commit("[RELEASE] v1.1.0")
        rng = check.collect(self.repo.path, self.gh, "v1.0.0..HEAD")
        self.assertEqual([i.number for i in rng.bugs], [9])
        self.assertIn("#9 bug", notes.parse(
            check.run(self.repo.path, self.gh, self.pr("develop", base)).draft
        )["Bug fixes"])

    def test_hotfix_pr_without_issue_rejected(self):
        self.gh.add_pull(13, body="no closing ref")
        base = self.repo.git("rev-parse", "main")
        self.repo.git("checkout", "-q", "-b", "hot_fix")
        self.repo.commit("Tidy (#13)")
        write_version(self.repo, "v1.0.1")
        self.repo.commit("[RELEASE] v1.0.1")
        errs = self.run_check(self.pr("hot_fix", base)).errors
        self.assertTrue(any("closes no issue" in e for e in errs), errs)

    def test_develop_unclassified_warns(self):
        self.gh.add_issue(6, "q", labels=["question"])
        pr = self.release("develop", "v1.1.0", "Fix #6: thing")
        rep = self.run_check(pr)
        self.assertEqual(rep.errors, [])
        self.assertTrue(any("#6" in w for w in rep.warnings))

    def test_bad_successor(self):
        pr = self.release("develop", "v1.0.1")
        self.assertTrue(any("successor" in e for e in self.run_check(pr).errors))

    def test_head_not_synced_rejected(self):
        pr = self.release("develop", "v1.1.0")
        self.repo.git("checkout", "-q", "main")
        self.repo.commit("on main")
        self.repo.git("tag", "-a", "-m", "v1.0.1", "v1.0.1")
        pr["base_sha"] = self.repo.git("rev-parse", "main")
        errs = self.run_check(pr).errors
        self.assertTrue(any("v1.0.1" in e and "contain" in e for e in errs),
                        errs)

    def test_incompatible_in_draft(self):
        self.gh.add_issue(7, "break", type_name="Feature",
                          labels=["incompatible"])
        pr = self.release("develop", "v1.1.0", "Add break (#7)")
        rep = self.run_check(pr)
        self.assertIn("#7 break", notes.parse(rep.draft)["Notable changes"])
        self.assertIn("#7 break", notes.parse(rep.draft)["Features"])

    def test_autoclose_unlisted_flagged(self):
        pr = self.release("develop", "v1.1.0", "Fix #49: x",
                          'Revert "Fix #49: x"')
        rep = self.run_check(pr)
        self.assertEqual(rep.autoclose, [(49, False)])
        self.assertTrue(any("#49" in w for w in rep.warnings))

    def test_valid_develop_release_passes(self):
        self.gh.add_issue(8, "feat", type_name="Feature")
        self.gh.add_issue(9, "bug", type_name="Bug")
        pr = self.release("develop", "v1.1.0", "Add feat (#8)", "Fix #9: bug")
        pr["body"] = (notes.draft(
            [check.Issue(8, "feat", "feature", "open", [])],
            [check.Issue(9, "bug", "bug", "open", [])], [])
            .replace("<!-- write -->", "text"))
        rep = self.run_check(pr)
        self.assertEqual(rep.errors, [])
        self.assertEqual(rep.autoclose, [(9, True)])
        self.assertIn("## Features", check.render(rep))

    def test_autoclose_from_pr_body(self):
        pr = self.release("develop", "v1.1.0")
        pr["body"] = self.good_body().replace("text", "Fixes #12", 1)
        rep = self.run_check(pr)
        self.assertEqual(rep.autoclose, [(12, False)])
        self.assertTrue(any("closing keyword" in e for e in rep.errors))

    def test_bad_note_rejected(self):
        pr = self.release("develop", "v1.1.0")
        pr["body"] = "nothing"
        self.assertTrue(any("release note" in e
                            for e in self.run_check(pr).errors))


if __name__ == "__main__":
    unittest.main()
