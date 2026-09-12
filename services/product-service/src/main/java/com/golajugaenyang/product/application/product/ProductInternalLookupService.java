package com.golajugaenyang.product.application.product;


import com.golajugaenyang.product.application.product.port.in.ProductInternalLookupUseCase;
import com.golajugaenyang.product.application.product.port.in.dto.ProductInternalItem;
import com.golajugaenyang.product.application.product.port.in.dto.ProductInternalLookupResult;
import com.golajugaenyang.product.application.product.port.out.ProductBulkQueryRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductInternalLookupService implements ProductInternalLookupUseCase {

    private final ProductBulkQueryRepository productBulkQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public ProductInternalLookupResult getProducts(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return new ProductInternalLookupResult(List.of(), List.of());
        }
        List<ProductInternalItem> items = productBulkQueryRepository.findAllByIds(productIds)
            .stream()
            .map(ProductInternalItem::from)
            .toList();

        Set<Long> foundIds = items.stream()
            .map(ProductInternalItem::productId)
            .collect(Collectors.toSet());
        List<Long> missingIds = productIds.stream()
            .distinct()
            .filter(id -> !foundIds.contains(id))
            .toList();

        return new ProductInternalLookupResult(items, missingIds);
    }
}
