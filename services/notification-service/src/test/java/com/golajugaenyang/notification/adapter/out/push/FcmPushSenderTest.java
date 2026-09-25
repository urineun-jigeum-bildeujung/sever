package com.golajugaenyang.notification.adapter.out.push;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.golajugaenyang.notification.domain.repository.FcmTokenRepository;
import com.google.firebase.FirebaseApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

class FcmPushSenderTest {

    @Test
    void skipsSendingWhenFirebaseIsNotConfigured() {
        @SuppressWarnings("unchecked")
        ObjectProvider<FirebaseApp> firebaseAppProvider = mock(ObjectProvider.class);
        FcmTokenRepository fcmTokenRepository = mock(FcmTokenRepository.class);
        when(firebaseAppProvider.getIfAvailable()).thenReturn(null);

        FcmPushSender sender = new FcmPushSender(firebaseAppProvider, fcmTokenRepository);

        sender.send("test-token", "title", "body", "TIMEDEAL", "1");

        verifyNoInteractions(fcmTokenRepository);
    }
}
