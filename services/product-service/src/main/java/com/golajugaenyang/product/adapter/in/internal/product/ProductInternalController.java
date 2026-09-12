package com.golajugaenyang.product.adapter.in.internal.product;


import com.golajugaenyang.product.adapter.in.internal.product.dto.ProductInternalItemsResponse;
import com.golajugaenyang.product.application.product.port.in.ProductInternalLookupUseCase;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/internal/products")
@RequiredArgsConstructor
@Validated
public class ProductInternalController {

    private static final int MAX_BULK_SIZE = 50;

    private final ProductInternalLookupUseCase productInternalLookupUseCase;

    @GetMapping
    public ProductInternalItemsResponse getProducts(
        @RequestParam @NotEmpty @Size(max = MAX_BULK_SIZE) List<Long> ids
    ) {
        return ProductInternalItemsResponse
            .from(productInternalLookupUseCase.getProducts(ids));
    }
}
