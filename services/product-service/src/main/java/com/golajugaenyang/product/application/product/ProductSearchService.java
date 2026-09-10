package com.golajugaenyang.product.application.product;


import com.golajugaenyang.product.application.product.port.in.ProductSearchUseCase;
import com.golajugaenyang.product.application.product.port.in.dto.ProductListItem;
import com.golajugaenyang.product.application.product.port.in.dto.ProductSearchCommand;
import com.golajugaenyang.product.application.product.port.in.dto.ProductSearchResult;
import com.golajugaenyang.product.application.product.port.out.ProductSearchQueryRepository;
import com.golajugaenyang.product.application.product.port.out.dto.ProductListProjection;
import com.golajugaenyang.product.application.product.port.out.dto.ProductSearchCriteria;
import com.golajugaenyang.product.application.product.port.out.dto.ProductSearchCursor;
import com.golajugaenyang.product.domain.product.ProductSortType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ProductSearchService implements ProductSearchUseCase {

    private final ProductSearchQueryRepository productSearchQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public ProductSearchResult searchProducts(ProductSearchCommand command) {
        ProductSortType effectiveSort = command.sortType().resolveEffectiveSort();

        ProductSearchCursor cursor = ProductSearchCursor.decode(command.cursor());
        if (cursor != null) {
            cursor.validate(effectiveSort, command.keyword());
        }

        ProductSearchCriteria criteria = ProductSearchCriteria.of(command, effectiveSort, cursor);

        List<ProductListProjection> fetched = productSearchQueryRepository.search(criteria);

        boolean hasNext = fetched.size() > command.size();
        List<ProductListProjection> pageItems = hasNext
            ? fetched.subList(0, command.size())
            : fetched;

        List<ProductListItem> items = pageItems.stream().map(ProductListItem::from).toList();

        String nextCursor = hasNext
            ? buildNextCursor(pageItems.getLast(), effectiveSort, command.keyword())
            : null;

        Long totalCount = (cursor == null)
            ? productSearchQueryRepository.count(criteria)
            : null;

        return new ProductSearchResult(items, nextCursor, hasNext, totalCount);
    }

    private String buildNextCursor(
        ProductListProjection last, ProductSortType sortType, String keyword) {
        String sortValue = switch (sortType) {
            case POPULAR -> String.valueOf(last.salesCount());
            case REVIEW -> String.valueOf(last.reviewCount());
            case PRICE_DESC, PRICE_ASC -> last.price().toPlainString();
            case RECOMMEND -> throw new IllegalStateException("도달 불가");
        };
        return new ProductSearchCursor(sortType, keyword, sortValue, last.id()).encode();
    }
}
