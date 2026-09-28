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
import com.golajugaenyang.order.domain.cart.CartItem;
import com.golajugaenyang.order.domain.cart.CartItemKey;
import com.golajugaenyang.order.domain.cart.CartItemType;
import com.golajugaenyang.order.error.OrderErrorCode;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
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
        List<CartItem> cartItems = cartRepository.findAll(memberId);
        if (cartItems.isEmpty()) {
            return CartResult.empty(memberId);
        }
        return buildCartResult(memberId, cartItems);
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

        if (lookup.isUnreachable(itemType, itemId)) {
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

    private CartResult buildCartResult(Long memberId, List<CartItem> cartItems) {
        List<CartItem> ordered = cartItems.stream().sorted(CartItem.ADDED_ORDER).toList();

        List<Long> productIds = new ArrayList<>();
        List<Long> timeDealIds = new ArrayList<>();
        ordered.forEach(item -> {
            if (item.key().itemType() == CartItemType.NORMAL) {
                productIds.add(item.key().itemId());
            } else {
                timeDealIds.add(item.key().itemId());
            }
        });

        CartCatalogLookupResult lookup = productCatalogPort.lookup(productIds, timeDealIds);

        List<CartItemResult> results = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem item : ordered) {
            CartItemKey key = item.key();
            boolean unreachable = lookup.isUnreachable(key.itemType(), key.itemId());

            CartItemResult result = key.itemType() == CartItemType.NORMAL
                ? toResult(item, lookup.products().get(key.itemId()), unreachable)
                : toResult(item, lookup.timeDealItems().get(key.itemId()), unreachable);
            results.add(result);
            if (result.available()) {
                totalAmount = totalAmount.add(result.subtotal());
            }
        }

        return new CartResult(memberId, results, totalAmount);
    }

    private CartItemResult toResult(CartItem item, ProductSummary summary, boolean unreachable) {
        if (unreachable) {
            return unavailableWithoutInfo(item, CartItemResult.REASON_TEMPORARILY_UNAVAILABLE);
        }
        if (summary == null) {
            return unavailableWithoutInfo(item, CartItemResult.REASON_NOT_FOUND);
        }
        if (!summary.purchasable()) {
            return unavailableWithInfo(
                item, summary.availability(),
                summary.productName(), summary.thumbnailUrl(),
                summary.price(), summary.originalPrice(), summary.discountRate(), null
            );
        }
        BigDecimal subtotal = summary.price().multiply(BigDecimal.valueOf(item.quantity()));
        return new CartItemResult(
            item.key().itemType().name(), item.key().itemId(), item.quantity(), item.addedAt(),
            true, null,
            summary.productName(), summary.thumbnailUrl(),
            summary.price(), summary.originalPrice(), summary.discountRate(),
            subtotal, null
        );
    }

    private CartItemResult toResult(CartItem item, TimeDealSummary summary, boolean unreachable) {
        if (unreachable) {
            return unavailableWithoutInfo(item, CartItemResult.REASON_TEMPORARILY_UNAVAILABLE);
        }
        if (summary == null) {
            return unavailableWithoutInfo(item, CartItemResult.REASON_NOT_FOUND);
        }
        boolean dealEnded = isDealEnded(summary);
        if (!summary.purchasable() || dealEnded) {
            String reason = dealEnded ? CartItemResult.REASON_DEAL_ENDED : summary.availability();
            return unavailableWithInfo(
                item, reason,
                summary.productName(), summary.thumbnailUrl(),
                summary.discountedPrice(), summary.normalPrice(), summary.discountRate(),
                summary.dealEndAt()
            );
        }
        BigDecimal subtotal = summary.discountedPrice()
            .multiply(BigDecimal.valueOf(item.quantity()));
        return new CartItemResult(
            item.key().itemType().name(), item.key().itemId(), item.quantity(), item.addedAt(),
            true, null,
            summary.productName(), summary.thumbnailUrl(),
            summary.discountedPrice(), summary.normalPrice(), summary.discountRate(),
            subtotal, summary.dealEndAt()
        );
    }

    private boolean isDealEnded(TimeDealSummary summary) {
        return summary.dealEndAt() != null && summary.dealEndAt().isBefore(OffsetDateTime.now());
    }

    private CartItemResult unavailableWithoutInfo(CartItem item, String reason) {
        return new CartItemResult(
            item.key().itemType().name(), item.key().itemId(), item.quantity(), item.addedAt(),
            false, reason,
            null, null, null, null,
            null, null, null
        );
    }

    private CartItemResult unavailableWithInfo(
        CartItem item, String reason,
        String productName, String thumbnailUrl,
        BigDecimal price, BigDecimal originalPrice, BigDecimal discountRate,
        OffsetDateTime dealEndAt
    ) {
        return new CartItemResult(
            item.key().itemType().name(), item.key().itemId(), item.quantity(), item.addedAt(),
            false, reason,
            productName, thumbnailUrl, price, originalPrice, discountRate,
            null, dealEndAt
        );
    }
}