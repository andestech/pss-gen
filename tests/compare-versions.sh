#!/usr/bin/env bash
# Runs the regression tests with two PSSGen versions and reports which results differ.
#
# Usage: compare-versions.sh --base-cmd <cmd> --new-cmd <cmd> --base-name <name>
#            --new-name <name> --out <dir> [--golden-rev <rev>] <dir>...
#
# <cmd> is the command that starts PSSGen, as PSSGEN of run-tests.sh. Each <dir> is a
# suite or a single test, as for run-tests.sh. A test is copied to <out>/<name>/<suite>/
# <test>/ and run there with the arguments of its test.cfg, so the test directories are
# never written. <out> is emptied first; tests with "skip =" are not run.
#
# A test falls in the first class that applies:
#   STATUS   the exit statuses differ
#   OUTPUT   test.s differs, or only one version writes it
#   MESSAGE  run.log differs once the elapsed-time prefix "[0.130s] " is removed
#   SAME     none of the above
#   SKIP     test.cfg says skip
#
# <out>/report.md lists the count per class and, for every test that is not SAME, the
# class, both exit statuses and the first 20 diff lines. With --golden-rev, the OUTPUT
# tests whose test.s.golden is not among the files changed in <rev>..HEAD come first:
# their output changed without a deliberate golden update.
#
# The exit status is 0 when the comparison ran, even if results differ, and 2 on a usage
# or run error.
set -uo pipefail

source "$(dirname "${BASH_SOURCE[0]}")/lib-cfg.sh"

CLASSES=(STATUS OUTPUT MESSAGE SAME SKIP)
DIFF_LINES=20

die() {
    echo "compare-versions.sh: $*" >&2
    exit 2
}

is_test() {
    [[ -f "$1/test.pss" || -f "$1/test.cfg" ]]
}

list_tests() {
    (cd "$1" && find . \( -name test.pss -o -name test.cfg \) -printf '%h\n') | LC_ALL=C sort -u
}

# Copies test directory $1 to $2 and runs command $3 there; the exit status goes to
# $2/exit.status and the log without the elapsed-time prefix to $2/run.norm.log.
run_version() {
    local src=$1 dst=$2 command=$3 rc
    local -a cmd args
    mkdir -p "$dst"
    cp -R "$src/." "$dst/"
    rm -f "$dst"/{test.s,run.log,result.log,diff.log}
    read -ra cmd <<< "$command"
    read -ra args <<< "$cfg_args"
    (cd "$dst" && "${cmd[@]}" "${args[@]}" > run.log 2>&1)
    rc=$?
    echo "$rc" > "$dst/exit.status"
    sed -E 's/^\[[0-9]+\.[0-9]{3}s\] //' "$dst/run.log" > "$dst/run.norm.log"
}

# Prints the class of the test whose two run directories are $1 (base) and $2 (new).
classify() {
    local b=$1 n=$2 b_has=0 n_has=0
    [[ -f "$b/test.s" ]] && b_has=1
    [[ -f "$n/test.s" ]] && n_has=1
    if [[ "$(< "$b/exit.status")" != "$(< "$n/exit.status")" ]]; then
        echo STATUS
    elif [[ "$b_has" != "$n_has" ]]; then
        echo OUTPUT
    elif [[ "$b_has" == 1 ]] && ! cmp -s "$b/test.s" "$n/test.s"; then
        echo OUTPUT
    elif ! cmp -s "$b/run.norm.log" "$n/run.norm.log"; then
        echo MESSAGE
    else
        echo SAME
    fi
}

# Prints the first lines of the difference that put the test in class $1.
excerpt() {
    local class=$1 b=$2 n=$3 f=run.norm.log
    [[ "$class" == OUTPUT ]] && f=test.s
    [[ -f "$b/$f" ]] || { echo "(base wrote no $f)"; return; }
    [[ -f "$n/$f" ]] || { echo "(new wrote no $f)"; return; }
    diff "$b/$f" "$n/$f" | head -n "$DIFF_LINES"
}

declare -A count
declare -a flagged=() details=()

