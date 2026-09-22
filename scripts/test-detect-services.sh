#!/usr/bin/env bash

set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
script_under_test="${script_dir}/detect-services.sh"
fixture_dir="$(mktemp -d)"
trap 'rm -rf "${fixture_dir}"' EXIT

fail() {
  echo "FAIL: $*" >&2
  exit 1
}

assert_equals() {
  local expected="$1"
  local actual="$2"
  local description="$3"
  if [ "${actual}" != "${expected}" ]; then
    fail "${description}: expected=${expected}, actual=${actual}"
  fi
  echo "PASS: ${description}"
}

mkdir -p "${fixture_dir}/scripts" "${fixture_dir}/services/auth-service" "${fixture_dir}/services/member-service" "${fixture_dir}/platform/api-gateway" "${fixture_dir}/modules/common-core" "${fixture_dir}/docs"
cp "${script_under_test}" "${fixture_dir}/scripts/detect-services.sh"

(
  cd "${fixture_dir}"
  git init -q
  git config user.email "gateway-test@petflow.local"
  git config user.name "gateway-detect-test"
  touch services/auth-service/.keep services/member-service/.keep platform/api-gateway/.keep modules/common-core/.keep docs/.keep
  git add .
  git commit -qm "fixture baseline"
)

run_changed_file_case() {
  local path="$1"
  local expected="$2"
  local description="$3"
  local base_sha
  local actual

  base_sha="$(git -C "${fixture_dir}" rev-parse HEAD)"
  mkdir -p "${fixture_dir}/$(dirname "${path}")"
  printf 'changed\n' > "${fixture_dir}/${path}"
  git -C "${fixture_dir}" add "${path}"
  git -C "${fixture_dir}" commit -qm "change ${path}"

  actual="$(
    cd "${fixture_dir}"
    EVENT_NAME=push BASE_SHA="${base_sha}" REQUESTED_IMAGE=all bash scripts/detect-services.sh
  )"
  assert_equals "${expected}" "${actual}" "${description}"
}

run_changed_file_case "platform/api-gateway/src/main/resources/application.yml" '["api-gateway"]' "Gateway 변경은 api-gateway만 감지"

run_changed_file_case "services/auth-service/src/main/resources/application.yml" '["auth-service"]' "기존 서비스 변경 감지 유지"

run_changed_file_case "modules/common-core/src/main/java/Common.java" '["api-gateway","auth-service","member-service"]' "공통 모듈 변경은 전체 이미지 감지"
run_changed_file_case ".dockerignore" '["api-gateway","auth-service","member-service"]' ".dockerignore 변경은 전체 이미지 감지"

run_changed_file_case "docs/gateway.md" '[]' "문서 변경은 이미지 빌드 생략"

manual_gateway="$(
  cd "${fixture_dir}"
  EVENT_NAME=workflow_dispatch BASE_SHA= REQUESTED_IMAGE=api-gateway bash scripts/detect-services.sh
)"
assert_equals '["api-gateway"]' "${manual_gateway}" "수동 api-gateway 허용"

manual_all="$(
  cd "${fixture_dir}"
  EVENT_NAME=workflow_dispatch BASE_SHA= REQUESTED_IMAGE=all bash scripts/detect-services.sh
)"
assert_equals '["api-gateway","auth-service","member-service"]' "${manual_all}" "수동 all에 Gateway 포함"

if (
  cd "${fixture_dir}"
  EVENT_NAME=workflow_dispatch BASE_SHA= REQUESTED_IMAGE='api-gateway;echo unsafe' bash scripts/detect-services.sh >/dev/null 2>&1
); then
  fail "잘못된 서비스명과 쉘 특수문자를 거부해야 함"
fi
echo "PASS: 잘못된 서비스명과 쉘 특수문자 거부"
