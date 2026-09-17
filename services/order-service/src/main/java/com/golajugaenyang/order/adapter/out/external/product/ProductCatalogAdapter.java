package com.golajugaenyang.order.adapter.out.external.product;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.order.adapter.out.external.product.client.ProductInternalApiClient;
import com.golajugaenyang.order.adapter.out.external.product.client.TimeDealInternalApiClient;
import com.golajugaenyang.order.adapter.out.external.product.dto.ProductInternalItemResponse;
import com.golajugaenyang.order.adapter.out.external.product.dto.TimeDealInternalItemResponse;
import com.golajugaenyang.order.application.order.port.out.ProductCatalogPort;
import com.golajugaenyang.order.application.order.port.out.dto.CatalogItem;
import com.golajugaenyang.order.error.OrderErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;


@Component
@RequiredArgsConstructor
public class ProductCatalogAdapter implements ProductCatalogPort {

    private final ProductInternalApiClient productInternalApiClient;
    private final TimeDealInternalApiClient timeDealInternalApiClient;
    private final ExecutorService cartCatalogExecutor;

    @Override
    public List<CatalogItem> lookup(List<Long> productIds, List<Long> dealItemIds) {
        try {
            CompletableFuture<List<CatalogItem>> productsFuture = productIds.isEmpty()
                ? CompletableFuture.completedFuture(List.of())
                : CompletableFuture.supplyAsync(
                    () -> mapProducts(productInternalApiClient.getProducts(productIds).items()),
                    cartCatalogExecutor);

            CompletableFuture<List<CatalogItem>> dealsFuture = dealItemIds.isEmpty()
                ? CompletableFuture.completedFuture(List.of())
                : CompletableFuture.supplyAsync(
                    () -> mapDeals(timeDealInternalApiClient.getTimeDealItems(dealItemIds).items()),
                    cartCatalogExecutor);

            return CompletableFuture.allOf(productsFuture, dealsFuture)
                .thenApply(v -> {
                    List<CatalogItem> merged = new ArrayList<>(productsFuture.join());
                    merged.addAll(dealsFuture.join());
                    return merged;
                })
                .join();
        } catch (CompletionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RestClientException) {
                throw new AppException(OrderErrorCode.PRODUCT_SERVICE_UNAVAILABLE);
            }
            throw e;
        } catch (RestClientException e) {
            throw new AppException(OrderErrorCode.PRODUCT_SERVICE_UNAVAILABLE);
        }
    }

    private List<CatalogItem> mapProducts(List<ProductInternalItemResponse> items) {
        return items.stream().map(p -> {
            var listPrice = p.originalPrice() != null ? p.originalPrice() : p.price();
            var discount = listPrice.subtract(p.price());
            return new CatalogItem(
                p.productId(), false, p.productId(),
                null, p.productGroupId(),
                p.productName(), p.thumbnailUrl(), p.categoryCode(), p.replenishable(),
                listPrice, discount,
                p.netQuantityValue(), p.netQuantityUnit(), p.quantityDimension(),
                p.normalizedQuantityValue(), p.normalizedQuantityUnit(),
                p.purchasable(), p.availability()
            );
        }).toList();
    }

    private List<CatalogItem> mapDeals(List<TimeDealInternalItemResponse> items) {
        return items.stream().map(d -> new CatalogItem(
            d.timeDealItemId(), true, d.productId(),
            d.timeDealItemId(), d.productGroupId(),
            d.productName(), d.thumbnailUrl(), d.categoryCode(), d.replenishable(),
            d.normalPrice(), d.normalPrice().subtract(d.discountedPrice()),
            d.netQuantityValue(), d.netQuantityUnit(), d.quantityDimension(),
            d.normalizedQuantityValue(), d.normalizedQuantityUnit(),
            d.purchasable(), d.availability()
        )).toList();
    }
}
