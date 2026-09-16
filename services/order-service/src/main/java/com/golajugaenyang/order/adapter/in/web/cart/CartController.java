package com.golajugaenyang.order.adapter.in.web.cart;


import com.golajugaenyang.common.security.annotation.MemberId;
import com.golajugaenyang.order.adapter.in.web.cart.dto.AddCartItemRequest;
import com.golajugaenyang.order.adapter.in.web.cart.dto.CartResponse;
import com.golajugaenyang.order.adapter.in.web.cart.dto.ChangeCartItemQuantityRequest;
import com.golajugaenyang.order.application.cart.port.in.AddCartItemUseCase;
import com.golajugaenyang.order.application.cart.port.in.ChangeCartItemQuantityUseCase;
import com.golajugaenyang.order.application.cart.port.in.GetCartUseCase;
import com.golajugaenyang.order.application.cart.port.in.RemoveCartItemUseCase;
import com.golajugaenyang.order.application.cart.port.in.dto.AddCartItemCommand;
import com.golajugaenyang.order.application.cart.port.in.dto.ChangeCartItemQuantityCommand;
import com.golajugaenyang.order.application.cart.port.in.dto.RemoveCartItemCommand;
import com.golajugaenyang.order.domain.cart.CartItemType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
@Validated
public class CartController implements CartControllerDocs {

    private final GetCartUseCase getCartUseCase;
    private final AddCartItemUseCase addCartItemUseCase;
    private final ChangeCartItemQuantityUseCase changeCartItemQuantityUseCase;
    private final RemoveCartItemUseCase removeCartItemUseCase;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@MemberId Long memberId) {
        return ResponseEntity.ok(CartResponse.from(getCartUseCase.getCart(memberId)));
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public void addItem(
        @MemberId Long memberId,
        @Valid @RequestBody AddCartItemRequest request
    ) {
        addCartItemUseCase.addItem(new AddCartItemCommand(
            memberId, request.itemType(), request.itemId(), request.quantity()));
    }

    @PatchMapping("/items/{itemType}/{itemId}")
    public void changeQuantity(
        @MemberId Long memberId,
        @PathVariable CartItemType itemType,
        @PathVariable Long itemId,
        @Valid @RequestBody ChangeCartItemQuantityRequest request
    ) {
        changeCartItemQuantityUseCase.changeQuantity(new ChangeCartItemQuantityCommand(
            memberId, itemType, itemId, request.delta()));
    }

    @DeleteMapping("/items/{itemType}/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(
        @MemberId Long memberId,
        @PathVariable CartItemType itemType,
        @PathVariable Long itemId
    ) {
        removeCartItemUseCase.removeItem(new RemoveCartItemCommand(
            memberId, itemType, itemId));
    }
}
