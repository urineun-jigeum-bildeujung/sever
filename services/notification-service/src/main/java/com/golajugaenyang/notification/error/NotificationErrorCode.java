package com.golajugaenyang.notification.error;

import com.golajugaenyang.common.core.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

    NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "NOTIFICATION_404_NOT_FOUND", "알림을 찾을 수 없습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
