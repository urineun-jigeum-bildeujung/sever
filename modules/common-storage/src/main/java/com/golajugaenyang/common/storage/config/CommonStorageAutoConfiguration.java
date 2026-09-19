package com.golajugaenyang.common.storage.config;

import com.golajugaenyang.common.storage.ObjectTagConfirmer;
import com.golajugaenyang.common.storage.PresignedUploadIssuer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@AutoConfiguration
public class CommonStorageAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public S3Client s3Client(@Value("${storage.region}") String region) {
        return S3Client.builder()
                .region(Region.of(region))
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public S3Presigner s3Presigner(@Value("${storage.region}") String region) {
        return S3Presigner.builder()
                .region(Region.of(region))
                .build();
    }

    @Bean
    public PresignedUploadIssuer presignedUploadIssuer(S3Presigner s3Presigner) {
        return new PresignedUploadIssuer(s3Presigner);
    }

    @Bean
    public ObjectTagConfirmer objectTagConfirmer(S3Client s3Client) {
        return new ObjectTagConfirmer(s3Client);
    }
}
