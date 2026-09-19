package com.golajugaenyang.common.storage;

import java.time.Duration;
import java.util.UUID;
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

    private final S3Presigner presigner;

    @Value("${storage.bucket}")
    private String bucket;

    @Value("${storage.cloudfront-domain}")
    private String cloudfrontDomain;

    /**
     * prefix(예: "profiles", "reviews") 하위에 {ownerId}/{uuid}.{extension} 형태의 키로
     * 업로드용 presigned PUT URL을 발급한다. 서명에 status=pending 태그 조건이 포함되므로,
     * 클라이언트는 실제 PUT 요청에도 동일한 x-amz-tagging: status=pending 헤더를 보내야 한다.
     */
    public PresignedUpload issue(String prefix, String ownerId, String extension) {
        String key = "%s/%s/%s.%s".formatted(prefix, ownerId, UUID.randomUUID(), extension);

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
}
