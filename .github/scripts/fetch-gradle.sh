#!/usr/bin/env bash
# Downloads the Gradle distribution that gradle/wrapper/gradle-wrapper.properties names,
# before the build runs. The wrapper itself tries the download once, and a download from
# services.gradle.org fails now and then; each failed attempt here is retried after a
# pause, so that one failed download does not fail the workflow.
# Usage: fetch-gradle.sh [attempts]   (default 4; FETCH_GRADLE_PAUSE seconds, default 15)
set -euo pipefail

attempts=${1:-4}
pause=${FETCH_GRADLE_PAUSE:-15}
root=$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)

for ((i = 1; i <= attempts; i++)); do
    if "$root/gradlew" --no-daemon --version >/dev/null; then
        exit 0
    fi
    if ((i < attempts)); then
        echo "::warning::Gradle download failed (attempt $i of $attempts); retrying in $((i * pause))s"
        sleep $((i * pause))
    fi
done
echo "::error::Gradle download failed $attempts times"
exit 1
