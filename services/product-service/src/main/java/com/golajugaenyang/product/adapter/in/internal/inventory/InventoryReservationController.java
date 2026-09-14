package com.golajugaenyang.product.adapter.in.internal.inventory;


import com.golajugaenyang.product.adapter.in.internal.inventory.dto.ReserveBulkRequest;
import com.golajugaenyang.product.application.inventory.port.in.InventoryCommandUseCase;
import com.golajugaenyang.product.application.inventory.port.in.dto.ReserveItemCommand;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/internal/inventory")
@RequiredArgsConstructor
@Validated
public class InventoryReservationController {

    private final InventoryCommandUseCase inventoryCommandUseCase;

    @PostMapping("/reserve-bulk")
    public ResponseEntity<Void> reserveBulk(@RequestBody @Valid ReserveBulkRequest request) {
        List<ReserveItemCommand> commands = request.items().stream()
            .map(ReserveBulkRequest.Item::toCommand)
            .toList();
        inventoryCommandUseCase.reserveBulk(commands);
        return ResponseEntity.ok().build();
    }
}
