package com.golajugaenyang.notification.adapter.out.push;

import com.golajugaenyang.notification.domain.repository.FcmTokenRepository;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmPushSender {

    private final ObjectProvider<FirebaseApp> firebaseAppProvider;
    private final FcmTokenRepository fcmTokenRepository;

    public void send(String token, String title, String body, String targetType, String targetId) {
        FirebaseApp firebaseApp = firebaseAppProvider.getIfAvailable();
        if (firebaseApp == null) {
            log.debug("FCM 미설정으로 푸시 발송을 건너뜁니다. token={}", mask(token));
            return;
        }

        Message.Builder builder = Message.builder()
                .setToken(token)
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build());
        if (targetType != null) {
            builder.putData("targetType", targetType);
        }
        if (targetId != null) {
            builder.putData("targetId", targetId);
        }

        try {
            FirebaseMessaging.getInstance(firebaseApp).send(builder.build());
        } catch (FirebaseMessagingException e) {
            log.warn("FCM 푸시 발송 실패: token={}, error={}", mask(token), e.getMessage());
            if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                fcmTokenRepository.deleteByToken(token);
            }
        }
    }

    private String mask(String token) {
        if (token == null || token.length() <= 6) {
            return "***";
        }
        return "***" + token.substring(token.length() - 6);
    }
}
