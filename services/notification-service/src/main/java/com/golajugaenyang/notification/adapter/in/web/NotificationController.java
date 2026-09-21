package com.golajugaenyang.notification.adapter.in.web;

import com.golajugaenyang.common.security.annotation.MemberId;
import com.golajugaenyang.notification.adapter.in.web.dto.request.FcmTokenRegisterRequest;
import com.golajugaenyang.notification.adapter.in.web.dto.request.SubscriptionUpdateRequest;
import com.golajugaenyang.notification.adapter.in.web.dto.response.NotificationListResponse;
import com.golajugaenyang.notification.adapter.in.web.dto.response.SubscriptionResponse;
import com.golajugaenyang.notification.application.NotificationService;
import com.golajugaenyang.notification.domain.entity.enums.NotificationCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Validated
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/fcm-tokens")
    public ResponseEntity<Void> registerFcmToken(
            @MemberId Long memberId,
            @Valid @RequestBody FcmTokenRegisterRequest request
    ) {
        notificationService.registerFcmToken(memberId, request.token());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/subscriptions/{category}")
    public ResponseEntity<SubscriptionResponse> updateSubscription(
            @MemberId Long memberId,
            @PathVariable NotificationCategory category,
            @Valid @RequestBody SubscriptionUpdateRequest request
    ) {
        notificationService.updateSubscription(memberId, category, request.subscribed());
        return ResponseEntity.ok(new SubscriptionResponse(category, request.subscribed()));
    }

    @GetMapping("/subscriptions/{category}")
    public ResponseEntity<SubscriptionResponse> getSubscription(
            @MemberId Long memberId,
            @PathVariable NotificationCategory category
    ) {
        boolean subscribed = notificationService.getSubscription(memberId, category);
        return ResponseEntity.ok(new SubscriptionResponse(category, subscribed));
    }

    @GetMapping
    public ResponseEntity<NotificationListResponse> getMyNotifications(
            @MemberId Long memberId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size
    ) {
        return ResponseEntity.ok(
                NotificationListResponse.from(notificationService.getMyNotifications(memberId, page, size)));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @MemberId Long memberId,
            @PathVariable Long notificationId
    ) {
        notificationService.markAsRead(memberId, notificationId);
        return ResponseEntity.noContent().build();
    }
}
