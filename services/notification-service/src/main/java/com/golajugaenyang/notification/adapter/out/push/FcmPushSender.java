package com.golajugaenyang.notification.adapter.out.push;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmPushSender {

    private final FirebaseApp firebaseApp;

    public void send(String token, String title, String body, String deepLink) {
        if (firebaseApp == null) {
            log.debug("FCM 미설정으로 푸시 발송을 건너뜁니다. token={}", token);
            return;
        }

        Message message = Message.builder()
                .setToken(token)
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putData("deepLink", deepLink == null ? "" : deepLink)
                .build();

        try {
            FirebaseMessaging.getInstance(firebaseApp).send(message);
        } catch (FirebaseMessagingException e) {
            log.warn("FCM 푸시 발송 실패: token={}, error={}", token, e.getMessage());
        }
    }
}
