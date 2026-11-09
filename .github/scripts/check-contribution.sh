#!/usr/bin/env bash
# Fails a contribution that changes VERSION or adds build output. The repository owner
# sets VERSION when a release is cut, and the release workflow builds the jar, so a
# pull request into develop or hot_fix touches neither. gradle-wrapper.jar is source:
# the wrapper that every build runs.
# Prints one "::error::" line (a GitHub annotation) per offending path.
# Usage: check-contribution.sh <base> <head>
set -euo pipefail

if [[ $# -ne 2 ]]; then
    echo "usage: $0 <base> <head>" >&2
    exit 2
fi

rc=0
while IFS=$'\t' read -r status path; do
    if [[ "$path" == VERSION ]]; then
        echo "::error file=VERSION::VERSION is set by the repository owner when a release is cut; leave it unchanged"
        rc=1
    elif [[ "$status" == A && "$path" =~ \.(jar|class)$ && "$path" != gradle/wrapper/gradle-wrapper.jar ]]; then
        echo "::error file=$path::$path is build output; the release workflow builds the jar"
        rc=1
    fi
done < <(git diff --no-renames --name-status "$1" "$2")
exit $rc
