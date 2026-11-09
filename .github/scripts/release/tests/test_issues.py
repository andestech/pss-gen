"""Tests for issues.py and the slug detection of gh.py."""
import os
import shutil
import tempfile
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
        found, prs, missing = issues.resolve(fake, {1})
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
        found, _, _ = issues.resolve(fake, {1})
        self.assertEqual(found[0].kind, "unclassified")

    def test_fields(self):
        fake = FakeGitHub()
        fake.add_issue(5, title="T", labels=["bug", "x"], state="closed")
        found, _, _ = issues.resolve(fake, {5})
        self.assertEqual(found[0], issues.Issue(5, "T", "bug", "closed",
                                                ["bug", "x"]))

    def test_pr_ref_uses_closing_refs(self):
        fake = FakeGitHub()
        fake.add_issue(40, title="bug", type_name="Bug")
        fake.add_pull(41, body="Fixes #40")
        found, prs, missing = issues.resolve(fake, {41})
        self.assertEqual([i.number for i in found], [40])
        self.assertEqual(prs, {41})
        self.assertEqual(missing, set())

    def test_missing_ref_reported(self):
        fake = FakeGitHub()
        fake.add_pull(41, body="Fixes #40")
        found, prs, missing = issues.resolve(fake, {41, 99})
        self.assertEqual(found, [])
        self.assertEqual(prs, {41})
        self.assertEqual(missing, {40, 99})

    def test_other_errors_raise(self):
        class Boom(FakeGitHub):
            def issue(self, n):
                raise gh.GitHubError(500, "u", "x")
        with self.assertRaises(gh.GitHubError):
            issues.resolve(Boom(), {1})

    def test_each_payload_fetched_once(self):
        fake = FakeGitHub()
        fake.add_issue(40, title="b")
        fake.add_pull(41, body="Fixes #40")
        issues.resolve(fake, {40, 41})
        self.assertEqual(fake.calls.count(("issue", 40)), 1)


class FakeFidelityTest(unittest.TestCase):
    def test_missing_issue_and_pull_404(self):
        fake = FakeGitHub()
        for call in (fake.issue, fake.pull):
            with self.assertRaises(gh.GitHubError) as cm:
                call(7)
            self.assertEqual(cm.exception.status, 404)

    def test_assets_in_sync_and_duplicate_422(self):
        fake = FakeGitHub()
        rel = fake.create_release("v1.0.0", "n", "b", True)
        self.assertEqual(rel["assets"], [])
        path = os.path.join(tempfile.mkdtemp(), "a.jar")
        self.addCleanup(shutil.rmtree, os.path.dirname(path), True)
        with open(path, "wb") as f:
            f.write(b"x")
        fake.upload_asset(rel, path)
        got = fake.release_by_tag("v1.0.0")
        self.assertEqual([a["name"] for a in got["assets"]], ["a.jar"])
        self.assertIn("id", got["assets"][0])
        with self.assertRaises(gh.GitHubError) as cm:
            fake.upload_asset(rel, path)
        self.assertEqual(cm.exception.status, 422)


    def test_draft_publish_and_delete_asset(self):
        fake = FakeGitHub()
        old = fake.create_release("v1.0.0", "n", "b", True)
        rel = fake.create_release("v1.1.0", "n", "b", True, draft=True)
        self.assertTrue(rel["draft"])
        self.assertFalse(rel["latest"])
        with self.assertRaises(gh.GitHubError) as cm:
            fake.create_release("v1.1.0", "n", "b", True, draft=True)
        self.assertEqual(cm.exception.status, 422)
        path = os.path.join(tempfile.mkdtemp(), "a.jar")
        self.addCleanup(shutil.rmtree, os.path.dirname(path), True)
        with open(path, "wb") as f:
            f.write(b"x")
        fake.upload_asset(rel, path)
        fake.upload_asset(old, path)
        ids = [a["id"] for a in rel["assets"] + old["assets"]]
        self.assertEqual(len(set(ids)), 2)
        self.assertEqual(rel["assets"][0]["state"], "uploaded")
        fake.delete_asset(rel["assets"][0]["id"])
        self.assertEqual(rel["assets"], [])
        self.assertEqual(fake.assets[rel["id"]], {})
        with self.assertRaises(gh.GitHubError) as cm:
            fake.delete_asset(ids[0])
        self.assertEqual(cm.exception.status, 404)
        fake.publish_release(rel["id"], "final")
        self.assertEqual((rel["draft"], rel["latest"], rel["body"]),
                         (False, True, "final"))
        self.assertFalse(old["latest"])


class ReleaseApiTest(unittest.TestCase):
    """Requests gh.GitHub sends for the release calls."""

    def client(self, answers):
        api = gh.GitHub("o/r", "t")
        sent = []

        def fake_json(method, path, body=None, allow_404=False):
            sent.append((method, path, body))
            return answers.pop(0) if answers else None
        api._json = fake_json
        return api, sent

    def test_create_draft(self):
        api, sent = self.client([{}])
        api.create_release("v1.1.0", "v1.1.0", "b", True, draft=True)
        self.assertEqual(sent[0][2]["draft"], True)
        self.assertEqual(sent[0][2]["make_latest"], "false")

    def test_publish_release(self):
        api, sent = self.client([{}])
        api.publish_release(7, "body")
        self.assertEqual(sent, [("PATCH", "/repos/o/r/releases/7", {
            "draft": False, "make_latest": "true", "body": "body"})])

    def test_delete_asset(self):
        api, sent = self.client([])
        api.delete_asset(12)
        self.assertEqual(sent, [("DELETE", "/repos/o/r/releases/assets/12",
                                 None)])

    def test_release_by_tag_finds_draft_in_list(self):
        page1 = [{"tag_name": "v%d" % i} for i in range(100)]
        page2 = [{"tag_name": "v1.1.0", "draft": True}]
        api, sent = self.client([None, page1, page2])
        self.assertEqual(api.release_by_tag("v1.1.0"), page2[0])
        self.assertEqual([p for _, p, _ in sent], [
            "/repos/o/r/releases/tags/v1.1.0",
            "/repos/o/r/releases?per_page=100&page=1",
            "/repos/o/r/releases?per_page=100&page=2"])

    def test_release_by_tag_none(self):
        api, sent = self.client([None, []])
        self.assertIsNone(api.release_by_tag("v1.1.0"))


class RedirectTest(unittest.TestCase):
    def redirect(self, new_url):
        import urllib.request
        req = urllib.request.Request(
            "https://api.github.com/x",
            headers={"Authorization": "Bearer t", "Accept": "a"})
        h = gh._DropAuthOnCrossHost()
        return h.redirect_request(req, None, 302, "Found", {}, new_url)

    def test_cross_host_drops_authorization(self):
        new = self.redirect("https://objects.example.net/y")
        self.assertFalse(new.has_header("Authorization"))
        self.assertTrue(new.has_header("Accept"))

    def test_same_host_keeps_authorization(self):
        new = self.redirect("https://api.github.com/y")
        self.assertEqual(new.get_header("Authorization"), "Bearer t")


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
