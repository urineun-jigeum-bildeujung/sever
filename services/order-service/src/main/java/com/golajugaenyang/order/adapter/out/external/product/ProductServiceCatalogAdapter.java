package com.golajugaenyang.order.adapter.out.external.product;


import com.golajugaenyang.order.adapter.out.external.product.client.ProductInternalApiClient;
import com.golajugaenyang.order.adapter.out.external.product.client.TimeDealInternalApiClient;
import com.golajugaenyang.order.adapter.out.external.product.dto.ProductInternalItemResponse;
import com.golajugaenyang.order.adapter.out.external.product.dto.ProductInternalItemsResponse;
import com.golajugaenyang.order.adapter.out.external.product.dto.TimeDealInternalItemResponse;
import com.golajugaenyang.order.adapter.out.external.product.dto.TimeDealInternalItemsResponse;
import com.golajugaenyang.order.application.cart.port.out.ProductCatalogPort;
import com.golajugaenyang.order.application.cart.port.out.dto.CartCatalogLookupResult;
import com.golajugaenyang.order.application.cart.port.out.dto.ProductSummary;
import com.golajugaenyang.order.application.cart.port.out.dto.TimeDealSummary;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductServiceCatalogAdapter implements ProductCatalogPort {

    private static final int MAX_BULK_SIZE = 50;

    private final ProductInternalApiClient productInternalApiClient;
    private final TimeDealInternalApiClient timeDealInternalApiClient;
    private final ExecutorService cartCatalogExecutor;

    @Override
    public CartCatalogLookupResult lookup(List<Long> productIds, List<Long> timeDealItemIds) {
        CompletableFuture<ProductLookup> productFuture = CompletableFuture.supplyAsync(
            () -> fetchProducts(productIds), cartCatalogExecutor);
        CompletableFuture<TimeDealLookup> timeDealFuture = CompletableFuture.supplyAsync(
            () -> fetchTimeDealItems(timeDealItemIds), cartCatalogExecutor);

        CompletableFuture.allOf(productFuture, timeDealFuture).join();

        ProductLookup productLookup = productFuture.join();
        TimeDealLookup timeDealLookup = timeDealFuture.join();

        return new CartCatalogLookupResult(
            productLookup.items(), timeDealLookup.items(),
            productLookup.missingIds(), timeDealLookup.missingIds(),
            productLookup.unreachableIds(), timeDealLookup.unreachableIds()
        );
    }

    private ProductLookup fetchProducts(List<Long> productIds) {
        if (productIds.isEmpty()) {
            return new ProductLookup(Map.of(), List.of(), List.of());
        }
        Map<Long, ProductSummary> items = new HashMap<>();
        List<Long> missing = new ArrayList<>();
        List<Long> unreachable = new ArrayList<>();

        for (List<Long> chunk : partition(productIds, MAX_BULK_SIZE)) {
            try {
                var response = productInternalApiClient.getProducts(chunk);
                response.items().forEach(item ->
                    items.put(item.productId(), toSummary(item)));
                missing.addAll(response.missingProductIds());
            } catch (Exception e) {
                log.error("Failed to call product-service. chunk={}", chunk, e);
                unreachable.addAll(chunk);
            }
        }
        return new ProductLookup(items, missing, unreachable);
    }

    private TimeDealLookup fetchTimeDealItems(List<Long> timeDealItemIds) {
        if (timeDealItemIds.isEmpty()) {
            return new TimeDealLookup(Map.of(), List.of(), List.of());
        }
        Map<Long, TimeDealSummary> items = new HashMap<>();
        List<Long> missing = new ArrayList<>();
        List<Long> unreachable = new ArrayList<>();

        for (List<Long> chunk : partition(timeDealItemIds, MAX_BULK_SIZE)) {
            try {
                var response = timeDealInternalApiClient.getTimeDealItems(chunk);
                response.items().forEach(item ->
                    items.put(item.timeDealItemId(), toSummary(item)));
                missing.addAll(response.missingTimeDealItemIds());
            } catch (Exception e) {
                log.error("Failed to call product-service chunk={}", chunk, e);
                unreachable.addAll(chunk);
            }
        }
        return new TimeDealLookup(items, missing, unreachable);
    }

    private ProductSummary toSummary(ProductInternalItemResponse item) {
        return new ProductSummary(
            item.productId(), item.productName(), item.thumbnailUrl(),
            item.price(), item.originalPrice(), item.discountRate(),
            item.purchasable(), item.availability()
        );
    }

    private TimeDealSummary toSummary(TimeDealInternalItemResponse item) {
        return new TimeDealSummary(
            item.timeDealItemId(), item.productId(), item.productName(), item.thumbnailUrl(),
            item.discountedPrice(), item.normalPrice(), item.discountRate(),
            item.remainingQuantity(), item.perUserQuantityLimit(),
            item.purchasable(), item.availability(), item.dealEndAt()
        );
    }

    private static <T> List<List<T>> partition(List<T> source, int size) {
        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < source.size(); i += size) {
            result.add(source.subList(i, Math.min(i + size, source.size())));
        }
        return result;
    }

    private record ProductLookup(
        Map<Long, ProductSummary> items,
        List<Long> missingIds,
        List<Long> unreachableIds
    ) {

    }

    private record TimeDealLookup(
        Map<Long, TimeDealSummary> items,
        List<Long> missingIds,
        List<Long> unreachableIds
    ) {

    }
}
