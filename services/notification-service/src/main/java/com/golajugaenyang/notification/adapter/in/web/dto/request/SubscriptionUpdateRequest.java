package com.golajugaenyang.notification.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotNull;

public record SubscriptionUpdateRequest(
        @NotNull Boolean subscribed
) {

}
