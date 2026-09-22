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

# Detect Services 스테이지는 Jenkins 기본(jnlp) 컨테이너에서 도는데 jq가 없어서
# (2026-09-10 실제로 겪음), jq 없이 순수 bash로 JSON 배열을 만든다. 서비스
# 디렉토리명은 영숫자+하이픈뿐이라 별도 이스케이프 없이 안전하게 처리 가능.
to_json_array() {
  local first=true
  printf '['
  while IFS= read -r line; do
    [ -z "${line}" ] && continue
    if [ "${first}" = true ]; then first=false; else printf ','; fi
    printf '"%s"' "${line}"
  done
  printf ']'
}

all_services="$(find services -mindepth 1 -maxdepth 1 -type d -printf '%f\n' | sort)"
all_images="$(printf '%s\n%s\n' "${all_services}" "api-gateway" | sort)"

if [ "${EVENT_NAME}" = "workflow_dispatch" ]; then
  if [ "${REQUESTED_IMAGE}" = "all" ]; then
    services="$(printf '%s\n' "${all_images}" | to_json_array)"
  else
    # REQUESTED_IMAGE는 Jenkins 빌드 파라미터로 사람이 직접 입력하는 값이라 신뢰 못 함 —
    # 실제 서비스 디렉토리명과 정확히 일치할 때만 받아들이고, 그 외(오타, 따옴표/개행 등
    # 이상한 값)는 바로 실패시켜서 이후 readJSON/셸 단계로 안 넘어가게 막는다
    # (2026-09-13 CodeRabbit 리뷰로 발견).
    if ! printf '%s\n' "${all_images}" | grep -Fxq -- "${REQUESTED_IMAGE}"; then
      echo "알 수 없는 서비스명: ${REQUESTED_IMAGE}" >&2
      exit 2
    fi
    services="$(printf '%s\n' "${REQUESTED_IMAGE}" | to_json_array)"
  fi
else
  changed_files="$(mktemp)"
  trap 'rm -f "${changed_files}"' EXIT
  git diff --name-only "${BASE_SHA}" HEAD > "${changed_files}"

  if grep -qE '^(modules/|build\.gradle$|settings\.gradle$|gradle\.properties$|gradle/|gradlew$|\.dockerignore$|Jenkinsfile$|scripts/detect-services\.sh$)' "${changed_files}"; then
    services="$(printf '%s\n' "${all_images}" | to_json_array)"
  else
    # grep -oE는 매치가 없으면 exit 1 -> pipefail 때문에 to_json_array가 이미
    # "[]"를 정상 출력했어도 전체 파이프가 실패로 잡힘. ||를 $(...) 안에 두면
    # 이미 캡처된 출력에 폴백 출력이 덧붙어 "[][]"가 되므로 바깥에서 처리한다.
    detected_images="$(grep -oE '^services/[^/]+' "${changed_files}" | sed 's|services/||' | sort -u)" || detected_images=''
    if grep -qE '^platform/api-gateway/' "${changed_files}"; then
      detected_images="$(printf '%s\n%s\n' "${detected_images}" "api-gateway" | sed '/^$/d' | sort -u)"
    fi
    services="$(printf '%s\n' "${detected_images}" | to_json_array)"
  fi
fi

echo "${services}"
