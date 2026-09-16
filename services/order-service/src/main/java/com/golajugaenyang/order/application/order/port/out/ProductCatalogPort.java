package com.golajugaenyang.order.application.order.port.out;

import com.golajugaenyang.order.application.order.port.out.dto.CatalogItem;
import java.util.List;

public interface ProductCatalogPort {

    List<CatalogItem> lookup(List<Long> productIds, List<Long> dealItemIds);
}
