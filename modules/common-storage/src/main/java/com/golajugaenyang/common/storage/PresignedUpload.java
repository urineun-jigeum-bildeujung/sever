package com.golajugaenyang.common.storage;

public record PresignedUpload(
        String uploadUrl,
        String fileUrl
) {
}
