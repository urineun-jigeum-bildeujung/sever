package com.golajugaenyang.product.adapter.in.web;

import com.golajugaenyang.common.core.domain.CategoryCode;
import com.golajugaenyang.product.adapter.in.web.dto.ProductDetailResponse;
import com.golajugaenyang.product.adapter.in.web.dto.ProductListResponse;
import com.golajugaenyang.product.adapter.in.web.dto.ProductSearchResponse;
import com.golajugaenyang.product.application.product.port.in.ProductDetailUseCase;
import com.golajugaenyang.product.application.product.port.in.ProductSearchUseCase;
import com.golajugaenyang.product.application.product.port.in.dto.ProductDetailResult;
import com.golajugaenyang.product.application.product.port.in.dto.ProductListCommand;
import com.golajugaenyang.product.application.product.port.in.dto.ProductListResult;
import com.golajugaenyang.product.application.product.port.in.ProductListUseCase;
import com.golajugaenyang.product.application.product.port.in.dto.ProductSearchCommand;
import com.golajugaenyang.product.application.product.port.in.dto.ProductSearchResult;
import com.golajugaenyang.product.config.ProductListProperties;
import com.golajugaenyang.product.domain.product.ProductSortType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Validated
public class ProductController implements ProductControllerDocs {

    private final ProductListUseCase productListUseCase;
    private final ProductSearchUseCase productSearchUseCase;
    private final ProductDetailUseCase productDetailUseCase;
    private final ProductListProperties productListProperties;

    @GetMapping
    public ResponseEntity<ProductListResponse> getProducts(
        @RequestParam(required = false) CategoryCode category,
        @RequestParam(defaultValue = "POPULAR") ProductSortType sort,
        @RequestParam(required = false) String cursor,
        @RequestParam(required = false) @Min(1) Integer size,
        @RequestParam(required = false) Long petId
    ) {
        int resolvedSize = productListProperties.resolveSize(size);
        ProductListCommand command = new ProductListCommand(
            category, sort, cursor, resolvedSize, petId);
        ProductListResult result = productListUseCase.getProductList(command);
        return ResponseEntity.ok(ProductListResponse.from(result));
    }

    @Override
    @GetMapping("/search")
    public ResponseEntity<ProductSearchResponse> searchProducts(
        @RequestParam @NotBlank String keyword,
        @RequestParam(required = false) CategoryCode category,
        @RequestParam(defaultValue = "POPULAR") ProductSortType sort,
        @RequestParam(required = false) String cursor,
        @RequestParam(required = false) @Min(1) Integer size
    ) {
        int resolvedSize = productListProperties.resolveSize(size);
        ProductSearchCommand command = new ProductSearchCommand(
            keyword, category, sort, cursor, resolvedSize);
        ProductSearchResult result = productSearchUseCase.searchProducts(command);
        return ResponseEntity.ok(ProductSearchResponse.from(result));
    }

    @Override
    @GetMapping("/{productId}")
    public ProductDetailResponse getProductDetail(@PathVariable Long productId) {
        ProductDetailResult result = productDetailUseCase.getProductDetail(productId);
        return ProductDetailResponse.from(result);
    }
}
