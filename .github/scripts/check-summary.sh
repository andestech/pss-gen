#!/usr/bin/env bash
# Pass/fail gate for the regression. "make" in samples/ and issue/ exits 0 even
# when tests fail and records each test's result (PASS, FAIL, NO RESULT or
# NO RESULT_LOG) in summary.log, so CI needs this check.
# Usage: check-summary.sh <dir>...        (each <dir> contains summary.log)
set -euo pipefail

if [[ $# -eq 0 ]]; then
    echo "usage: $0 <dir>..." >&2
    exit 2
fi

rc=0
for dir in "$@"; do
    log="$dir/summary.log"
    if [[ ! -s "$log" ]]; then
        echo "::error::$log is missing or empty; the regression did not complete"
        rc=1
        continue
    fi
    # Lines look like "./test_x: <ESC>[32mPASS<ESC>[00m"; drop the colour codes.
    plain=$(sed -e "s/\x1b\[[0-9;]*m//g" "$log")
    total=$(grep -c "" "$log")
    pass=$(grep -c ": PASS\$" <<<"$plain" || true)
    echo "$dir: $pass of $total tests passed"
    if [[ -n "${GITHUB_STEP_SUMMARY:-}" ]]; then
        echo "- $dir: $pass of $total tests passed" >>"$GITHUB_STEP_SUMMARY"
    fi
    if [[ "$pass" -ne "$total" ]]; then
        grep -v ": PASS\$" <<<"$plain" | while IFS= read -r line; do
            echo "::error::$dir: $line"
        done
        rc=1
    fi
done
exit "$rc"
