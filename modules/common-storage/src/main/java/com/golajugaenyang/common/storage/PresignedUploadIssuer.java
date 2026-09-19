package com.golajugaenyang.common.storage;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@RequiredArgsConstructor
public class PresignedUploadIssuer {

    private static final String PENDING_TAG = "status=pending";
    private static final Duration SIGNATURE_DURATION = Duration.ofMinutes(10);

    private static final Pattern SAFE_OWNER_ID = Pattern.compile("^[A-Za-z0-9_-]+$");
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");

    private final S3Presigner presigner;

    @Value("${storage.bucket}")
    private String bucket;

    @Value("${storage.cloudfront-domain}")
    private String cloudfrontDomain;

    @Value("${storage.prefix}")
    private String prefix;

    /**
     * {storage.prefix}/{ownerId}/{uuid}.{extension} 형태의 키로 업로드용 presigned PUT URL을
     * 발급한다. 서명에 status=pending 태그 조건이 포함되므로, 클라이언트는 실제 PUT 요청에도
     * 동일한 x-amz-tagging: status=pending 헤더를 보내야 한다.
     */
    public PresignedUpload issue(String ownerId, String extension) {
        validateOwnerId(ownerId);
        String normalizedExtension = validateExtension(extension);

        String key = "%s/%s/%s.%s".formatted(prefix, ownerId, UUID.randomUUID(), normalizedExtension);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .tagging(PENDING_TAG)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(SIGNATURE_DURATION)
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presigned = presigner.presignPutObject(presignRequest);

        String fileUrl = cloudfrontDomain + "/" + key;
        return new PresignedUpload(presigned.url().toString(), fileUrl);
    }

    private void validateOwnerId(String ownerId) {
        if (ownerId == null || !SAFE_OWNER_ID.matcher(ownerId).matches()) {
            throw new IllegalArgumentException("허용되지 않는 ownerId 값입니다: " + ownerId);
        }
    }

    private String validateExtension(String extension) {
        String normalized = extension == null ? "" : extension.toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(normalized)) {
            throw new IllegalArgumentException("허용되지 않는 확장자입니다: " + extension);
        }
        return normalized;
    }
}
