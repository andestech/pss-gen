"""Tests for the command line of release.py."""
import contextlib
import io
import json
import os
import unittest
from unittest import mock

import release
from tests.fake_gh import FakeGitHub
from tests.gitutil import TempRepo


class CliTest(unittest.TestCase):
    def setUp(self):
        self.repo = TempRepo(self)
        with open(os.path.join(self.repo.path, "VERSION"), "w") as f:
            f.write("v1.0.0\n")
        self.repo.git("add", "VERSION")
        self.repo.commit("init")
        self.repo.git("tag", "-a", "-m", "v1.0.0", "v1.0.0")
        self.fake = FakeGitHub()
        old = os.getcwd()
        os.chdir(self.repo.path)
        self.addCleanup(os.chdir, old)
        for target, value in (
                ("release.gh.GitHub", lambda slug, token: self.fake),
                ("release.gh.slug_from_env", lambda d: "o/r")):
            p = mock.patch(target, value)
            p.start()
            self.addCleanup(p.stop)

    def prepare(self, kind):
        err = io.StringIO()
        with contextlib.redirect_stdout(io.StringIO()), \
                contextlib.redirect_stderr(err):
            return release.main(["prepare", kind])

    def refused(self, kind, fragment):
        with self.assertRaises(SystemExit) as cm:
            self.prepare(kind)
        self.assertIn(fragment, str(cm.exception))

    def test_wrong_branch(self):
        self.refused("minor", "not on main")

    def test_dirty_tree(self):
        self.repo.git("checkout", "-q", "-b", "develop")
        with open(os.path.join(self.repo.path, "x"), "w") as f:
            f.write("x")
        self.refused("minor", "not clean")

    def test_patch_on_develop(self):
        self.repo.git("checkout", "-q", "-b", "develop")
        self.refused("patch", "hot_fix")

    def test_minor_on_hot_fix(self):
        self.repo.git("checkout", "-q", "-b", "hot_fix")
        self.refused("minor", "develop")

    def test_unsynced(self):
        self.repo.git("checkout", "-q", "-b", "develop")
        self.repo.git("checkout", "-q", "main")
        self.repo.commit("on main")
        self.repo.git("tag", "-a", "-m", "v1.0.1", "v1.0.1")
        self.repo.git("update-ref", "refs/remotes/origin/main", "main")
        self.repo.git("checkout", "-q", "develop")
        self.refused("minor", "v1.0.1")

    def test_prepare_writes_version(self):
        self.repo.git("checkout", "-q", "-b", "develop")
        self.assertEqual(self.prepare("minor"), 0)
        with open(os.path.join(self.repo.path, "VERSION")) as f:
            self.assertEqual(f.read(), "v1.1.0\n")

    def check(self, event):
        path = os.path.join(self.repo.path, "..", "event%d.json" % id(self))
        with open(path, "w") as f:
            json.dump({"pull_request": event}, f)
        self.addCleanup(os.remove, path)
        out = io.StringIO()
        with contextlib.redirect_stdout(out):
            rc = release.main(["check", "--pr-event", path])
        return rc, out.getvalue()

    def event(self, head_repo):
        return {"body": "", "head": {"ref": "develop", "sha": "a",
                                     "repo": head_repo},
                "base": {"sha": "b", "repo": {"full_name": "o/r"}}}

    def test_event_null_head_repo(self):
        rc, out = self.check(self.event(None))
        self.assertEqual(rc, 1)
        self.assertIn("FAILED", out)

    def test_event_fork(self):
        rc, out = self.check(self.event({"full_name": "x/r"}))
        self.assertEqual(rc, 1)
        self.assertIn("x/r", out)


if __name__ == "__main__":
    unittest.main()
