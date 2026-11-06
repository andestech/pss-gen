#!/usr/bin/env bash
# Self-test of tests/compare-versions.sh. It builds a small suite in a temporary
# directory and two fake PSSGen commands that react to the test name, so it needs
# neither Java nor network.
#
# Usage: bash tests/compare-test/run.sh
set -uo pipefail

HERE=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
COMPARE=$HERE/../compare-versions.sh
WORK=$(mktemp -d)
trap 'rm -rf "$WORK"' EXIT

fails=0
check() {
    local name=$1 pattern=$2 report=${3:-$WORK/out/report.md}
    if grep -qE -- "$pattern" "$report"; then
        echo "PASS: $name"
    else
        echo "FAIL: $name (no line matching '$pattern' in report.md)"
        fails=$((fails + 1))
    fi
}

SUITE=$WORK/src/suite
for t in same out status msg skipped onlyone; do
    mkdir -p "$SUITE/$t"
    touch "$SUITE/$t/test.pss"
done
printf 'skip = not run\n' > "$SUITE/skipped/test.cfg"

# The fake reads the test name from the directory it runs in. $1 is the version.
cat > "$WORK/fake" <<'FAKE'
#!/usr/bin/env bash
version=$1
test=$(basename "$PWD")
stamp=$((RANDOM % 900 + 100))
echo "[0.$stamp""s] PSS-INFO: start"
case "$test" in
    same)    echo ".text" > test.s ;;
    out)     echo "code $version" > test.s ;;
    status)  echo ".text" > test.s
             [[ "$version" == base ]] || exit 134 ;;
    msg)     echo ".text" > test.s
             echo "[0.$stamp""s] PSS-WARNING: from $version" ;;
    onlyone) [[ "$version" == base ]] || echo ".text" > test.s ;;
    kept|updated) echo "code $version" > test.s ;;
esac
echo "[0.$stamp""s] PSS-INFO: done"
FAKE
printf '#!/usr/bin/env bash\nexec bash %s base "$@"\n' "$WORK/fake" > "$WORK/base-cmd"
printf '#!/usr/bin/env bash\nexec bash %s new "$@"\n' "$WORK/fake" > "$WORK/new-cmd"
chmod +x "$WORK/base-cmd" "$WORK/new-cmd"

before=$(cd "$WORK/src" && find . -type f | LC_ALL=C sort | xargs md5sum)

bash "$COMPARE" --base-cmd "$WORK/base-cmd" --new-cmd "$WORK/new-cmd" \
    --base-name base --new-name new --out "$WORK/out" "$SUITE" > "$WORK/stdout.log" 2>&1
rc=$?
if [[ "$rc" -eq 0 ]]; then
    echo "PASS: cmp_exit_zero"
else
    echo "FAIL: cmp_exit_zero (exit $rc)"
    cat "$WORK/stdout.log"
    fails=$((fails + 1))
fi

check cmp_classes_same    '^SAME: 1$'
check cmp_classes_output  '^OUTPUT: 2$'
check cmp_classes_status  '^STATUS: 1$'
check cmp_classes_message '^MESSAGE: 1$'
check cmp_classes_skip    '^SKIP: 1$'
check cmp_per_test_out    'suite/out: OUTPUT'
check cmp_per_test_msg    'suite/msg: MESSAGE'
check cmp_per_test_status 'suite/status: STATUS .*base=0 new=134'
check cmp_one_side_output 'suite/onlyone: OUTPUT'
if grep -qE 'suite/same: ' "$WORK/out/report.md"; then
    echo "FAIL: cmp_timestamps_ignored (suite/same reported)"
    fails=$((fails + 1))
else
    echo "PASS: cmp_timestamps_ignored"
fi

after=$(cd "$WORK/src" && find . -type f | LC_ALL=C sort | xargs md5sum)
if [[ "$before" == "$after" ]]; then
    echo "PASS: cmp_source_untouched"
else
    echo "FAIL: cmp_source_untouched"
    fails=$((fails + 1))
fi

# A second run with the same names reuses the out directory without leftovers.
bash "$COMPARE" --base-cmd "$WORK/base-cmd" --new-cmd "$WORK/new-cmd" \
    --base-name base --new-name new --out "$WORK/out" "$SUITE" > /dev/null 2>&1
check cmp_rerun '^SAME: 1$'

# The golden cross-check resolves goldens in the git checkout that holds the script,
# so it runs on a copy of the script inside a temporary repository: both tests change
# their output, but only "updated" has its golden changed since the tag "base".
REPO=$WORK/repo
mkdir -p "$REPO/tests"
cp "$COMPARE" "$HERE/../lib-cfg.sh" "$REPO/tests/"
for t in kept updated; do
    mkdir -p "$REPO/tests/suite/$t"
    touch "$REPO/tests/suite/$t/test.pss"
    echo "code base" > "$REPO/tests/suite/$t/test.s.golden"
done
repo_git() {
    git -C "$REPO" -c user.name=compare-test -c user.email=compare-test@localhost "$@"
}
repo_git -c init.defaultBranch=main init -q
repo_git add -A
repo_git commit -q -m base
repo_git tag base
echo "code new" > "$REPO/tests/suite/updated/test.s.golden"
repo_git commit -q -am update

bash "$REPO/tests/compare-versions.sh" --base-cmd "$WORK/base-cmd" --new-cmd "$WORK/new-cmd" \
    --base-name base --new-name new --out "$WORK/gout" --golden-rev base \
    "$REPO/tests/suite" > "$WORK/golden.log" 2>&1 || cat "$WORK/golden.log"
check cmp_golden_counts '^OUTPUT: 2$' "$WORK/gout/report.md"
flagged=$(sed -n '/^## Output changed but golden unchanged$/,/^## Differences$/p' \
    "$WORK/gout/report.md")
if grep -qxF -- '- suite/kept' <<< "$flagged" && ! grep -qF -- 'suite/updated' <<< "$flagged"; then
    echo "PASS: cmp_golden_cross_check"
else
    echo "FAIL: cmp_golden_cross_check (flagged section: $(tr '\n' ' ' <<< "$flagged"))"
    fails=$((fails + 1))
fi

[[ "$fails" -eq 0 ]]
