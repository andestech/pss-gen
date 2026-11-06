"""In-memory stand-in for gh.GitHub that records every call."""
import os


class FakeGitHub:
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

    def add_issue(self, number, title="", type_name=None, labels=(),
                  state="open", body="", pull_request=False):
        data = {"number": number, "title": title, "state": state,
                "body": body, "labels": [{"name": n} for n in labels],
                "type": {"name": type_name} if type_name else None}
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
            "draft": draft, "latest": latest,
            "upload_url": "https://uploads.example/%d/assets{?name,label}"
                          % rid}
        return self.releases[tag]

    def issue(self, n):
        self.calls.append(("issue", n))
        return self.issues[n]

    def pull(self, n):
        self.calls.append(("pull", n))
        return self.pulls[n]

    def pulls_of_commit(self, sha):
        self.calls.append(("pulls_of_commit", sha))
        return self.commit_pulls.get(sha, [])

    def set_issue_state(self, n, state, reason=None):
        self.calls.append(("set_issue_state", n, state, reason))
        self.issues[n]["state"] = state
        self.issues[n]["state_reason"] = reason

    def add_labels(self, n, labels):
        self.calls.append(("add_labels", n, list(labels)))
        have = self.issues[n]["labels"]
        for name in labels:
            if name not in [x["name"] for x in have]:
                have.append({"name": name})

    def create_issue(self, title, body):
        self.calls.append(("create_issue", title, body))
        n = self._next_number
        self.add_issue(n, title=title, body=body)
        return n

    def release_by_tag(self, tag):
        self.calls.append(("release_by_tag", tag))
        return self.releases.get(tag)

    def create_release(self, tag, name, body, latest):
        self.calls.append(("create_release", tag, name, body, latest))
        rel = self.add_release(tag, body=body, latest=latest)
        rel["name"] = name
        return rel

    def update_release(self, release_id, body):
        self.calls.append(("update_release", release_id, body))
        for rel in self.releases.values():
            if rel["id"] == release_id:
                rel["body"] = body
                return
        raise KeyError(release_id)

    def upload_asset(self, release, path):
        self.calls.append(("upload_asset", release["id"], path))
        self.assets.setdefault(release["id"], {})[os.path.basename(path)] = \
            open(path, "rb").read()

    def download(self, url, path):
        self.calls.append(("download", url, path))
        with open(path, "wb") as f:
            f.write(self.downloads[url])
