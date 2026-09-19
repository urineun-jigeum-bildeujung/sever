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
     * DB에 저장한 fileUrl(CloudFront 조회 주소)을 그대로 받아서, 해당 S3 객체의 태그를
     * status=pending에서 status=confirmed로 바꾼다. confirmed로 바뀐 객체는 Lifecycle
     * 삭제 대상에서 제외된다.
     */
    public void confirm(String fileUrl) {
        String key = toKey(fileUrl);

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
}
