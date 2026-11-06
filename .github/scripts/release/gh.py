"""Minimal GitHub REST client (stdlib only) for the release flow."""
import json
import os
import re
import subprocess
import tempfile
import urllib.error
import urllib.parse
import urllib.request

API = "https://api.github.com"
API_VERSION = "2022-11-28"
SLUG_RES = (
    re.compile(r"^https://(?:[^@/]+@)?github\.com/([^/]+/[^/]+?)(?:\.git)?/?$"),
    re.compile(r"^git@github\.com:([^/]+/[^/]+?)(?:\.git)?$"),
)


class _DropAuthOnCrossHost(urllib.request.HTTPRedirectHandler):
    """Never forward the token to a host other than the original one."""

    def redirect_request(self, req, fp, code, msg, headers, newurl):
        new = super().redirect_request(req, fp, code, msg, headers, newurl)
        if new is not None and (urllib.parse.urlsplit(newurl).netloc
                                != urllib.parse.urlsplit(req.full_url).netloc):
            for name in list(new.headers):
                if name.lower() == "authorization":
                    del new.headers[name]
        return new


OPENER = urllib.request.build_opener(_DropAuthOnCrossHost)
TIMEOUT = 60


class GitHubError(RuntimeError):
    def __init__(self, status, url, message):
        super().__init__("HTTP %s for %s: %s" % (status, url, message))
        self.status = status
        self.url = url


def slug_from_env(repo_dir):
    slug = os.environ.get("GITHUB_REPOSITORY")
    if slug:
        return slug
    r = subprocess.run(["git", "-C", repo_dir, "remote", "get-url", "origin"],
                       capture_output=True, text=True)
    url = r.stdout.strip()
    for rx in SLUG_RES:
        m = rx.match(url)
        if m:
            return m.group(1)
    raise RuntimeError("cannot determine the repository slug: set "
                       "GITHUB_REPOSITORY or an origin remote on github.com")


class GitHub:
    def __init__(self, slug, token):
        self.slug = slug
        self.token = token

    def _headers(self, extra=None):
        h = {"Accept": "application/vnd.github+json",
             "X-GitHub-Api-Version": API_VERSION,
             "User-Agent": "pssgen-release"}
        if self.token:
            h["Authorization"] = "Bearer " + self.token
        h.update(extra or {})
        return h

    def _open(self, method, url, data=None, headers=None, allow_404=False):
        req = urllib.request.Request(url, data=data, method=method,
                                     headers=self._headers(headers))
        try:
            return OPENER.open(req, timeout=TIMEOUT)
        except urllib.error.HTTPError as e:
            if e.code == 404 and allow_404:
                return None
            raise GitHubError(e.code, url, e.read().decode(
                "utf-8", "replace")) from None

    def _json(self, method, path, body=None, allow_404=False):
        url = path if path.startswith("https://") else API + path
        data = None if body is None else json.dumps(body).encode()
        headers = {"Content-Type": "application/json"} if data else None
        resp = self._open(method, url, data, headers, allow_404)
        if resp is None:
            return None
        with resp:
            raw = resp.read()
        return json.loads(raw) if raw else None

    def _repo(self, tail):
        return "/repos/%s%s" % (self.slug, tail)

    def issue(self, n):
        return self._json("GET", self._repo("/issues/%d" % n))

    def pull(self, n):
        return self._json("GET", self._repo("/pulls/%d" % n))

    def pulls_of_commit(self, sha):
        return self._json("GET", self._repo("/commits/%s/pulls?per_page=100" % sha))

    def set_issue_state(self, n, state, reason=None):
        body = {"state": state}
        if reason is not None:
            body["state_reason"] = reason
        self._json("PATCH", self._repo("/issues/%d" % n), body)

    def add_labels(self, n, labels):
        self._json("POST", self._repo("/issues/%d/labels" % n),
                   {"labels": list(labels)})

    def create_issue(self, title, body):
        r = self._json("POST", self._repo("/issues"),
                       {"title": title, "body": body})
        return r["number"]

    def release_by_tag(self, tag):
        """The release of tag, a draft included, or None."""
        rel = self._json("GET", self._repo("/releases/tags/%s" % tag),
                         allow_404=True)
        if rel is not None:
            return rel
        # The by-tag endpoint only knows published releases; a draft is
        # found in the list, which shows drafts to a token with push access.
        page = 1
        while True:
            batch = self._json("GET", self._repo(
                "/releases?per_page=100&page=%d" % page)) or []
            for rel in batch:
                if rel.get("tag_name") == tag:
                    return rel
            if len(batch) < 100:
                return None
            page += 1

    def create_release(self, tag, name, body, latest, draft=False):
        return self._json("POST", self._repo("/releases"), {
            "tag_name": tag, "name": name, "body": body, "draft": draft,
            "make_latest": "true" if latest and not draft else "false"})

    def publish_release(self, release_id, body):
        """Make a draft public as the latest release, with its final body."""
        return self._json("PATCH", self._repo("/releases/%d" % release_id),
                          {"draft": False, "make_latest": "true",
                           "body": body})

    def delete_asset(self, asset_id):
        self._json("DELETE", self._repo("/releases/assets/%d" % asset_id))

    def update_release(self, release_id, body):
        self._json("PATCH", self._repo("/releases/%d" % release_id),
                   {"body": body})

    def upload_asset(self, release, path):
        base = re.sub(r"\{\?[^}]*\}$", "", release["upload_url"])
        url = base + "?" + urllib.parse.urlencode(
            {"name": os.path.basename(path)})
        with open(path, "rb") as f:
            data = f.read()
        resp = self._open("POST", url, data,
                          {"Content-Type": "application/octet-stream"})
        with resp:
            resp.read()

    def download(self, url, path):
        resp = self._open("GET", url,
                          headers={"Accept": "application/octet-stream"})
        fd, tmp = tempfile.mkstemp(dir=os.path.dirname(os.path.abspath(path)))
        try:
            with resp, os.fdopen(fd, "wb") as f:
                while True:
                    chunk = resp.read(1 << 16)
                    if not chunk:
                        break
                    f.write(chunk)
            os.replace(tmp, path)
        except BaseException:
            os.unlink(tmp)
            raise
