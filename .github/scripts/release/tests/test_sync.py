"""Tests for sync.py: bring develop and hot_fix up to a new release tag."""
import contextlib
import io
import os
import shlex
import shutil
import subprocess
import tempfile
import unittest
from unittest import mock

import release
import sync
from tests.fake_gh import FakeGitHub
from tests.gitutil import TempRepo

HERE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
TITLE = "Sync v1.1.0 into hot_fix"


def write(path, text):
    with open(path, "w") as f:
        f.write(text)


class SyncTest(unittest.TestCase):
    """origin has main, develop and hot_fix at v1.0.0."""

    def setUp(self):
        self.remote = tempfile.mkdtemp()
        self.addCleanup(shutil.rmtree, self.remote, True)
        subprocess.run(["git", "init", "-q", "--bare", self.remote],
                       check=True)
        self.repo = r = TempRepo(self)
        for key, value in (("user.name", "t"), ("user.email", "t@example.com"),
                           ("tag.gpgSign", "false"),
                           ("commit.gpgSign", "false")):
            r.git("config", key, value)
        self.set_version("v1.0.0")
        r.commit("init")
        r.git("tag", "-a", "v1.0.0", "-m", "v1.0.0")
        r.git("remote", "add", "origin", self.remote)
        r.git("branch", "develop")
        r.git("branch", "hot_fix")
        r.git("push", "-q", "origin", "main", "develop", "hot_fix",
              "refs/tags/v1.0.0")
        self.fake = FakeGitHub()

    def set_version(self, v):
        write(os.path.join(self.repo.path, "VERSION"), v + "\n")
        self.repo.git("add", "VERSION")

    def remote_tip(self, branch):
        return subprocess.run(
            ["git", "-C", self.remote, "rev-parse", "refs/heads/" + branch],
            check=True, capture_output=True, text=True).stdout.strip()

    def on_branch(self, branch, subject, files):
        """Commit files on branch and push it."""
        r = self.repo
        r.git("switch", "-q", branch)
        for name, text in files.items():
            write(os.path.join(r.path, name), text)
            r.git("add", name)
        sha = r.commit(subject)
        r.git("push", "-q", "origin", branch)
        r.git("switch", "-q", "main")
        return sha

    def release_develop(self, extra=None):
        """Release v1.1.0 from develop: a merge commit on main, tagged."""
        r = self.repo
        files = {"VERSION": "v1.1.0\n", "feature.txt": "f\n"}
        files.update(extra or {})
        self.on_branch("develop", "[RELEASE] v1.1.0", files)
        r.git("merge", "-q", "--no-ff", "-m", "Merge develop", "develop")
        r.git("tag", "-a", "v1.1.0", "-m", "v1.1.0 (#1)")
        r.git("push", "-q", "origin", "main", "refs/tags/v1.1.0")
        return r.git("rev-parse", "HEAD")

    def sync(self, branch, test_cmd=None, tag="v1.1.0"):
        with contextlib.redirect_stdout(io.StringIO()):
            return sync.run(self.repo.path, self.fake, branch, tag, test_cmd)

    def reject_pushes(self, message):
        """Make origin refuse every push with message on stderr."""
        hook = os.path.join(self.remote, "hooks", "pre-receive")
        write(hook, "#!/bin/sh\necho %s >&2\nexit 1\n" % shlex.quote(message))
        os.chmod(hook, 0o755)

    def issues_created(self):
        return [c for c in self.fake.calls if c[0] == "create_issue"]

    def assert_clean_checkout(self):
        r = self.repo
        self.assertEqual(r.git("rev-parse", "--abbrev-ref", "HEAD"), "main")
        self.assertEqual(r.git("status", "--porcelain"), "")
        self.assertEqual(len(r.git("worktree", "list").splitlines()), 1)

    def test_fast_forward(self):
        tag_sha = self.release_develop()
        self.assertEqual(self.sync("develop"), "fast-forward")
        self.assertEqual(self.remote_tip("develop"), tag_sha)
        self.assertEqual(self.sync("hot_fix"), "fast-forward")
        self.assertEqual(self.remote_tip("hot_fix"), tag_sha)
        self.assertEqual(self.issues_created(), [])
        self.assert_clean_checkout()

    def test_up_to_date(self):
        tag_sha = self.release_develop()
        self.sync("develop")
        self.assertEqual(self.sync("develop", ["false"]), "up-to-date")
        self.assertEqual(self.remote_tip("develop"), tag_sha)
        self.assertEqual(self.issues_created(), [])

    def test_clean_merge_pushed(self):
        fix = self.on_branch("hot_fix", "Fix a crash", {"fix.txt": "x\n"})
        tag_sha = self.release_develop()
        # The test command runs on the merge result.
        check = ["bash", "-c", 'test -f fix.txt && test -f feature.txt && '
                 'test "$(cat VERSION)" = v1.1.0']
        self.assertEqual(self.sync("hot_fix", check), "merged")
        tip = self.remote_tip("hot_fix")
        r = self.repo
        self.assertEqual(r.git("log", "-1", "--format=%s", tip),
                         "Merge v1.1.0 into hot_fix")
        self.assertEqual(r.git("log", "-1", "--format=%P", tip).split(),
                         [fix, tag_sha])
        self.assertEqual(self.issues_created(), [])
        self.assert_clean_checkout()
        self.assertEqual(self.sync("hot_fix", ["false"]), "up-to-date")

    def test_conflict_opens_issue(self):
        before = self.on_branch("hot_fix", "[RELEASE] v1.0.1",
                                {"VERSION": "v1.0.1\n", "two words": "a\n"})
        self.release_develop({"two words": "b\n"})
        ran = os.path.join(self.remote, "ran")
        self.assertEqual(self.sync("hot_fix", ["touch", ran]),
                         "needs-manual")
        self.assertFalse(os.path.exists(ran))
        self.assertEqual(self.remote_tip("hot_fix"), before)
        [(_, title, body)] = self.issues_created()
        self.assertEqual(title, TITLE)
        self.assertIn("- `VERSION`\n", body)
        self.assertIn("- `two words`\n", body)
        for cmd in ("git fetch origin", "git switch hot_fix",
                    "git merge --no-ff v1.1.0", "make -C src && make -C tests",
                    "git push origin hot_fix"):
            self.assertIn(cmd, body)
        self.assert_clean_checkout()
        # A re-run finds the open issue and opens no second one.
        self.assertEqual(self.sync("hot_fix"), "needs-manual")
        self.assertEqual(len(self.issues_created()), 1)

    def test_failing_tests_not_pushed(self):
        before = self.on_branch("hot_fix", "Fix a crash", {"fix.txt": "x\n"})
        self.release_develop()
        # A closed issue of the same title does not stop a new one.
        self.fake.add_issue(40, title=TITLE, state="closed")
        failing = ["bash", "-c", "touch build.out; exit 3"]
        self.assertEqual(self.sync("hot_fix", failing), "needs-manual")
        self.assertEqual(self.remote_tip("hot_fix"), before)
        [(_, title, body)] = self.issues_created()
        self.assertEqual(title, TITLE)
        self.assertIn("exit 3", body)
        self.assertIn("git merge --no-ff v1.1.0", body)
        self.assert_clean_checkout()

    def test_rejected_push_manual(self):
        self.on_branch("hot_fix", "Fix a crash", {"fix.txt": "x\n"})
        self.release_develop()
        # A second clone pushes to hot_fix while the merge is being tested.
        other = tempfile.mkdtemp()
        self.addCleanup(shutil.rmtree, other, True)
        subprocess.run(["git", "clone", "-q", "-b", "hot_fix", self.remote,
                        other], check=True)
        subprocess.run(["git", "-C", other, "-c", "user.name=t", "-c",
                        "user.email=t@example.com", "-c",
                        "commit.gpgSign=false", "commit", "-q",
                        "--allow-empty", "-m", "Another fix"], check=True)
        moved = subprocess.run(["git", "-C", other, "rev-parse", "HEAD"],
                               check=True, capture_output=True,
                               text=True).stdout.strip()
        push = ["git", "-C", other, "push", "-q", "origin", "hot_fix"]
        self.assertEqual(self.sync("hot_fix", push), "needs-manual")
        self.assertEqual(self.remote_tip("hot_fix"), moved)
        [(_, title, body)] = self.issues_created()
        self.assertEqual(title, TITLE)
        self.assertIn("Origin rejected the push", body)
        self.assertIn("git merge --no-ff v1.1.0", body)
        self.assert_clean_checkout()

    def test_rejected_ff_issue_uses_ff_only(self):
        before = self.remote_tip("hot_fix")
        self.release_develop()
        self.reject_pushes("denied by a server hook")
        self.assertEqual(self.sync("hot_fix", ["false"]), "needs-manual")
        self.assertEqual(self.remote_tip("hot_fix"), before)
        [(_, title, body)] = self.issues_created()
        self.assertEqual(title, TITLE)
        self.assertIn("Origin rejected the push", body)
        self.assertIn("denied by a server hook", body)
        self.assertIn("git merge --ff-only v1.1.0", body)
        self.assertNotIn("git merge --no-ff v1.1.0\n", body)
        self.assertNotIn(".github/workflows/", body)

    def test_rejected_push_workflows_note(self):
        self.release_develop()
        self.reject_pushes("refusing to allow a GitHub App to create or "
                           "update workflow `.github/workflows/ci.yml` "
                           "without `workflows` permission")
        self.assertEqual(self.sync("hot_fix", ["false"]), "needs-manual")
        [(_, _, body)] = self.issues_created()
        self.assertIn(sync.WORKFLOWS_NOTE, body)

    def test_workflow_change_needs_manual(self):
        before = self.on_branch("hot_fix", "Fix a crash", {"fix.txt": "x\n"})
        os.makedirs(os.path.join(self.repo.path, ".github", "workflows"))
        self.release_develop({".github/workflows/ci.yml": "on: push\n"})
        ran = os.path.join(self.remote, "ran")
        self.assertEqual(self.sync("hot_fix", ["touch", ran]),
                         "needs-manual")
        self.assertFalse(os.path.exists(ran))
        self.assertEqual(self.remote_tip("hot_fix"), before)
        [(_, title, body)] = self.issues_created()
        self.assertEqual(title, TITLE)
        self.assertIn("`.github/workflows/ci.yml`", body)
        self.assertIn(sync.WORKFLOWS_NOTE, body)
        self.assertIn("git merge --no-ff v1.1.0", body)
        self.assert_clean_checkout()

    def test_merge_without_test_cmd_not_pushed(self):
        before = self.on_branch("hot_fix", "Fix a crash", {"fix.txt": "x\n"})
        self.release_develop()
        self.assertEqual(self.sync("hot_fix", None), "needs-manual")
        self.assertEqual(self.remote_tip("hot_fix"), before)
        [(_, title, body)] = self.issues_created()
        self.assertEqual(title, TITLE)
        self.assertIn("no test command", body)
        self.assert_clean_checkout()

    def test_no_force_in_source(self):
        for name in ("sync.py", "publish.py"):
            with open(os.path.join(HERE, name)) as f:
                text = f.read()
            for bad in ("--force", "force-with-lease", "+refs/"):
                self.assertNotIn(bad, text, "%s names %s" % (name, bad))

    def cli(self, *argv):
        old = os.getcwd()
        os.chdir(self.repo.path)
        self.addCleanup(os.chdir, old)
        out = io.StringIO()
        with mock.patch("release.gh.GitHub", lambda slug, token: self.fake), \
                mock.patch("release.gh.slug_from_env", lambda d: "o/r"), \
                contextlib.redirect_stdout(out):
            code = release.main(list(argv))
        return code, out.getvalue()

    def test_cli_tag_from_version(self):
        tag_sha = self.release_develop()
        code, out = self.cli("sync", "develop", "--test-cmd", "false")
        self.assertEqual(code, 0)
        self.assertIn("fast-forward", out)
        self.assertEqual(self.remote_tip("develop"), tag_sha)

    def test_cli_needs_manual_warns(self):
        self.on_branch("hot_fix", "Fix a crash", {"fix.txt": "x\n"})
        self.release_develop()
        summary = os.path.join(self.remote, "summary.md")
        with mock.patch.dict(os.environ, {"GITHUB_STEP_SUMMARY": summary}):
            code, out = self.cli("sync", "hot_fix", "--test-cmd",
                                 "true && false")
        self.assertEqual(code, 0)
        self.assertIn("::warning::", out)
        self.assertIn(TITLE, out)
        with open(summary) as f:
            self.assertIn(TITLE, f.read())
        [(_, title, _)] = self.issues_created()
        self.assertEqual(title, TITLE)


if __name__ == "__main__":
    unittest.main()
