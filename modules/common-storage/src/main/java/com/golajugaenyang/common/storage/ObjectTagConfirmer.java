package com.golajugaenyang.common.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.services.s3.model.Tagging;

@RequiredArgsConstructor
public class ObjectTagConfirmer {

    private final S3Client s3Client;

    @Value("${storage.bucket}")
    private String bucket;

    @Value("${storage.cloudfront-domain}")
    private String cloudfrontDomain;

    /**
     * fileUrl의 키가 실제로 expectedOwnerId 소유인지만 로컬에서 검증한다(S3 호출 없음).
     * DB에 저장하기 전, 트랜잭션 커밋 여부와 무관하게 즉시 걸러내야 하는 시점에 사용한다.
     */
    public void validateOwnership(String fileUrl, String expectedOwnerId) {
        validateOwner(toKey(fileUrl), expectedOwnerId);
    }

    /**
     * DB에 저장한 fileUrl(CloudFront 조회 주소)을 받아서, 해당 S3 객체의 태그를
     * status=pending에서 status=confirmed로 바꾼다. confirmed로 바뀐 객체는 Lifecycle
     * 삭제 대상에서 제외된다.
     *
     * S3 호출은 되돌릴 수 없으므로, DB 트랜잭션 커밋이 확정된 뒤에만 호출해야 한다 —
     * 커밋 전에 호출했다가 이후 커밋이 실패하면, DB에는 반영 안 됐는데 S3 객체만
     * confirmed로 남아 Lifecycle 정리 대상에서 영원히 제외되는 고아 객체가 생긴다.
     */
    public void confirm(String fileUrl, String expectedOwnerId) {
        String key = toKey(fileUrl);
        validateOwner(key, expectedOwnerId);

        s3Client.putObjectTagging(PutObjectTaggingRequest.builder()
                .bucket(bucket)
                .key(key)
                .tagging(Tagging.builder()
                        .tagSet(Tag.builder().key("status").value("confirmed").build())
                        .build())
                .build());
    }

    private String toKey(String fileUrl) {
        String prefix = cloudfrontDomain + "/";
        if (!fileUrl.startsWith(prefix)) {
            throw new IllegalArgumentException("cloudfront 조회 주소가 아닙니다: " + fileUrl);
        }
        return fileUrl.substring(prefix.length());
    }

    private void validateOwner(String key, String expectedOwnerId) {
        String[] segments = key.split("/", 3);
        if (segments.length < 2 || !segments[1].equals(expectedOwnerId)) {
            throw new IllegalArgumentException("이 파일에 대한 권한이 없습니다: " + key);
        }
    }
}
