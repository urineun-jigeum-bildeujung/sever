#!/usr/bin/env bash

set -Eeuo pipefail

if [ "$#" -ne 5 ]; then
  echo "Usage: $0 <kaniko-tar> <image-ref> <agent-version> <agent-sha256> <runtime-user>" >&2
  exit 2
fi

image_tar="$1"
image_ref="$2"
agent_version="$3"
expected_sha256="$4"
expected_user="$5"
agent_path="/app/opentelemetry-javaagent.jar"

if [ ! -s "${image_tar}" ]; then
  echo "[gateway-image-verify] ERROR: Kaniko tar가 없거나 비어 있습니다: ${image_tar}" >&2
  exit 1
fi
if [[ ! "${agent_version}" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  echo "[gateway-image-verify] ERROR: 잘못된 Agent 버전 형식" >&2
  exit 1
fi
if [[ ! "${expected_sha256}" =~ ^[0-9a-f]{64}$ ]]; then
  echo "[gateway-image-verify] ERROR: 잘못된 SHA256 형식" >&2
  exit 1
fi
if [[ ! "${expected_user}" =~ ^[a-z_][a-z0-9_-]*$ ]]; then
  echo "[gateway-image-verify] ERROR: 잘못된 런타임 사용자 형식" >&2
  exit 1
fi

work_dir="$(mktemp -d "${WORKSPACE:-.}/.gateway-image-verify.XXXXXX")"
archive_dir="${work_dir}/archive"
rootfs_dir="${work_dir}/rootfs"
trap 'rm -rf -- "${work_dir}"' EXIT
mkdir -p "${archive_dir}" "${rootfs_dir}"

tar_sha256="$(sha256sum "${image_tar}" | awk '{print $1}')"
echo "[gateway-image-verify] imageRef=${image_ref}"
echo "[gateway-image-verify] kanikoTar=${image_tar}"
echo "[gateway-image-verify] kanikoTarSha256=${tar_sha256}"
echo "[gateway-image-verify] agentVersion=${agent_version}"

tar -xf "${image_tar}" -C "${archive_dir}"
manifest="${archive_dir}/manifest.json"
if [ ! -s "${manifest}" ]; then
  echo "[gateway-image-verify] ERROR: Docker archive manifest.json이 없습니다." >&2
  exit 1
fi
if ! grep -Fq "\"$image_ref\"" "${manifest}"; then
  echo "[gateway-image-verify] ERROR: tar RepoTags와 검증 대상 imageRef가 다릅니다." >&2
  exit 1
fi

config_name="$(grep -oE '"Config":"sha256:[0-9a-f]{64}"' "${manifest}" | head -n 1 | cut -d '"' -f 4)"
if [[ ! "${config_name}" =~ ^sha256:[0-9a-f]{64}$ ]] || [ ! -s "${archive_dir}/${config_name}" ]; then
  echo "[gateway-image-verify] ERROR: 이미지 config를 찾을 수 없습니다." >&2
  exit 1
fi

mapfile -t layers < <(grep -oE '"[0-9a-f]{64}\.tar\.gz"' "${manifest}" | tr -d '"')
if [ "${#layers[@]}" -eq 0 ]; then
  echo "[gateway-image-verify] ERROR: 이미지 레이어를 찾을 수 없습니다." >&2
  exit 1
fi

image_config="${archive_dir}/${config_name}"
runtime_user="$(grep -oE '"User"[[:space:]]*:[[:space:]]*"[^"]*"' "${image_config}" | head -n 1 | sed -E 's/^.*:[[:space:]]*"([^"]*)"$/\1/')"
if [ "${runtime_user}" != "${expected_user}" ]; then
  echo "[gateway-image-verify] ERROR: 기본 실행 사용자가 다릅니다: actual=${runtime_user}, expected=${expected_user}" >&2
  exit 1
fi
if ! grep -Fq "\"-javaagent:${agent_path}\"" "${image_config}"; then
  echo "[gateway-image-verify] ERROR: 이미지 ENTRYPOINT가 ${agent_path}를 참조하지 않습니다." >&2
  exit 1
fi

for layer in "${layers[@]}"; do
  if [[ ! "${layer}" =~ ^[0-9a-f]{64}\.tar\.gz$ ]] || [ ! -s "${archive_dir}/${layer}" ]; then
    echo "[gateway-image-verify] ERROR: 잘못되었거나 누락된 레이어: ${layer}" >&2
    exit 1
  fi
  layer_listing="${work_dir}/layer-entries"
  tar -tzf "${archive_dir}/${layer}" > "${layer_listing}"
  if grep -qE '(^|/)\.wh\.' "${layer_listing}"; then
    echo "[gateway-image-verify] ERROR: 지원하지 않는 whiteout 레이어가 있어 최종 filesystem을 증명할 수 없습니다: ${layer}" >&2
    exit 1
  fi
  tar -xzf "${archive_dir}/${layer}" -C "${rootfs_dir}"
done

agent_file="${rootfs_dir}${agent_path}"
if [ ! -s "${agent_file}" ]; then
  echo "[gateway-image-verify] ERROR: 최종 filesystem에 Agent가 없거나 비어 있습니다: ${agent_path}" >&2
  exit 1
fi

actual_sha256="$(sha256sum "${agent_file}" | awk '{print $1}')"
if [ "${actual_sha256}" != "${expected_sha256}" ]; then
  echo "[gateway-image-verify] ERROR: Agent SHA256이 다릅니다: actual=${actual_sha256}, expected=${expected_sha256}" >&2
  exit 1
fi

passwd_entry="$(grep -E "^${expected_user}:" "${rootfs_dir}/etc/passwd" | head -n 1)"
if [ -z "${passwd_entry}" ]; then
  echo "[gateway-image-verify] ERROR: 최종 filesystem에서 ${expected_user}를 찾을 수 없습니다." >&2
  exit 1
fi
IFS=: read -r _ _ runtime_uid runtime_gid _ <<< "${passwd_entry}"
if [[ ! "${runtime_uid}" =~ ^[0-9]+$ ]] || [[ ! "${runtime_gid}" =~ ^[0-9]+$ ]]; then
  echo "[gateway-image-verify] ERROR: 런타임 UID/GID를 확인할 수 없습니다." >&2
  exit 1
fi

echo "[gateway-image-verify] runtimeUser=${runtime_user} uid=${runtime_uid} gid=${runtime_gid}"
echo "[gateway-image-verify] agentPath=${agent_path} agentSha256=${actual_sha256}"

OTEL_TRACES_EXPORTER=none \
OTEL_METRICS_EXPORTER=none \
OTEL_LOGS_EXPORTER=none \
LD_LIBRARY_PATH=/opt/java/openjdk/lib:/opt/java/openjdk/lib/server \
  chroot --userspec="${runtime_uid}:${runtime_gid}" "${rootfs_dir}" \
    /bin/sh -eu -c \
    "test -s '${agent_path}' && test -r '${agent_path}' && echo '${expected_sha256}  ${agent_path}' | sha256sum -c - && exec /opt/java/openjdk/bin/java -javaagent:${agent_path} -version"

echo "[gateway-image-verify] javaAgentExitCode=0"
