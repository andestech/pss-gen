"""Tests for publish.py: tag, GitHub Release and issues of a release."""
import contextlib
import hashlib
import io
import os
import shutil
import subprocess
import tempfile
import unittest
from unittest import mock

import publish
import release
from gh import GitHubError
from tests.fake_gh import FakeGitHub
from tests.gitutil import TempRepo

WRITES = {"set_issue_state", "add_labels", "create_issue", "create_release",
          "update_release", "upload_asset", "publish_release", "delete_asset"}
ASSETS = ["SHA256SUMS", "antlr-4.9.3-complete.jar", "pssgen_v1.1.0.jar"]
MERGED_AT = "2029-12-31T23:00:00Z"
BEFORE_MERGE = "2029-12-01T00:00:00Z"
AFTER_MERGE = "2029-12-31T23:00:05Z"

PR_BODY = """<!--
- [ ] The version follows the major/minor rules
-->

## Summary

The next minor.

## Notable changes

None

## Features

- #4 Feature four

## Bug fixes

- #3 Crash on empty input

## Known issues / limitations

- #7 Limitation seven
- #9 Limitation nine

## Artifacts

Filled in by the release workflow.

Closes #8
"""


def write(path, text):
    with open(path, "w") as f:
        f.write(text)


def sha256(path):
    with open(path, "rb") as f:
        return hashlib.sha256(f.read()).hexdigest()


