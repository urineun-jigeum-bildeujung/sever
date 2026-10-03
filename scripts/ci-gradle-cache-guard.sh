#!/usr/bin/env bash
# 공용 잠금 안에서 Gradle 캐시 Pod의 이전 사용자와 이번 빌드의 삭제 완료를 확인한다.
set -euo pipefail

mode="${CACHE_GUARD_MODE:-wait}"
namespace="${JENKINS_NAMESPACE:-jenkins}"
claim="sever-ci-gradle-cache"

case "${mode}" in
  wait) default_timeout=900 ;;
  cleanup)
    : "${JOB_NAME:?JOB_NAME is required for cleanup}"
    : "${BUILD_NUMBER:?BUILD_NUMBER is required for cleanup}"
    default_timeout=300
    ;;
  *) echo "Unknown cache guard mode: ${mode}" >&2; exit 1 ;;
esac

timeout_seconds="${CI_CACHE_GUARD_TIMEOUT_SECONDS:-${default_timeout}}"
if [[ ! "${timeout_seconds}" =~ ^[0-9]+$ ]]; then
  echo "Invalid cache guard timeout: ${timeout_seconds}" >&2
  exit 1
fi

# kubectl은 기본 kubeconfig가 없으면 localhost:8080을 사용하므로 접속 정보를 명시한다.
# tokenFile로 projected 토큰을 읽게 해서 비밀값을 명령 인자나 로그에 넣지 않는다.
service_account_dir="${CI_CACHE_GUARD_SERVICE_ACCOUNT_DIR:-/var/run/secrets/kubernetes.io/serviceaccount}"
cache_guard_kubeconfig="$(mktemp)"
trap 'rm -f "${cache_guard_kubeconfig}"' EXIT
cat > "${cache_guard_kubeconfig}" <<CONFIG
apiVersion: v1
kind: Config
clusters:
  - name: in-cluster
    cluster:
      server: https://kubernetes.default.svc
      certificate-authority: ${service_account_dir}/ca.crt
users:
  - name: cache-guard
    user:
      tokenFile: ${service_account_dir}/token
contexts:
  - name: cache-guard
    context:
      cluster: in-cluster
      user: cache-guard
current-context: cache-guard
CONFIG

kube() {
  kubectl --kubeconfig="${cache_guard_kubeconfig}" --namespace="${namespace}" --request-timeout=20s "$@"
}

cache_pods() {
  # API 실패나 JSON 파싱 실패는 'Pod 없음'으로 취급하지 않는다.
  kube get pods -o json | jq -r --arg claim "${claim}" --arg mode "${mode}" \
    --arg job "${JOB_NAME:-}" --arg build "${BUILD_NUMBER:-}" '
    .items[]
    | select(any(.spec.volumes[]?; .persistentVolumeClaim.claimName == $claim))
    | select($mode == "wait" or
        (.metadata.annotations["petflow.io/ci-job"] == $job and
         .metadata.labels["petflow.io/ci-build"] == $build))
    | .metadata.name'
}

diagnostics() {
  local pod_name
  while IFS= read -r pod_name; do
    [[ -n "${pod_name}" ]] || continue
    echo "Cache Pod ${pod_name} (PVC=${claim})"
    # Pod이 이미 삭제됐을 수 있다. 진단 실패가 본래 오류를 가리지 않도록 한다.
    kube get pod "${pod_name}" --ignore-not-found -o wide || true
    # describe pod는 에이전트 연결 비밀값까지 출력할 수 있어 상태·볼륨·이벤트만 기록한다.
    kube get pod "${pod_name}" --ignore-not-found -o json | jq '
      {phase: .status.phase, node: .spec.nodeName, deleting: .metadata.deletionTimestamp,
       conditions: .status.conditions,
       containers: [.status.containerStatuses[]? | {name, ready, state}],
       volumes: [.spec.volumes[]? | select(.persistentVolumeClaim)]}' || true
    kube get events --field-selector="involvedObject.name=${pod_name}" \
      --sort-by=.lastTimestamp || true
  done <<< "$1"
}

if [[ "${mode}" == cleanup ]]; then
  pods="$(cache_pods)"
  if [[ -n "${pods}" ]]; then
    diagnostics "${pods}"
    while IFS= read -r pod_name; do
      echo "Deleting this build cache Pod: ${pod_name}"
      kube delete pod "${pod_name}" --ignore-not-found=true --wait=false
    done <<< "${pods}"
  fi
fi

deadline=$(( $(date +%s) + timeout_seconds ))
next_report=0
while true; do
  pods="$(cache_pods)"
  if [[ -z "${pods}" ]]; then
    echo "Gradle cache guard ${mode} complete: no matching cache Pods remain"
    break
  fi
  now="$(date +%s)"
  if (( now >= next_report )); then
    diagnostics "${pods}"
    next_report=$(( now + 30 ))
  fi
  if (( now >= deadline )); then
    echo "Gradle cache guard ${mode} timed out after ${timeout_seconds}s; PVC ${claim} still has Pods: ${pods}" >&2
    exit 1
  fi
  sleep 3
done
