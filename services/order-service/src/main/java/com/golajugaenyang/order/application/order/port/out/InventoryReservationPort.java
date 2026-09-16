package com.golajugaenyang.order.application.order.port.out;

import com.golajugaenyang.order.application.order.port.out.dto.ReservationItem;
import java.util.List;

public interface InventoryReservationPort {

    void reserveBulk(List<ReservationItem> items);
}
