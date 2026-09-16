package com.golajugaenyang.order.application.cart.port.out;

import com.golajugaenyang.order.application.cart.port.out.dto.CartCatalogLookupResult;
import java.util.List;

public interface ProductCatalogPort {

    CartCatalogLookupResult lookup(
        List<Long> productIds, List<Long> timeDealItemIds);
}
