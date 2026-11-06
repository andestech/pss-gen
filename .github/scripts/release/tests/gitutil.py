"""Throw-away git repository helper shared by the release script tests."""
import subprocess
import tempfile
import shutil


class TempRepo:
    def __init__(self, testcase):
        self.path = tempfile.mkdtemp()
        testcase.addCleanup(shutil.rmtree, self.path, True)
        self.git("init", "-q", "-b", "main")

    def git(self, *args):
        r = subprocess.run(
            ["git", "-c", "user.name=t", "-c", "user.email=t@example.com",
             "-c", "tag.gpgSign=false", "-c", "commit.gpgSign=false",
             "-C", self.path, *args],
            check=True, capture_output=True, text=True)
        return r.stdout.strip()

    def commit(self, subject, body=""):
        msg = subject + ("\n\n" + body if body else "")
        self.git("commit", "-q", "--allow-empty", "-m", msg)
        return self.git("rev-parse", "HEAD")
