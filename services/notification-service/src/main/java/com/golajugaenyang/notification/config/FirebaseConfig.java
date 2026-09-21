package com.golajugaenyang.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.FileInputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${fcm.credentials-path:}")
    private String credentialsPath;

    /**
     * 로컬 등 FCM 서비스 계정 키가 없는 환경에서는 앱 부팅이 막히지 않도록
     * null을 반환하고, FcmPushSender가 이를 확인해 발송을 건너뛴다.
     */
    @Bean
    public FirebaseApp firebaseApp() {
        if (credentialsPath == null || credentialsPath.isBlank()) {
            log.warn("fcm.credentials-path가 설정되지 않아 FCM 푸시 발송이 비활성화됩니다.");
            return null;
        }
        try (FileInputStream serviceAccount = new FileInputStream(credentialsPath)) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            return FirebaseApp.getApps().isEmpty()
                    ? FirebaseApp.initializeApp(options)
                    : FirebaseApp.getInstance();
        } catch (IOException e) {
            log.warn("FCM 인증 파일을 읽을 수 없어 푸시 발송이 비활성화됩니다: {}", e.getMessage());
            return null;
        }
    }
}
