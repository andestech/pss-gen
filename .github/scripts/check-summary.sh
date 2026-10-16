#!/usr/bin/env bash
# Reports the results that tests/run-tests.sh wrote to summary.log: the counts per
# suite, and one "::error::" line (a GitHub annotation) per test that did not pass.
# Exits 1 if a test failed or a summary.log is missing or empty; SKIP is not a failure.
# Usage: check-summary.sh <suite dir>...
set -euo pipefail

if [[ $# -eq 0 ]]; then
    echo "usage: $0 <suite dir>..." >&2
    exit 2
fi

rc=0
for dir in "$@"; do
    log="$dir/summary.log"
    if [[ ! -s "$log" ]]; then
        echo "::error::$log is missing or empty; the suite did not run"
        rc=1
        continue
    fi
    total=$(grep -c "" "$log")
    pass=$(grep -c ": PASS\$" "$log" || true)
    skip=$(grep -c ": SKIP\$" "$log" || true)
    line="$dir: $pass of $total tests passed, $skip skipped"
    echo "$line"
    if [[ -n "${GITHUB_STEP_SUMMARY:-}" ]]; then
        echo "- $line" >>"$GITHUB_STEP_SUMMARY"
    fi
    if [[ $((pass + skip)) -ne "$total" ]]; then
        grep -v -E ": (PASS|SKIP)\$" "$log" | while IFS= read -r test; do
            reason=$(sed -n 's/^FAIL: //p' "$dir/${test%: *}/result.log" 2>/dev/null || true)
            echo "::error::$dir: $test${reason:+ ($reason)}"
        done
        rc=1
    fi
done
exit "$rc"
