package com.golajugaenyang.notification.adapter.in.internal;

import com.golajugaenyang.notification.adapter.in.internal.dto.NoticeCreateRequest;
import com.golajugaenyang.notification.application.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자(운영자)가 공지를 등록하면 FCM 토큰을 등록한 모든 회원에게 발송한다.
 * 별도 관리자 인증 체계가 없어, 게이트웨이의 내부 시크릿 검증(X-Internal-Secret)에
 * 의존하는 내부 API로 우선 제공한다.
 */
@RestController
@RequestMapping("/internal/notifications/notices")
@RequiredArgsConstructor
public class NoticeInternalController {

    private final NoticeService noticeService;

    @PostMapping
    public ResponseEntity<Void> createNotice(@Valid @RequestBody NoticeCreateRequest request) {
        noticeService.broadcastNotice(request.title(), request.body());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
