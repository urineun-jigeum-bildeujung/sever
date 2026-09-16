package com.golajugaenyang.order.adapter.out.external.inventory;


import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.order.adapter.out.external.inventory.dto.InventoryErrorResponse;
import com.golajugaenyang.order.adapter.out.external.inventory.client.InventoryInternalApiClient;
import com.golajugaenyang.order.adapter.out.external.inventory.dto.ReserveBulkRequest;
import com.golajugaenyang.order.application.order.port.out.InventoryReservationPort;
import com.golajugaenyang.order.application.order.port.out.dto.ReservationItem;
import com.golajugaenyang.order.error.OrderErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryReservationAdapter implements InventoryReservationPort {

    private static final String INSUFFICIENT_STOCK = "PRODUCT_409_INSUFFICIENT_STOCK";
    private static final String STOCK_MOVEMENT_CONFLICT = "PRODUCT_409_STOCK_MOVEMENT_CONFLICT";

    private final InventoryInternalApiClient inventoryInternalApiClient;
    private final ObjectMapper objectMapper;

    @Override
    public void reserveBulk(List<ReservationItem> items) {
        List<ReserveBulkRequest.Item> requestItems = items.stream()
            .map(i -> new ReserveBulkRequest.Item(i.orderItemId(), i.subjectType(), i.subjectId(),
                i.quantity()))
            .toList();
        try {
            inventoryInternalApiClient.reserveBulk(new ReserveBulkRequest(requestItems));
        } catch (HttpClientErrorException e) {
            throw mapClientError(e);
        } catch (RestClientException e) {
            throw new AppException(OrderErrorCode.INVENTORY_SERVICE_UNAVAILABLE);
        }
    }

    private AppException mapClientError(HttpClientErrorException e) {
        InventoryErrorResponse body = parseBody(e);
        if (body == null || body.errorCode() == null) {
            log.error("[InventoryReservation] unexpected 4xx without errorCode, status={}",
                e.getStatusCode());
            return new AppException(OrderErrorCode.INVENTORY_REQUEST_INVALID);
        }
        return switch (body.errorCode()) {
            case INSUFFICIENT_STOCK -> new AppException(OrderErrorCode.INSUFFICIENT_STOCK);
            case STOCK_MOVEMENT_CONFLICT -> {
                log.error("[InventoryReservation] stock movement conflict detected, detail={}",
                    body.detail());
                yield new AppException(OrderErrorCode.STOCK_MOVEMENT_CONFLICT);
            }
            default -> new AppException(OrderErrorCode.INVENTORY_REQUEST_INVALID);
        };
    }

    private InventoryErrorResponse parseBody(HttpClientErrorException e) {
        try {
            return objectMapper.readValue(e.getResponseBodyAsByteArray(),
                InventoryErrorResponse.class);
        } catch (Exception parseError) {
            return null;
        }
    }
}
