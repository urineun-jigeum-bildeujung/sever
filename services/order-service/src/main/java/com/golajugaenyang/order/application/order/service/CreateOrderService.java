package com.golajugaenyang.order.application.order.service;

import com.golajugaenyang.order.application.order.port.in.CreateOrderUseCase;
import com.golajugaenyang.order.application.order.port.in.dto.CreateOrderCommand;
import com.golajugaenyang.order.application.order.port.in.dto.CreateOrderResult;
import com.golajugaenyang.order.application.order.port.out.InventoryReservationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateOrderService implements CreateOrderUseCase {

    private final OrderCreationTransactionSupport transactionSupport;
    private final InventoryReservationPort inventoryReservationPort;

    @Override
    public CreateOrderResult createOrder(CreateOrderCommand command) {
        CreateOrderResult existingResult =
            transactionSupport.findExistingResult(command.memberId(), command.idempotencyKey());
        if (existingResult != null) {
            return existingResult;
        }

        PendingOrderCreation pending;
        try {
            pending = transactionSupport.persistPendingOrder(command);
        } catch (DataIntegrityViolationException dup) {
            CreateOrderResult racedResult =
                transactionSupport.findExistingResult(command.memberId(), command.idempotencyKey());
            if (racedResult != null) {
                return racedResult;
            }
            throw dup;
        }

        try {
            inventoryReservationPort.reserveBulk(pending.reservationItems());
        } catch (RuntimeException reserveEx) {
            try {
                transactionSupport.failReservation(pending.orderId());
            } catch (RuntimeException recordEx) {
                log.error("[CreateOrder] 예약 실패 기록 중 추가 오류 orderId={}",
                    pending.orderId(), recordEx);
            }
            throw reserveEx;
        }

        try {
            transactionSupport.confirmReservation(pending.orderId());
        } catch (RuntimeException confirmEx) {
            log.error("[CreateOrder] 원격 예약은 성공했으나 로컬 상태 기록에 실패 orderId={}",
                pending.orderId(), confirmEx);
            throw confirmEx;
        }

        return pending.result();
    }
}
