#!/usr/bin/env python3
"""Command line of the release flow.

  release.py prepare <major|minor|patch>   on develop or hot_fix: write the next
                                           VERSION and print the release-note draft
  release.py check --pr-event <event.json> check a release PR into main; the report
                                           goes to stdout and $GITHUB_STEP_SUMMARY
  release.py previous-tag [<rev>]          print the previous release tag of rev
                                           (default HEAD); exit 1 when there is none
  release.py tag [--sha <sha>]             check the release merged into main as sha
                                           (default HEAD) and create its annotated
                                           tag locally unless it exists
  release.py publish [--sha <sha>] [--dist <dir>] [--dry-run]
                                           push the tag, create the GitHub Release
                                           with the files of dist (default dist)
                                           and update the listed issues
  release.py sync <develop|hot_fix> [--tag <tag>] [--test-cmd <cmd>]
                                           sync the branch on origin to the release
                                           tag (default: the VERSION of HEAD); a sync
                                           merge is pushed only when the shell
                                           command cmd passes on it, else an issue
                                           asks for a manual sync

The GitHub API is read with GITHUB_TOKEN when set, anonymously otherwise.
"""
import argparse
import json
import os
import subprocess
import sys

import check
import gh
import notes
import publish
import sync
import version


def _git(repo, *args):
    r = subprocess.run(["git", "-C", repo, *args], capture_output=True,
                       text=True)
    if r.returncode != 0:
        raise RuntimeError("git %s failed: %s" % (" ".join(args),
                                                  r.stderr.strip()))
    return r.stdout.strip()


def _toplevel():
    return _git(".", "rev-parse", "--show-toplevel")


def cmd_prepare(args):
    repo = _toplevel()
    branch = _git(repo, "rev-parse", "--abbrev-ref", "HEAD")
    if branch not in check.RELEASE_BRANCHES:
        sys.exit("prepare runs on develop or hot_fix, not on %s" % branch)
    if _git(repo, "status", "--porcelain"):
        sys.exit("the working tree is not clean")
    if branch == "develop" and args.kind == "patch":
        sys.exit("a patch release is cut from hot_fix")
    if branch == "hot_fix" and args.kind != "patch":
        sys.exit("a %s release is cut from develop" % args.kind)
    has_main = subprocess.run(
        ["git", "-C", repo, "rev-parse", "--verify", "-q", "origin/main"],
        capture_output=True).returncode == 0
    prev = version.previous_release(repo, "origin/main" if has_main else "HEAD")
    if prev is None:
        sys.exit("no release tag is reachable from main")
    contains = subprocess.run(
        ["git", "-C", repo, "merge-base", "--is-ancestor", version.fmt(prev),
         "HEAD"]).returncode == 0
    if not contains:
        sys.exit("%s does not contain the previous release %s; merge main "
                 "into it first" % (branch, version.fmt(prev)))
    new = version.next_version(prev, args.kind)
    api = gh.GitHub(gh.slug_from_env(repo), os.environ.get("GITHUB_TOKEN"))
    rng = check.collect(repo, api, "%s..HEAD" % version.fmt(prev))
    with open(os.path.join(repo, "VERSION"), "w") as f:
        f.write(version.fmt(new) + "\n")
    for n in sorted(rng.missing):
        print("warning: #%d is not known to GitHub" % n, file=sys.stderr)
    for i in rng.unclassified:
        print("warning: #%d is neither a feature nor a bug" % i.number,
              file=sys.stderr)
    for n in sorted(rng.reverted):
        print("warning: the fix of #%d was reverted in this range" % n,
              file=sys.stderr)
    for n in rng.no_note:
        print("warning: PR #%d has no release-note line" % n, file=sys.stderr)
    print(notes.draft(rng.features, rng.bugs, rng.incompatible,
                      rng.contributions), end="")
    print("VERSION is now %s; commit it as '[RELEASE] %s'"
          % (version.fmt(new), version.fmt(new)), file=sys.stderr)
    return 0


