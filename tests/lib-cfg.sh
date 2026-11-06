# Parsing of test.cfg, shared by run-tests.sh and compare-versions.sh. The sourcing
# script defines die(), which read_cfg calls on a malformed file.

DEFAULT_ARGS="-info test.pss -o test.s -root pss_top::root_a"

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
