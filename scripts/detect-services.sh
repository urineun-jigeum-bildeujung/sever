#!/usr/bin/env bash
# 변경된(또는 요청된) 서비스 목록을 JSON 배열로 stdout에 출력한다.
# GitHub Actions 컨텍스트를 직접 참조하지 않고, 호출하는 워크플로우가 아래 3개
# 환경변수를 넘겨주는 방식으로 동작한다 (build.yml/test.yml 등에서 공용으로 재사용).
#
# 입력 (환경변수):
#   EVENT_NAME       - 예: "workflow_dispatch" 또는 "pull_request"
#   BASE_SHA         - PR의 base 커밋 SHA (workflow_dispatch일 때는 안 씀)
#   REQUESTED_IMAGE  - workflow_dispatch 입력값 ("all" 또는 서비스 이름 하나)
#
# 출력: JSON 배열 (예: ["order-service"] 또는 ["auth-service","order-service"])

set -euo pipefail

all_services="$(find services -mindepth 1 -maxdepth 1 -type d -printf '%f\n' | sort)"

if [ "${EVENT_NAME}" = "workflow_dispatch" ]; then
  if [ "${REQUESTED_IMAGE}" = "all" ]; then
    services="$(echo "${all_services}" | jq -R -s -c 'split("\n") | map(select(length > 0))')"
  else
    services="$(jq -nc --arg s "${REQUESTED_IMAGE}" '[$s]')"
  fi
else
  changed_files="$(mktemp)"
  trap 'rm -f "${changed_files}"' EXIT
  git diff --name-only "${BASE_SHA}" HEAD > "${changed_files}"

  if grep -qE '^(modules/|build\.gradle$|settings\.gradle$|gradle\.properties$)' "${changed_files}"; then
    services="$(echo "${all_services}" | jq -R -s -c 'split("\n") | map(select(length > 0))')"
  else
    services="$(grep -oE '^services/[^/]+' "${changed_files}" | sed 's|services/||' | sort -u | jq -R -s -c 'split("\n") | map(select(length > 0))' || echo '[]')"
  fi
fi

echo "${services}"
