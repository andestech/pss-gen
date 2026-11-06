"""Tests for version.py."""
import unittest

import version
from tests.gitutil import TempRepo


class ParseTest(unittest.TestCase):
    def test_accepts(self):
        self.assertEqual(version.parse("v0.0.0"), (0, 0, 0))
        self.assertEqual(version.parse("v10.20.30"), (10, 20, 30))
        self.assertEqual(version.parse(" v1.2.3\n"), (1, 2, 3))

    def test_rejects(self):
        for bad in ("v1.2", "v01.2.3", "v1.2.3-rc1", "V1.2.3", "1.2.3", ""):
            with self.assertRaises(ValueError, msg=bad):
                version.parse(bad)

    def test_fmt(self):
        self.assertEqual(version.fmt((2, 4, 1)), "v2.4.1")


class NextVersionTest(unittest.TestCase):
    def test_kinds(self):
        self.assertEqual(version.next_version((2, 4, 1), "minor"), (2, 5, 0))
        self.assertEqual(version.next_version((2, 4, 1), "major"), (3, 0, 0))
        self.assertEqual(version.next_version((2, 4, 1), "patch"), (2, 4, 2))

    def test_bad_kind(self):
        with self.assertRaises(ValueError):
            version.next_version((1, 0, 0), "huge")


class SuccessorTest(unittest.TestCase):
    prev = (2, 4, 1)

    def errs(self, new, branch):
        return version.check_successor(self.prev, new, branch)

    def test_hot_fix(self):
        self.assertEqual(self.errs((2, 4, 2), "hot_fix"), [])
        self.assertTrue(self.errs((2, 5, 0), "hot_fix"))

    def test_develop(self):
        self.assertEqual(self.errs((2, 5, 0), "develop"), [])
        self.assertEqual(self.errs((3, 0, 0), "develop"), [])
        for bad in ((2, 4, 2), (2, 6, 0), (3, 0, 1), (3, 1, 0)):
            self.assertTrue(self.errs(bad, "develop"), bad)

    def test_other_branch(self):
        self.assertTrue(self.errs((2, 4, 2), "refactor"))


class PreviousReleaseTest(unittest.TestCase):
    def test_highest_reachable(self):
        r = TempRepo(self)
        r.commit("base")
        r.git("checkout", "-q", "-b", "side")
        r.commit("side")
        r.git("tag", "-a", "v1.0.2", "-m", "x")
        r.git("checkout", "-q", "main")
        r.commit("m1")
        r.git("tag", "v2.3.0")
        r.commit("m2")
        r.git("tag", "-a", "v2.4.1", "-m", "x")
        r.git("tag", "-a", "v9.9.9-rc1", "-m", "x")
        r.git("tag", "-a", "nightly", "-m", "x")
        self.assertEqual(version.previous_release(r.path, "main"), (2, 4, 1))

    def test_numeric_not_lexical(self):
        r = TempRepo(self)
        r.commit("a")
        r.git("tag", "v1.9.0")
        r.commit("b")
        r.git("tag", "v1.10.0")
        self.assertEqual(version.previous_release(r.path, "main"), (1, 10, 0))

    def test_none(self):
        r = TempRepo(self)
        r.commit("a")
        self.assertIsNone(version.previous_release(r.path, "main"))


if __name__ == "__main__":
    unittest.main()
