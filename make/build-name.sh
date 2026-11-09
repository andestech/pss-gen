#!/usr/bin/env bash
# Prints the build name of a PSSGen checkout: the jar is pssgen_<name>.jar.
# Usage: build-name.sh [<repo dir>]    (default: the checkout holding this script)
#
# <name> is VERSION when HEAD is the commit tagged with exactly that version, or
# when <repo dir> is not a git work tree (a source archive). Any other commit is
# named by "git describe", and --dirty keeps an edited tree from looking like a
# clean build. Without any reachable v* tag the name is VERSION-g<short sha>.
# Exits 2 when VERSION is not vMAJOR.MINOR.PATCH (no leading zeros, no suffix).
set -euo pipefail

dir=${1:-$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)}

version=$(tr -d '[:space:]' < "$dir/VERSION") || exit 2
if [[ ! $version =~ ^v(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)$ ]]; then
    echo "$dir/VERSION must look like v1.2.3, found '$version'" >&2
    exit 2
fi

if [[ $(git -C "$dir" rev-parse --is-inside-work-tree 2>/dev/null) != true ]]; then
    echo "$version"
    exit 0
fi

# A clean checkout of the release tag keeps the plain version; the check is
# on the tag, so an uncommitted edit still falls through to describe --dirty.
tag=$(git -C "$dir" describe --tags --exact-match --match 'v[0-9]*' HEAD 2>/dev/null || true)
if [[ $tag == "$version" ]] && [[ -z $(git -C "$dir" status --porcelain --untracked-files=no) ]]; then
    echo "$version"
elif git -C "$dir" describe --tags --match 'v[0-9]*' --dirty 2>/dev/null; then
    :
else
    # No release tag is reachable (shallow clone, fork without tags).
    echo "$version-g$(git -C "$dir" rev-parse --short HEAD)$(
        [[ -z $(git -C "$dir" status --porcelain --untracked-files=no) ]] || echo -dirty)"
fi
