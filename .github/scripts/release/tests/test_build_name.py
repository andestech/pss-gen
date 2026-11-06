"""Tests for make/build-name.sh, run against throw-away repositories."""
import os
import re
import shutil
import subprocess
import tempfile
import unittest

SCRIPT = os.path.abspath(os.path.join(
    os.path.dirname(__file__), "..", "..", "..", "..", "make", "build-name.sh"))


class BuildNameTest(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.mkdtemp()
        self.addCleanup(shutil.rmtree, self.tmp, True)
        os.makedirs(os.path.join(self.tmp, "make"))
        shutil.copy(SCRIPT, os.path.join(self.tmp, "make", "build-name.sh"))

    def git(self, *args):
        subprocess.run(
            ["git", "-c", "user.name=t", "-c", "user.email=t@example.com",
             "-c", "tag.gpgSign=false", "-c", "commit.gpgSign=false",
             "-C", self.tmp, *args],
            check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)

    def write_version(self, text):
        with open(os.path.join(self.tmp, "VERSION"), "wb") as f:
            f.write(text.encode())

    def init_repo(self, version="v1.2.3"):
        self.write_version(version + "\n")
        self.git("init", "-q")
        self.git("add", ".")
        self.git("commit", "-q", "-m", "first")

    def run_script(self):
        return subprocess.run(
            ["bash", os.path.join(self.tmp, "make", "build-name.sh"), self.tmp],
            capture_output=True, text=True)

    def test_tagged_head_plain(self):
        self.init_repo()
        self.git("tag", "-a", "v1.2.3", "-m", "release")
        r = self.run_script()
        self.assertEqual((r.returncode, r.stdout), (0, "v1.2.3\n"))

    def test_untagged_describe(self):
        self.init_repo()
        self.git("tag", "-a", "v1.2.3", "-m", "release")
        self.git("commit", "-q", "--allow-empty", "-m", "a")
        self.git("commit", "-q", "--allow-empty", "-m", "b")
        r = self.run_script()
        self.assertEqual(r.returncode, 0)
        self.assertRegex(r.stdout.strip(), r"^v1\.2\.3-2-g[0-9a-f]{7,}$")

    def test_dirty(self):
        self.init_repo()
        self.git("tag", "-a", "v1.2.3", "-m", "release")
        with open(os.path.join(self.tmp, "make", "build-name.sh"), "a") as f:
            f.write("# edit\n")
        r = self.run_script()
        self.assertEqual(r.returncode, 0)
        self.assertTrue(r.stdout.strip().endswith("-dirty"), r.stdout)

    def test_no_git_plain(self):
        self.write_version("v1.2.3\n")
        r = self.run_script()
        self.assertEqual((r.returncode, r.stdout), (0, "v1.2.3\n"))

    def test_bad_version(self):
        for bad in ("v1.2.3-rc1", "v01.2.3", "1.2.3"):
            with self.subTest(version=bad):
                self.write_version(bad + "\n")
                r = self.run_script()
                self.assertEqual(r.returncode, 2)
                self.assertIn("VERSION", r.stderr)

    def test_crlf_version(self):
        self.write_version("v1.2.3\r\n")
        r = self.run_script()
        self.assertEqual((r.returncode, r.stdout), (0, "v1.2.3\n"))


if __name__ == "__main__":
    unittest.main()
