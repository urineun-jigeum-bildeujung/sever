package com.golajugaenyang.order.application.cart;


import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.order.application.cart.port.in.AddCartItemUseCase;
import com.golajugaenyang.order.application.cart.port.in.ChangeCartItemQuantityUseCase;
import com.golajugaenyang.order.application.cart.port.in.GetCartUseCase;
import com.golajugaenyang.order.application.cart.port.in.RemoveCartItemUseCase;
import com.golajugaenyang.order.application.cart.port.in.dto.AddCartItemCommand;
import com.golajugaenyang.order.application.cart.port.in.dto.CartItemResult;
import com.golajugaenyang.order.application.cart.port.in.dto.CartResult;
import com.golajugaenyang.order.application.cart.port.in.dto.ChangeCartItemQuantityCommand;
import com.golajugaenyang.order.application.cart.port.in.dto.RemoveCartItemCommand;
import com.golajugaenyang.order.application.cart.port.out.CartRepository;
import com.golajugaenyang.order.application.cart.port.out.ProductCatalogPort;
import com.golajugaenyang.order.application.cart.port.out.dto.CartCatalogLookupResult;
import com.golajugaenyang.order.application.cart.port.out.dto.ProductSummary;
import com.golajugaenyang.order.application.cart.port.out.dto.TimeDealSummary;
import com.golajugaenyang.order.domain.cart.CartItemKey;
import com.golajugaenyang.order.domain.cart.CartItemType;
import com.golajugaenyang.order.error.OrderErrorCode;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CartService implements
    AddCartItemUseCase, GetCartUseCase, ChangeCartItemQuantityUseCase, RemoveCartItemUseCase {

    private static final Duration CART_TTL = Duration.ofDays(30);

    private final CartRepository cartRepository;
    private final ProductCatalogPort productCatalogPort;

    @Override
    public void addItem(AddCartItemCommand command) {
        CartItemKey key = new CartItemKey(command.itemType(), command.itemId());
        validatePurchasable(command.itemType(), command.itemId());
        cartRepository.addOrIncrease(command.memberId(), key, command.quantity(), CART_TTL);
    }

    @Override
    public CartResult getCart(Long memberId) {
        Map<CartItemKey, Integer> storedItems = cartRepository.findAll(memberId);
        if (storedItems.isEmpty()) {
            return CartResult.empty(memberId);
        }
        return buildCartResult(memberId, storedItems);
    }

    @Override
    public void changeQuantity(ChangeCartItemQuantityCommand command) {
        CartItemKey key = new CartItemKey(command.itemType(), command.itemId());
        int updated = cartRepository.changeQuantity(
            command.memberId(), key, command.delta(), CART_TTL);
        if (updated == CartRepository.NOT_FOUND) {
            throw new AppException(OrderErrorCode.CART_ITEM_NOT_FOUND);
        }
    }

    @Override
    public void removeItem(RemoveCartItemCommand command) {
        CartItemKey key = new CartItemKey(command.itemType(), command.itemId());
        cartRepository.remove(command.memberId(), key);
    }

    private void validatePurchasable(CartItemType itemType, Long itemId) {
        CartCatalogLookupResult lookup = itemType == CartItemType.NORMAL
            ? productCatalogPort.lookup(List.of(itemId), List.of())
            : productCatalogPort.lookup(List.of(), List.of(itemId));

        boolean unreachable = itemType == CartItemType.NORMAL
            ? lookup.unreachableProductIds().contains(itemId)
            : lookup.unreachableTimeDealItemIds().contains(itemId);
        
        if (unreachable) {
            throw new AppException(OrderErrorCode.PRODUCT_SERVICE_UNAVAILABLE);
        }

        if (itemType == CartItemType.NORMAL) {
            ProductSummary summary = lookup.products().get(itemId);
            if (summary == null || !summary.purchasable()) {
                throw new AppException(OrderErrorCode.PRODUCT_NOT_PURCHASABLE);
            }
        } else {
            TimeDealSummary summary = lookup.timeDealItems().get(itemId);
            if (summary == null || !summary.purchasable()) {
                throw new AppException(OrderErrorCode.PRODUCT_NOT_PURCHASABLE);
            }
        }
    }

    private CartResult buildCartResult(Long memberId, Map<CartItemKey, Integer> storedItems) {
        List<Long> productIds = new ArrayList<>();
        List<Long> timeDealIds = new ArrayList<>();

        storedItems.keySet().forEach(key -> {
            if (key.itemType() == CartItemType.NORMAL) {
                productIds.add(key.itemId());
            } else {
                timeDealIds.add(key.itemId());
            }
        });

        CartCatalogLookupResult lookup =
            productCatalogPort.lookup(productIds, timeDealIds);

        List<CartItemResult> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Map.Entry<CartItemKey, Integer> entry : storedItems.entrySet()) {
            CartItemKey key = entry.getKey();
            int quantity = entry.getValue();
            CartItemResult result = key.itemType() == CartItemType.NORMAL
                ? toResult(key, quantity, lookup.products().get(key.itemId()))
                : toResult(key, quantity, lookup.timeDealItems().get(key.itemId()));
            items.add(result);
            if (result.available()) {
                totalAmount = totalAmount.add(result.subtotal());
            }
        }

        return new CartResult(memberId, items, totalAmount);
    }

    private CartItemResult toResult(CartItemKey key, int quantity, ProductSummary summary) {
        if (summary == null || !summary.purchasable()) {
            return unavailable(key, quantity,
                summary == null ? "NOT_FOUND" : summary.availability());
        }
        BigDecimal subtotal = summary.price().multiply(BigDecimal.valueOf(quantity));

        return new CartItemResult(
            key.itemType().name(), key.itemId(), quantity, true, null,
            summary.productName(), summary.thumbnailUrl(),
            summary.price(), summary.originalPrice(), summary.discountRate(),
            subtotal, null
        );
    }

    private CartItemResult toResult(CartItemKey key, int quantity, TimeDealSummary summary) {
        if (summary == null || !summary.purchasable() || isDealEnded(summary)) {
            String reason = summary == null ? "NOT_FOUND"
                : isDealEnded(summary) ? "DEAL_ENDED" : summary.availability();
            return unavailable(key, quantity, reason);
        }
        BigDecimal subtotal = summary.discountedPrice().multiply(BigDecimal.valueOf(quantity));

        return new CartItemResult(
            key.itemType().name(), key.itemId(), quantity, true, null,
            summary.productName(), summary.thumbnailUrl(),
            summary.discountedPrice(), summary.normalPrice(), summary.discountRate(),
            subtotal, summary.dealEndAt()
        );
    }

    private boolean isDealEnded(TimeDealSummary summary) {
        return summary.dealEndAt() != null
            && summary.dealEndAt().isBefore(OffsetDateTime.now());
    }

    private CartItemResult unavailable(CartItemKey key, int quantity, String reason) {
        return new CartItemResult(
            key.itemType().name(), key.itemId(), quantity, false, reason,
            null, null, null, null,
            null, null, null
        );
    }
}