# Compares one test: $1 source directory, $2 key in the report.
compare_test() {
    local src=$1 key=$2 class b n golden top
    read_cfg "$src"
    if [[ -n "$cfg_skip" ]]; then
        class=SKIP
    else
        b=$OUT/$BASE_NAME/$key n=$OUT/$NEW_NAME/$key
        run_version "$src" "$b" "$BASE_CMD"
        run_version "$src" "$n" "$NEW_CMD"
        class=$(classify "$b" "$n")
    fi
    count[$class]=$((${count[$class]:-0} + 1))
    [[ "$class" == SAME || "$class" == SKIP ]] && return
    details+=("### $key: $class (base=$(< "$b/exit.status") new=$(< "$n/exit.status"))"
              '```' "$(excerpt "$class" "$b" "$n")" '```' '')
    if [[ "$class" == OUTPUT && -n "$GOLDEN_REV" && -f "$src/test.s.golden" ]]; then
        golden=$(cd "$src" && pwd -P)/test.s.golden
        if ! grep -qxF -- "${golden#"$TOP"/}" <<< "$CHANGED_GOLDENS"; then
            flagged+=("- $key")
        fi
    fi
}

write_report() {
    local c
    {
        echo "# PSSGen comparison: $BASE_NAME vs $NEW_NAME"
        echo
        for c in "${CLASSES[@]}"; do echo "$c: ${count[$c]:-0}"; done
        echo
        if [[ -n "$GOLDEN_REV" ]]; then
            echo "## Output changed but golden unchanged"
            echo
            if [[ ${#flagged[@]} -gt 0 ]]; then printf '%s\n' "${flagged[@]}"; else echo "(none)"; fi
            echo
        fi
        echo "## Differences"
        echo
        if [[ ${#details[@]} -gt 0 ]]; then printf '%s\n' "${details[@]}"; else echo "(none)"; fi
    } > "$OUT/report.md"
}

main() {
    local dir t c
    BASE_CMD="" NEW_CMD="" BASE_NAME="" NEW_NAME="" OUT="" GOLDEN_REV=""
    while [[ $# -gt 0 ]]; do
        case "$1" in
            --base-cmd)   BASE_CMD=${2:?}; shift 2 ;;
            --new-cmd)    NEW_CMD=${2:?}; shift 2 ;;
            --base-name)  BASE_NAME=${2:?}; shift 2 ;;
            --new-name)   NEW_NAME=${2:?}; shift 2 ;;
            --out)        OUT=${2:?}; shift 2 ;;
            --golden-rev) GOLDEN_REV=${2:?}; shift 2 ;;
            -*)           die "unknown option $1" ;;
            *)            break ;;
        esac
    done
    [[ -n "$BASE_CMD" && -n "$NEW_CMD" && -n "$BASE_NAME" && -n "$NEW_NAME" && -n "$OUT" \
        && $# -gt 0 ]] || die "usage: $0 --base-cmd <cmd> --new-cmd <cmd> --base-name <name> --new-name <name> --out <dir> [--golden-rev <rev>] <dir>..."
    [[ "$BASE_NAME" != "$NEW_NAME" ]] || die "--base-name and --new-name must differ"
    for dir in "$@"; do
        [[ -d "$dir" ]] || die "no such directory: $dir"
    done
    if [[ -n "$GOLDEN_REV" ]]; then
        TOP=$(git -C "$(dirname "${BASH_SOURCE[0]}")" rev-parse --show-toplevel) \
            || die "not in a git checkout"
        CHANGED_GOLDENS=$(git -C "$TOP" diff --name-only "$GOLDEN_REV..HEAD") \
            || die "git diff $GOLDEN_REV..HEAD failed"
    fi
    rm -rf "$OUT"
    mkdir -p "$OUT" || die "cannot create $OUT"
    OUT=$(cd "$OUT" && pwd -P)

    for dir in "$@"; do
        dir=$(cd "$dir" && pwd -P)
        if is_test "$dir"; then
            compare_test "$dir" "$(basename "$(dirname "$dir")")/$(basename "$dir")"
        else
            while IFS= read -r t; do
                compare_test "$dir/$t" "$(basename "$dir")/${t#./}"
            done < <(list_tests "$dir")
        fi
    done
    write_report
    for c in "${CLASSES[@]}"; do echo "$c: ${count[$c]:-0}"; done
    echo "Report: $OUT/report.md"
}

main "$@"
