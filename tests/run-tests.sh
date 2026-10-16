#!/usr/bin/env bash
# Runs PSSGen regression tests and checks their results.
#
# Usage: PSSGEN="<command>" run-tests.sh <dir>...
#        run-tests.sh --clean <dir>...
#
# PSSGEN is the command that starts PSSGen (tests/Makefile sets it for the jar or the
# native executable). Each <dir> is a suite, whose subdirectories hold tests, or a
# single test. A test is a directory that contains test.pss or test.cfg.
#
# PSSGen runs in the test directory with the arguments from test.cfg, by default
# "-info test.pss -o test.s -root pss_top::root_a"; its stdout and stderr go to
# run.log. The test passes when
#   - PSSGen exits with the expected status: by default 0 when test.s.golden exists
#     and 1 when it does not;
#   - run.log contains every "expect" text of test.cfg;
#   - test.s equals test.s.golden, or no test.s is written when there is no golden file.
#
# test.cfg holds "key = value" lines; empty lines and lines starting with # are skipped:
#   args   = <arguments>   PSSGen arguments, split at white space
#   status = <n>           expected exit status
#   expect = <text>        text that run.log must contain; may be repeated
#   skip   = <reason>      do not run the test
#
# For a suite, the result of each test is written to <suite>/summary.log as
# "./<test>: PASS", "./<test>: FAIL" or "./<test>: SKIP". The script exits 1 when a
# test failed and 2 on a usage or test.cfg error.
set -uo pipefail

DEFAULT_ARGS="-info test.pss -o test.s -root pss_top::root_a"
OUTPUTS=(test.s run.log result.log diff.log)

if [[ -t 1 ]]; then
    GREEN=$'\e[32m' RED=$'\e[91m' BLUE=$'\e[34m' RESET=$'\e[00m'
else
    GREEN="" RED="" BLUE="" RESET=""
fi

die() {
    echo "run-tests.sh: $*" >&2
    exit 2
}

is_test() {
    [[ -f "$1/test.pss" || -f "$1/test.cfg" ]]
}

# Test directories of a suite, relative to it, in a stable order.
list_tests() {
    (cd "$1" && find . \( -name test.pss -o -name test.cfg \) -printf '%h\n') | LC_ALL=C sort -u
}

# Reads test.cfg of directory $1 into cfg_args, cfg_status, cfg_expect and cfg_skip.
read_cfg() {
    cfg_args=$DEFAULT_ARGS cfg_status="" cfg_expect=() cfg_skip=""
    local file="$1/test.cfg" line key value n=0
    [[ -f "$file" ]] || return 0
    while IFS= read -r line || [[ -n "$line" ]]; do
        n=$((n + 1))
        [[ "$line" =~ ^[[:space:]]*(#|$) ]] && continue
        [[ "$line" == *=* ]] || die "$file:$n: expected 'key = value'"
        key=${line%%=*}
        value=${line#*=}
        key=${key//[[:space:]]/}
        value=${value#"${value%%[![:space:]]*}"}
        value=${value%"${value##*[![:space:]]}"}
        case "$key" in
            args)   cfg_args=$value ;;
            status) [[ "$value" =~ ^[0-9]+$ ]] || die "$file:$n: status must be a number"
                    cfg_status=$value ;;
            expect) cfg_expect+=("$value") ;;
            skip)   cfg_skip=${value:-skipped} ;;
            *)      die "$file:$n: unknown key '$key'" ;;
        esac
    done < "$file"
}

# Runs the test in directory $1 and prints its result; sets "result" to PASS, FAIL or SKIP.
run_test() {
    local dir=$1 name=$2 rc reason="" text
    local -a cmd args
    read_cfg "$dir"
    if [[ -n "$cfg_skip" ]]; then
        result=SKIP
        echo "$name: ${BLUE}SKIP${RESET} ($cfg_skip)"
        return
    fi
    read -ra cmd <<< "$PSSGEN"
    read -ra args <<< "$cfg_args"
    (cd "$dir" && rm -f "${OUTPUTS[@]}" && "${cmd[@]}" "${args[@]}" > run.log 2>&1)
    rc=$?
    if [[ -z "$cfg_status" ]]; then
        if [[ -f "$dir/test.s.golden" ]]; then cfg_status=0; else cfg_status=1; fi
    fi
    if [[ "$rc" != "$cfg_status" ]]; then
        reason="exit status $rc, expected $cfg_status"
    fi
    for text in "${cfg_expect[@]}"; do
        if [[ -z "$reason" ]] && ! grep -qF -- "$text" "$dir/run.log"; then
            reason="run.log does not contain '$text'"
        fi
    done
    if [[ -z "$reason" ]]; then
        if [[ -f "$dir/test.s.golden" ]]; then
            if [[ ! -f "$dir/test.s" ]]; then
                reason="no test.s written"
            elif ! diff "$dir/test.s.golden" "$dir/test.s" > "$dir/diff.log"; then
                reason="test.s differs from test.s.golden (see diff.log)"
            fi
        elif [[ -f "$dir/test.s" ]]; then
            reason="test.s written, but the test has no test.s.golden"
        fi
    fi
    if [[ -z "$reason" ]]; then
        result=PASS
        echo "PASS" > "$dir/result.log"
        echo "$name: ${GREEN}PASS${RESET}"
    else
        result=FAIL
        echo "FAIL: $reason" > "$dir/result.log"
        echo "$name: ${RED}FAIL${RESET} ($reason)"
        if [[ -s "$dir/diff.log" ]]; then
            head -n 20 "$dir/diff.log" | sed 's/^/    /'
        else
            tail -n 5 "$dir/run.log" | sed 's/^/    /'
        fi
    fi
}

clean() {
    local dir=$1 t
    if is_test "$dir"; then
        (cd "$dir" && rm -f "${OUTPUTS[@]}")
        return
    fi
    rm -f "$dir/summary.log"
    while IFS= read -r t; do
        (cd "$dir/$t" && rm -f "${OUTPUTS[@]}")
    done < <(list_tests "$dir")
}

run_suite() {
    local suite=$1 t pass=0 fail=0 skip=0
    echo "== $suite"
    rm -f "$suite/summary.log"
    while IFS= read -r t; do
        run_test "$suite/$t" "$t"
        echo "$t: $result" >> "$suite/summary.log"
        case "$result" in
            PASS) pass=$((pass + 1)) ;;
            FAIL) fail=$((fail + 1)) ;;
            SKIP) skip=$((skip + 1)) ;;
        esac
    done < <(list_tests "$suite")
    echo "--------------------------------------------------------"
    echo "$suite: TOTAL=$((pass + fail + skip)), PASS=$pass, FAIL=$fail, SKIP=$skip"
    echo "--------------------------------------------------------"
    [[ "$fail" -eq 0 ]]
}

main() {
    local mode=run dir status=0
    if [[ "${1:-}" == "--clean" ]]; then
        mode=clean
        shift
    fi
    [[ $# -gt 0 ]] || die "usage: PSSGEN=<command> $0 <dir>... | $0 --clean <dir>..."
    for dir in "$@"; do
        [[ -d "$dir" ]] || die "no such directory: $dir"
    done
    if [[ "$mode" == clean ]]; then
        for dir in "$@"; do clean "$dir"; done
        return 0
    fi
    [[ -n "${PSSGEN:-}" ]] || die "PSSGEN is not set"
    for dir in "$@"; do
        if is_test "$dir"; then
            run_test "$dir" "$dir"
            [[ "$result" != FAIL ]] || status=1
        else
            run_suite "$dir" || status=1
        fi
    done
    return "$status"
}

main "$@"