def cmd_check(args):
    repo = _toplevel()
    with open(args.pr_event) as f:
        event = json.load(f)["pull_request"]
    pr = {
        "head_ref": event["head"]["ref"],
        "head_repo": (event["head"].get("repo") or {}).get("full_name", ""),
        "base_repo": event["base"]["repo"]["full_name"],
        "head_sha": event["head"]["sha"],
        "base_sha": event["base"]["sha"],
        "body": event.get("body") or "",
    }
    api = gh.GitHub(pr["base_repo"], os.environ.get("GITHUB_TOKEN"))
    report = check.run(repo, api, pr)
    text = check.render(report)
    print(text)
    summary = os.environ.get("GITHUB_STEP_SUMMARY")
    if summary:
        with open(summary, "a") as f:
            f.write(text + "\n")
    return 1 if report.errors else 0


def cmd_previous_tag(args):
    prev = version.previous_release(_toplevel(), args.rev)
    if prev is None:
        return 1
    print(version.fmt(prev))
    return 0


def _publish_args(args):
    repo = _toplevel()
    sha = _git(repo, "rev-parse", "--verify", args.sha + "^{commit}")
    api = gh.GitHub(gh.slug_from_env(repo), os.environ.get("GITHUB_TOKEN"))
    return repo, api, sha


def cmd_tag(args):
    repo, api, sha = _publish_args(args)
    try:
        done = publish.tag_local(repo, api, sha)
    except publish.PublishError as e:
        sys.exit("tag: %s" % e)
    print(done or "the release tag of %s exists already" % sha)
    return 0


def cmd_publish(args):
    repo, api, sha = _publish_args(args)
    try:
        done = publish.run(repo, api, sha, args.dist, args.dry_run)
    except publish.PublishError as e:
        sys.exit("publish: %s" % e)
    prefix = "would " if args.dry_run else ""
    if not done:
        print("nothing left to do")
    for action in done:
        print(prefix + action)
    return 0


def cmd_sync(args):
    repo = _toplevel()
    tag = args.tag
    if tag is None:
        try:
            tag = version.fmt(version.parse(_git(repo, "show", "HEAD:VERSION")))
        except ValueError as e:
            sys.exit("sync: VERSION at HEAD: %s" % e)
    test_cmd = None if args.test_cmd is None else ["bash", "-c", args.test_cmd]
    api = gh.GitHub(gh.slug_from_env(repo), os.environ.get("GITHUB_TOKEN"))
    try:
        outcome = sync.run(repo, api, args.branch, tag, test_cmd)
    except sync.SyncError as e:
        sys.exit("sync: %s" % e)
    print("%s: %s" % (args.branch, outcome))
    if outcome == sync.NEEDS_MANUAL:
        msg = ("%s is not synced to %s; the issue '%s' asks for a manual "
               "sync merge" % (args.branch, tag,
                               sync.issue_title(tag, args.branch)))
        print("::warning::" + msg)
        summary = os.environ.get("GITHUB_STEP_SUMMARY")
        if summary:
            with open(summary, "a") as f:
                f.write("- warning: %s\n" % msg)
    return 0


def main(argv=None):
    p = argparse.ArgumentParser(description=__doc__.split("\n")[0])
    sub = p.add_subparsers(dest="cmd", required=True)
    s = sub.add_parser("prepare")
    s.add_argument("kind", choices=["major", "minor", "patch"])
    s.set_defaults(fn=cmd_prepare)
    s = sub.add_parser("check")
    s.add_argument("--pr-event", required=True)
    s.set_defaults(fn=cmd_check)
    s = sub.add_parser("previous-tag")
    s.add_argument("rev", nargs="?", default="HEAD")
    s.set_defaults(fn=cmd_previous_tag)
    s = sub.add_parser("tag")
    s.add_argument("--sha", default="HEAD")
    s.set_defaults(fn=cmd_tag)
    s = sub.add_parser("publish")
    s.add_argument("--sha", default="HEAD")
    s.add_argument("--dist", default="dist")
    s.add_argument("--dry-run", action="store_true")
    s.set_defaults(fn=cmd_publish)
    s = sub.add_parser("sync")
    s.add_argument("branch", choices=check.RELEASE_BRANCHES)
    s.add_argument("--tag")
    s.add_argument("--test-cmd", help=(
        "shell command that must pass on a sync merge before it is pushed; "
        "without it a branch that needs a sync merge is not pushed and an "
        "issue asks for a manual sync"))
    s.set_defaults(fn=cmd_sync)
    args = p.parse_args(argv)
    return args.fn(args)


if __name__ == "__main__":
    sys.exit(main())
