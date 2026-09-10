package com.golajugaenyang.product.application.product;

import com.golajugaenyang.product.application.product.port.out.dto.ProductCursor;
import com.golajugaenyang.product.application.product.port.in.dto.ProductListCommand;
import com.golajugaenyang.product.application.product.port.out.dto.ProductListCriteria;
import com.golajugaenyang.product.application.product.port.in.dto.ProductListItem;
import com.golajugaenyang.product.application.product.port.out.dto.ProductListProjection;
import com.golajugaenyang.product.application.product.port.in.dto.ProductListResult;
import com.golajugaenyang.product.application.product.port.in.ProductListUseCase;
import com.golajugaenyang.product.application.product.port.out.ProductQueryRepository;
import com.golajugaenyang.product.domain.product.ProductSortType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ProductListService implements ProductListUseCase {

    private final ProductQueryRepository productQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public ProductListResult getProductList(ProductListCommand command) {

        ProductCursor cursor = ProductCursor.decode(command.cursor());
        ProductSortType effectiveSort = command.sortType().resolveEffectiveSort();
        if (cursor != null) {
            cursor.validateSortType(effectiveSort);
        }
        ProductListCriteria criteria = ProductListCriteria.of(command, cursor);

        List<ProductListProjection> fetched = productQueryRepository.findProductList(criteria);

        boolean hasNext = fetched.size() > command.size();
        List<ProductListProjection> pageItems = hasNext
            ? fetched.subList(0, command.size())
            : fetched;

        List<ProductListItem> items = pageItems.stream()
            .map(ProductListItem::from)
            .toList();

        String nextCursor = hasNext
            ? buildNextCursor(pageItems.getLast(), command.sortType())
            : null;

        return new ProductListResult(items, nextCursor, hasNext);
    }

    private String buildNextCursor(ProductListProjection last, ProductSortType sortType) {
        String sortValue = switch (sortType) {
            case POPULAR -> String.valueOf(last.salesCount());
            case REVIEW -> String.valueOf(last.reviewCount());
            case PRICE_DESC, PRICE_ASC -> last.price().toPlainString();
            case RECOMMEND -> throw new IllegalStateException("도달 불가");
        };
        return new ProductCursor(sortType, sortValue, last.id()).encode();
    }
}
