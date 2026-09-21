package com.golajugaenyang.notification.domain.entity.enums;

/**
 * 타임딜 알림 스케줄러의 발송 시점 구분 (내부 중복 방지용, API로는 노출하지 않음).
 * API에 노출되는 알림 유형은 {@link NotificationDisplayType}을 따른다.
 */
public enum TimeDealTrigger {
    UPCOMING, // 타임딜 시작 10분 전
    START,    // 타임딜 시작
    ONGOING   // 타임딜 시작 30분 후
}
