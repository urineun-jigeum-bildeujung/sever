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
     * DB에 저장할 fileUrl(CloudFront 조회 주소)을 받아서, 해당 S3 객체의 태그를
     * status=pending에서 status=confirmed로 바꾼다. confirmed로 바뀐 객체는 Lifecycle
     * 삭제 대상에서 제외된다.
     *
     * fileUrl은 클라이언트가 요청 바디에 실어 보내는 값이라 임의로 조작될 수 있으므로,
     * 키의 ownerId 세그먼트가 호출자가 실제로 발급받은 expectedOwnerId와 일치하는지
     * 반드시 검증한다. 이 검증 없이는 다른 사람의 pending 객체를 임의로 confirmed로
     * 바꿔버릴 수 있다
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
