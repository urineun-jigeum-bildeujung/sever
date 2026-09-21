package com.golajugaenyang.notification.adapter.in.web.dto.response;

import com.golajugaenyang.notification.domain.entity.enums.NotificationCategory;

public record SubscriptionResponse(
        NotificationCategory category,
        boolean subscribed
) {

}
