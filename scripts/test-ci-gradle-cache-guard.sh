#!/usr/bin/env bash
# 가짜 kubectl로 Gradle 캐시 대기·삭제 확인·오류 처리를 검증한다.
set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
fixture_dir="$(mktemp -d)"
trap 'rm -rf "${fixture_dir}"' EXIT
mkdir -p "${fixture_dir}/bin"

jq -n '
  def pod($name; $job; $build): {
    metadata: {name: $name, labels: {"petflow.io/ci-build": $build},
               annotations: {"petflow.io/ci-job": $job}},
    spec: {volumes: [{persistentVolumeClaim: {claimName: "sever-ci-gradle-cache"}}]}
  };
  {items: [pod("own"; "sever-ci/dev"; "7"),
           pod("other-branch"; "sever-ci/PR-218"; "7"),
           pod("other-build"; "sever-ci/dev"; "8")]}
' > "${fixture_dir}/all.json"
jq 'del(.items[0])' "${fixture_dir}/all.json" > "${fixture_dir}/others.json"
printf '{"items":[]}\n' > "${fixture_dir}/empty.json"

cat > "${fixture_dir}/bin/kubectl" <<'MOCK'
#!/usr/bin/env bash
set -euo pipefail
while [[ "${1:-}" == --* ]]; do shift; done
if [[ "${SCENARIO}" == forbidden ]]; then
  echo 'Error from server (Forbidden): pods is forbidden' >&2
  exit 1
fi
case "$1 $2" in
  'get pods')
    count="$(cat "${FIXTURE_DIR}/count")"
    count=$(( count + 1 ))
    echo "${count}" > "${FIXTURE_DIR}/count"
    case "${SCENARIO}" in
      free) cat "${FIXTURE_DIR}/empty.json" ;;
      clears)
        if (( count == 1 )); then cat "${FIXTURE_DIR}/all.json"; else cat "${FIXTURE_DIR}/empty.json"; fi
        ;;
      cleanup-clears)
        if (( count < 3 )); then cat "${FIXTURE_DIR}/all.json"; else cat "${FIXTURE_DIR}/others.json"; fi
        ;;
      cleanup-already-deleted)
        if (( count == 1 )); then cat "${FIXTURE_DIR}/all.json"; else cat "${FIXTURE_DIR}/others.json"; fi
        ;;
      other-only) cat "${FIXTURE_DIR}/others.json" ;;
      malformed) printf 'invalid json\n' ;;
      *) cat "${FIXTURE_DIR}/all.json" ;;
    esac
    ;;
  'delete pod')
    echo "$3" >> "${FIXTURE_DIR}/deleted"
    if [[ "${SCENARIO}" == delete-fails ]]; then
      echo 'Error from server (Forbidden): cannot delete pod' >&2
      exit 1
    fi
    if [[ "${SCENARIO}" == cleanup-already-deleted ]]; then
      [[ "$*" == *--ignore-not-found=true* ]] || exit 1
    fi
    ;;
  'get pod')
    if [[ "$*" == *'-o wide'* ]]; then echo 'own Pending node-a'; exit 0; fi
    echo '{"metadata":{"name":"own"},"spec":{"nodeName":"node-a","containers":[{"env":[{"name":"JENKINS_SECRET","value":"test-agent-secret"}]}]},"status":{"phase":"Pending"}}'
    ;;
  'get events') echo 'FailedAttachVolume: Multi-Attach error, node=node-a' ;;
  *) echo "Unexpected kubectl call: $*" >&2; exit 1 ;;
esac
MOCK
cat > "${fixture_dir}/bin/date" <<'MOCK'
#!/usr/bin/env bash
cat "${FIXTURE_DIR}/clock"
MOCK
cat > "${fixture_dir}/bin/sleep" <<'MOCK'
#!/usr/bin/env bash
now="$(cat "${FIXTURE_DIR}/clock")"
echo "$(( now + 3 ))" > "${FIXTURE_DIR}/clock"
MOCK
chmod +x "${fixture_dir}/bin/"*

run_case() {
  local scenario="$1" mode="$2" expected="$3"
  local status=0
  echo 0 > "${fixture_dir}/count"
  echo 0 > "${fixture_dir}/clock"
  : > "${fixture_dir}/deleted"
  PATH="${fixture_dir}/bin:${PATH}" FIXTURE_DIR="${fixture_dir}" SCENARIO="${scenario}" \
    CACHE_GUARD_MODE="${mode}" JOB_NAME=sever-ci/dev BUILD_NUMBER=7 \
    CI_CACHE_GUARD_TIMEOUT_SECONDS=2 bash "${script_dir}/ci-gradle-cache-guard.sh" \
    > "${fixture_dir}/output" 2>&1 || status=$?
  if [[ "${expected}" == success && "${status}" != 0 ]] || \
     [[ "${expected}" == failure && "${status}" == 0 ]]; then
    cat "${fixture_dir}/output"
    echo "FAIL: ${scenario} (${mode}), exit=${status}" >&2
    exit 1
  fi
  echo "PASS: ${scenario} (${mode})"
}

run_case free wait success
run_case clears wait success
[[ ! -s "${fixture_dir}/deleted" ]] # Preflight never deletes another build.
run_case stuck wait failure
[[ ! -s "${fixture_dir}/deleted" ]]
grep -q 'timed out' "${fixture_dir}/output"
grep -q 'Multi-Attach' "${fixture_dir}/output"
if grep -q 'test-agent-secret' "${fixture_dir}/output"; then
  echo 'FAIL: diagnostics exposed an agent secret' >&2
  exit 1
fi
run_case cleanup-clears cleanup success
[[ "$(cat "${fixture_dir}/deleted")" == own ]] # Job and build must both match.
[[ "$(cat "${fixture_dir}/count")" == 3 ]] # Wait through deletion rather than trusting DELETE.
run_case other-only cleanup success
[[ ! -s "${fixture_dir}/deleted" ]]
run_case stuck cleanup failure
grep -q 'timed out' "${fixture_dir}/output"
run_case delete-fails cleanup failure
run_case forbidden wait failure
run_case malformed wait failure
run_case cleanup-already-deleted cleanup success

echo 'All Gradle cache guard tests passed.'
