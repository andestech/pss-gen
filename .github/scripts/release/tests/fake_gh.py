"""In-memory stand-in for gh.GitHub that records every call."""
import hashlib
import os

from gh import GitHubError


class FakeGitHub:
    # Server time stamped on issues the fake closes, in GitHub's format.
    now = "2030-01-01T00:00:00Z"

    def __init__(self, issues=None, pulls=None, releases=None,
                 commit_pulls=None):
        self.issues = dict(issues or {})
        self.pulls = dict(pulls or {})
        self.releases = dict(releases or {})
        self.commit_pulls = dict(commit_pulls or {})
        self.assets = {}
        self.downloads = {}
        self.calls = []
        self._next_number = max(
            [0] + list(self.issues) + list(self.pulls)) + 1
        self._next_release_id = 1
        self._next_asset_id = 1

    def add_issue(self, number, title="", type_name=None, labels=(),
                  state="open", body="", pull_request=False, closed_at=None):
        data = {"number": number, "title": title, "state": state,
                "body": body, "labels": [{"name": n} for n in labels],
                "type": {"name": type_name} if type_name else None,
                "closed_at": closed_at}
        if pull_request:
            data["pull_request"] = {}
        self.issues[number] = data
        self._next_number = max(self._next_number, number + 1)
        return data

    def add_pull(self, number, body="", merged=True):
        self.pulls[number] = {"number": number, "body": body,
                              "merged": merged}
        self.add_issue(number, body=body, pull_request=True)

    def add_release(self, tag, body="", draft=False, latest=False):
        rid = self._next_release_id
        self._next_release_id += 1
        self.releases[tag] = {
            "id": rid, "tag_name": tag, "name": tag, "body": body,
            "draft": draft, "latest": latest, "assets": [],
            "upload_url": "https://uploads.example/%d/assets{?name,label}"
                          % rid}
        return self.releases[tag]

    def issue(self, n):
        self.calls.append(("issue", n))
        if n not in self.issues:
            raise GitHubError(404, "issues/%d" % n, "Not Found")
        return self.issues[n]

    def pull(self, n):
        self.calls.append(("pull", n))
        if n not in self.pulls:
            raise GitHubError(404, "pulls/%d" % n, "Not Found")
        return self.pulls[n]

    def pulls_of_commit(self, sha):
        self.calls.append(("pulls_of_commit", sha))
        return self.commit_pulls.get(sha, [])

    def set_issue_state(self, n, state, reason=None):
        self.calls.append(("set_issue_state", n, state, reason))
        self.issues[n]["state"] = state
        self.issues[n]["state_reason"] = reason
        self.issues[n]["closed_at"] = (self.now if state == "closed"
                                       else None)

    def add_labels(self, n, labels):
        self.calls.append(("add_labels", n, list(labels)))
        have = self.issues[n]["labels"]
        for name in labels:
            if name not in [x["name"] for x in have]:
                have.append({"name": name})

    def open_issues(self):
        self.calls.append(("open_issues",))
        return [i for i in self.issues.values()
                if i["state"] == "open" and "pull_request" not in i]

    def create_issue(self, title, body):
        self.calls.append(("create_issue", title, body))
        n = self._next_number
        self.add_issue(n, title=title, body=body)
        return n

    def release_by_tag(self, tag):
        self.calls.append(("release_by_tag", tag))
        return self.releases.get(tag)

    def create_release(self, tag, name, body, latest, draft=False):
        self.calls.append(("create_release", tag, name, body, latest, draft))
        if tag in self.releases:
            raise GitHubError(422, "releases", "already_exists: " + tag)
        rel = self.add_release(tag, body=body, draft=draft,
                               latest=latest and not draft)
        rel["name"] = name
        return rel

    def _release(self, release_id):
        for rel in self.releases.values():
            if rel["id"] == release_id:
                return rel
        raise GitHubError(404, "releases/%d" % release_id, "Not Found")

    def publish_release(self, release_id, body):
        self.calls.append(("publish_release", release_id, body))
        rel = self._release(release_id)
        for other in self.releases.values():
            other["latest"] = False
        rel.update(draft=False, latest=True, body=body)
        return rel

    def delete_asset(self, asset_id):
        self.calls.append(("delete_asset", asset_id))
        for rel in self.releases.values():
            for a in rel["assets"]:
                if a["id"] == asset_id:
                    rel["assets"].remove(a)
                    self.assets.get(rel["id"], {}).pop(a["name"], None)
                    return
        raise GitHubError(404, "releases/assets/%d" % asset_id, "Not Found")

    def update_release(self, release_id, body):
        self.calls.append(("update_release", release_id, body))
        self._release(release_id)["body"] = body

    def upload_asset(self, release, path):
        self.calls.append(("upload_asset", release["id"], path))
        name = os.path.basename(path)
        rel = self._release(release["id"])
        if any(a["name"] == name for a in rel["assets"]):
            raise GitHubError(422, "uploads/%d" % rel["id"],
                              "already_exists: " + name)
        with open(path, "rb") as f:
            data = f.read()
        self.assets.setdefault(rel["id"], {})[name] = data
        asset_id = self._next_asset_id
        self._next_asset_id += 1
        rel["assets"].append({
            "id": asset_id, "name": name, "state": "uploaded",
            "digest": "sha256:" + hashlib.sha256(data).hexdigest()})

    def download(self, url, path):
        self.calls.append(("download", url, path))
        with open(path, "wb") as f:
            f.write(self.downloads[url])