class PublishTest(unittest.TestCase):
    def setUp(self):
        self.remote = tempfile.mkdtemp()
        self.addCleanup(shutil.rmtree, self.remote, True)
        subprocess.run(["git", "init", "-q", "--bare", self.remote],
                       check=True)
        self.repo = r = TempRepo(self)
        for key, value in (("user.name", "t"), ("user.email", "t@example.com"),
                           ("tag.gpgSign", "false")):
            r.git("config", key, value)
        self.set_version("v1.0.0")
        r.git("add", "VERSION")
        r.commit("init")
        r.git("tag", "-a", "-m", "v1.0.0", "v1.0.0")
        r.git("checkout", "-q", "-b", "develop")
        r.commit("Fix #3: crash on empty input")
        r.commit("Fix #5: wrong order")
        r.commit('Revert "Fix #5: wrong order"')
        r.commit("Fix #6: typo")
        self.set_version("v1.1.0")
        r.git("add", "VERSION")
        r.commit("[RELEASE] v1.1.0")
        r.git("checkout", "-q", "main")
        r.git("merge", "-q", "--no-ff", "-m",
              "Merge pull request #10 from o/develop", "develop")
        self.sha = r.git("rev-parse", "HEAD")
        r.git("remote", "add", "origin", self.remote)
        r.git("push", "-q", "origin", "main", "refs/tags/v1.0.0")

        self.fake = FakeGitHub()
        self.fake.add_issue(3, "Crash", "Bug")
        self.fake.add_issue(4, "Feature four", "Feature", state="closed",
                            closed_at=AFTER_MERGE)
        self.fake.add_issue(5, "Wrong order", "Bug", state="closed",
                            closed_at=AFTER_MERGE)
        self.fake.add_issue(6, "Typo", "Bug", state="closed",
                            closed_at=BEFORE_MERGE)
        self.fake.add_issue(7, "Limitation seven", "Bug", state="closed",
                            closed_at=BEFORE_MERGE)
        self.fake.add_issue(8, "Closed by the PR body", "Bug", state="closed",
                            closed_at=AFTER_MERGE)
        self.fake.add_issue(9, "Limitation nine", "Bug")
        self.fake.commit_pulls[self.sha] = [
            self.pull(9, "0" * 40, "develop"),
            self.pull(10, self.sha, "develop"),
        ]

        self.dist = tempfile.mkdtemp()
        self.addCleanup(shutil.rmtree, self.dist, True)
        write(os.path.join(self.dist, "pssgen_v1.1.0.jar"), "pssgen jar")
        write(os.path.join(self.dist, "antlr-4.9.3-complete.jar"), "antlr")
        self.write_sums()

    def set_version(self, text):
        write(os.path.join(self.repo.path, "VERSION"), text + "\n")

    def pull(self, number, merge_sha, head_ref, head_repo="o/r"):
        return {"number": number, "merge_commit_sha": merge_sha,
                "merged_at": MERGED_AT, "body": PR_BODY,
                "head": {"ref": head_ref, "repo": {"full_name": head_repo}},
                "base": {"ref": "main", "repo": {"full_name": "o/r"}}}

    def write_sums(self):
        names = ["antlr-4.9.3-complete.jar", "pssgen_v1.1.0.jar"]
        write(os.path.join(self.dist, "SHA256SUMS"), "".join(
            "%s  %s\n" % (sha256(os.path.join(self.dist, n)), n)
            for n in names))

    def run_publish(self, dry_run=False):
        return publish.run(self.repo.path, self.fake, self.sha, self.dist,
                           dry_run)

    def tag_and_publish(self):
        publish.tag_local(self.repo.path, self.fake, self.sha)
        return self.run_publish()

    def remote_git(self, *args):
        r = subprocess.run(["git", "-C", self.remote, *args],
                           capture_output=True, text=True)
        return r.stdout.strip() if r.returncode == 0 else None

    def writes(self):
        return [c for c in self.fake.calls if c[0] in WRITES]

    def state_calls(self, n):
        return [c for c in self.fake.calls
                if c[0] == "set_issue_state" and c[1] == n]

    def test_publish_creates_tag_release_assets(self):
        actions = self.tag_and_publish()
        self.assertEqual(self.remote_git("rev-parse", "v1.1.0^{commit}"),
                         self.sha)
        self.assertIn("v1.1.0 (#10)",
                      self.remote_git("cat-file", "-p", "v1.1.0"))
        rel = self.fake.releases["v1.1.0"]
        self.assertEqual(rel["name"], "v1.1.0")
        self.assertFalse(rel["draft"])
        self.assertTrue(rel["latest"])
        self.assert_body_matches_dist(rel)
        self.assertTrue(rel["body"].startswith("## Summary\n"))
        self.assertNotIn("checklist", rel["body"])
        self.assertNotIn("Filled in by the release workflow", rel["body"])
        self.assertEqual(self.fake.assets[rel["id"]]["pssgen_v1.1.0.jar"],
                         b"pssgen jar")
        create = [c for c in self.fake.calls if c[0] == "create_release"]
        self.assertEqual([c[4:] for c in create], [(False, True)])
        self.assertEqual(actions[:5], [
            "push tag v1.1.0", "create draft release v1.1.0",
            "upload antlr-4.9.3-complete.jar", "upload pssgen_v1.1.0.jar",
            "upload SHA256SUMS"])
        self.assertEqual(actions[5], "publish release v1.1.0")
        last_upload = max(i for i, c in enumerate(self.fake.calls)
                          if c[0] == "upload_asset")
        published = [i for i, c in enumerate(self.fake.calls)
                     if c[0] == "publish_release"]
        self.assertEqual(len(published), 1)
        self.assertGreater(published[0], last_upload)

    def assert_body_matches_dist(self, rel):
        """Assets, the body's sums and SHA256SUMS all come from dist."""
        self.assertEqual(sorted(a["name"] for a in rel["assets"]), ASSETS)
        for name in ASSETS:
            path = os.path.join(self.dist, name)
            self.assertIn("- `%s`  %s" % (name, sha256(path)), rel["body"])
            with open(path, "rb") as f:
                self.assertEqual(self.fake.assets[rel["id"]][name], f.read())

    def test_closes_listed(self):
        actions = self.tag_and_publish()
        self.assertEqual(self.fake.issues[3]["state"], "closed")
        self.assertEqual(self.fake.issues[3]["state_reason"], "completed")
        self.assertIn("close #3", actions)
        self.assertEqual(self.state_calls(4), [])

    def test_reopens_autoclosed_unlisted(self):
        actions = self.tag_and_publish()
        for n in (5, 8):
            self.assertEqual(self.fake.issues[n]["state"], "open")
            self.assertIn("reopen #%d" % n, actions)
        self.assertEqual(self.fake.issues[6]["state"], "closed")
        self.assertEqual(self.state_calls(6), [])

    def test_known_issue_labelled(self):
        actions = self.tag_and_publish()
        for n in (7, 9):
            self.assertEqual(self.fake.issues[n]["state"], "open")
            self.assertIn({"name": "known-issue"},
                          self.fake.issues[n]["labels"])
            self.assertIn("label #%d known-issue" % n, actions)
        self.assertIn("reopen #7", actions)
        self.assertEqual(self.state_calls(9), [])

    def fail_second_upload(self):
        """First run: tag pushed, draft created, one asset uploaded."""
        upload = self.fake.upload_asset
        uploads = []

        def flaky(release, path):
            uploads.append(path)
            if len(uploads) == 2:
                raise GitHubError(502, "uploads", "Bad Gateway")
            upload(release, path)

        self.fake.upload_asset = flaky
        publish.tag_local(self.repo.path, self.fake, self.sha)
        with self.assertRaises(GitHubError):
            self.run_publish()
        self.fake.upload_asset = upload
        rel = self.fake.releases["v1.1.0"]
        self.assertTrue(rel["draft"])
        self.assertFalse(rel["latest"])
        self.assertEqual(len(rel["assets"]), 1)
        # A re-run of the workflow starts from a fresh checkout that fetched
        # the pushed tag.
        self.repo.git("tag", "-d", "v1.1.0")
        self.repo.git("fetch", "-q", "origin",
                      "refs/tags/v1.1.0:refs/tags/v1.1.0")
        publish.tag_local(self.repo.path, self.fake, self.sha)
        self.fake.calls.clear()
        return rel

    def assert_rerun_completes(self, rel):
        actions = self.run_publish()
        self.assertNotIn("push tag v1.1.0", actions)
        self.assertNotIn("create draft release v1.1.0", actions)
        self.assertEqual(
            [c for c in self.fake.calls if c[0] == "create_release"], [])
        self.assertEqual([a for a in actions if a.startswith("delete ")],
                         ["delete antlr-4.9.3-complete.jar"])
        self.assertEqual(len([a for a in actions if a.startswith("upload ")]),
                         3)
        self.assertIn("publish release v1.1.0", actions)
        self.assertFalse(rel["draft"])
        self.assertTrue(rel["latest"])
        self.assert_body_matches_dist(rel)
        self.assertEqual(len({a["id"] for a in rel["assets"]}), 3)
        self.assertEqual(self.remote_git("rev-parse", "v1.1.0^{commit}"),
                         self.sha)

    def test_publish_idempotent(self):
        rel = self.fail_second_upload()
        self.assert_rerun_completes(rel)
        self.fake.calls.clear()
        self.assertEqual(self.run_publish(), [])
        self.assertEqual(self.writes(), [])
        self.assertEqual(len(self.fake.releases), 1)

    def test_rerun_draft_rebuilt_jar(self):
        rel = self.fail_second_upload()
        first_sums = rel["body"]
        write(os.path.join(self.dist, "pssgen_v1.1.0.jar"), "rebuilt jar")
        self.write_sums()
        self.assert_rerun_completes(rel)
        self.assertNotEqual(rel["body"], first_sums)
        self.assertEqual(self.fake.assets[rel["id"]]["pssgen_v1.1.0.jar"],
                         b"rebuilt jar")

    def publish_then_reopen_3(self):
        """A published release whose issue step has not run yet."""
        self.tag_and_publish()
        self.fake.issues[3].update(state="open", closed_at=None)
        self.fake.calls.clear()
        rel = self.fake.releases["v1.1.0"]
        return rel, rel["body"], dict(self.fake.assets[rel["id"]])

    def release_writes(self):
        return [c for c in self.writes()
                if c[0] not in ("set_issue_state", "add_labels")]

    def test_published_release_not_modified(self):
        rel, body, files = self.publish_then_reopen_3()
        write(os.path.join(self.dist, "pssgen_v1.1.0.jar"), "rebuilt jar")
        self.write_sums()
        self.assertEqual(self.run_publish(dry_run=True), ["close #3"])
        self.assertEqual(self.writes(), [])
        self.assertEqual(self.run_publish(), ["close #3"])
        self.assertEqual(self.release_writes(), [])
        self.assertEqual(self.fake.issues[3]["state"], "closed")
        self.assertEqual(rel["body"], body)
        self.assertEqual(self.fake.assets[rel["id"]], files)
        self.assertFalse(rel["draft"])

    def assert_published_refused(self, *fragments):
        for dry_run in (True, False):
            with self.assertRaises(publish.PublishError) as cm:
                self.run_publish(dry_run=dry_run)
            msg = str(cm.exception)
            for fragment in ("published", "v1.1.0") + fragments:
                self.assertIn(fragment, msg)
        self.assertEqual(self.writes(), [])
        self.assertEqual(self.fake.issues[3]["state"], "open")

    def test_published_body_asset_mismatch_errors(self):
        rel, body, _ = self.publish_then_reopen_3()
        digest = sha256(os.path.join(self.dist, "pssgen_v1.1.0.jar"))
        rel["body"] = body.replace(digest, "0" * 64)
        self.assert_published_refused(
            "checksum of pssgen_v1.1.0.jar in the body")

        rel["body"] = "\n".join(line for line in body.split("\n")
                                if "antlr" not in line)
        self.assert_published_refused(
            "lists no checksum of antlr-4.9.3-complete.jar")

        rel["body"] = body
        rel["assets"] = [a for a in rel["assets"]
                         if a["name"] != "SHA256SUMS"]
        rel["assets"][0]["state"] = "starter"
        self.assert_published_refused("SHA256SUMS is missing",
                                      "not fully uploaded")

    def test_warnings_in_step_summary(self):
        summary = os.path.join(self.dist, "..", "summary%d.md" % id(self))
        self.addCleanup(lambda: os.path.exists(summary) and os.remove(summary))
        self.fake.commit_pulls[self.sha][1]["body"] = PR_BODY.replace(
            "- #7 Limitation seven", "- #3 Still slow\n- #77 Unknown")
        with mock.patch.dict(os.environ, {"GITHUB_STEP_SUMMARY": summary}), \
                contextlib.redirect_stderr(io.StringIO()) as err:
            self.run_publish(dry_run=True)
        with open(summary) as f:
            text = f.read()
        for fragment in ("#3", "both as fixed and as known", "#77"):
            self.assertIn(fragment, text)
            self.assertIn(fragment, err.getvalue())

    def tag_other(self):
        self.repo.git("checkout", "-q", "-b", "side", "develop")
        other = self.repo.commit("other")
        self.repo.git("tag", "-a", "-m", "v1.1.0", "v1.1.0")
        self.repo.git("checkout", "-q", "main")
        return other

    def assert_all_refused(self):
        for call in (lambda: publish.tag_local(self.repo.path, self.fake,
                                               self.sha),
                     self.run_publish,
                     lambda: self.run_publish(dry_run=True)):
            with self.assertRaises(publish.PublishError) as cm:
                call()
            self.assertIn("v1.1.0", str(cm.exception))
        self.assertEqual(self.writes(), [])
        self.assertEqual(self.fake.releases, {})

    def test_tag_elsewhere_errors(self):
        other = self.tag_other()
        self.repo.git("push", "-q", "origin", "refs/tags/v1.1.0")
        self.repo.git("tag", "-d", "v1.1.0")
        self.assert_all_refused()
        self.assertEqual(self.remote_git("rev-parse", "v1.1.0^{commit}"),
                         other)

    def test_local_tag_elsewhere_errors(self):
        self.tag_other()
        self.assert_all_refused()
        self.assertIsNone(self.remote_git("rev-parse", "-q", "--verify",
                                          "refs/tags/v1.1.0"))

    def test_dry_run_changes_nothing(self):
        before = self.repo.git("tag", "--list")
        actions = self.run_publish(dry_run=True)
        self.assertEqual(actions, [
            "push tag v1.1.0", "create draft release v1.1.0",
            "upload antlr-4.9.3-complete.jar", "upload pssgen_v1.1.0.jar",
            "upload SHA256SUMS", "publish release v1.1.0", "close #3",
            "reopen #5", "reopen #8",
            "reopen #7", "label #7 known-issue", "label #9 known-issue"])
        self.assertEqual(self.writes(), [])
        self.assertEqual(self.fake.releases, {})
        self.assertEqual(self.repo.git("tag", "--list"), before)
        self.assertIsNone(self.remote_git("rev-parse", "-q", "--verify",
                                          "refs/tags/v1.1.0"))

    def test_tag_message_names_pr(self):
        self.assertEqual(publish.tag_local(self.repo.path, self.fake,
                                           self.sha), "create tag v1.1.0")
        self.assertIn("v1.1.0 (#10)",
                      self.repo.git("cat-file", "-p", "v1.1.0"))
        self.assertIsNone(publish.tag_local(self.repo.path, self.fake,
                                            self.sha))

    def refused(self, fragment, dry_run=False):
        with self.assertRaises(publish.PublishError) as cm:
            self.run_publish(dry_run=dry_run)
        self.assertIn(fragment, str(cm.exception))
        self.assertEqual(self.writes(), [])
        self.assertIsNone(self.remote_git("rev-parse", "-q", "--verify",
                                          "refs/tags/v1.1.0"))

    def test_no_release_pr_errors(self):
        self.fake.commit_pulls[self.sha] = [self.pull(9, "0" * 40, "develop")]
        self.refused("no pull request")

    def test_fork_pr_errors(self):
        self.fake.commit_pulls[self.sha] = [
            self.pull(10, self.sha, "develop", head_repo="x/r")]
        self.refused("x/r")

    def test_bad_successor_errors(self):
        self.fake.commit_pulls[self.sha] = [self.pull(10, self.sha, "hot_fix")]
        self.refused("not a valid successor")

    def test_missing_local_tag_errors(self):
        self.refused("tag v1.1.0")

    def test_sums_mismatch_errors(self):
        write(os.path.join(self.dist, "pssgen_v1.1.0.jar"), "rebuilt")
        publish.tag_local(self.repo.path, self.fake, self.sha)
        self.refused("SHA256SUMS")

    def cli(self, *argv):
        old = os.getcwd()
        os.chdir(self.repo.path)
        self.addCleanup(os.chdir, old)
        out = io.StringIO()
        with mock.patch("release.gh.GitHub", lambda slug, token: self.fake), \
                mock.patch("release.gh.slug_from_env", lambda d: "o/r"), \
                contextlib.redirect_stdout(out):
            rc = release.main(list(argv))
        return rc, out.getvalue()

    def test_cli_tag_and_dry_run(self):
        rc, out = self.cli("tag")
        self.assertEqual((rc, out), (0, "create tag v1.1.0\n"))
        rc, out = self.cli("publish", "--dist", self.dist, "--dry-run")
        self.assertEqual(rc, 0)
        self.assertIn("would push tag v1.1.0\n", out)
        self.assertEqual(self.writes(), [])

    def test_cli_error_exits(self):
        self.fake.commit_pulls[self.sha] = []
        with self.assertRaises(SystemExit) as cm:
            self.cli("publish", "--sha", self.sha, "--dist", self.dist)
        self.assertIn("no pull request", str(cm.exception))


if __name__ == "__main__":
    unittest.main()
