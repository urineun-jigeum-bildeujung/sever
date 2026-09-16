package com.golajugaenyang.order.adapter.out.external.inventory.dto;

public record InventoryErrorResponse(
    String detail,
    String instance,
    int status,
    String title,
    String errorCode
) {

}
