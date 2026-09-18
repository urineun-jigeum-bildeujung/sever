package com.golajugaenyang.payment.adapter.out.external.order;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.payment.adapter.out.external.order.client.OrderInternalApiClient;
import com.golajugaenyang.payment.adapter.out.external.order.dto.OrderPaymentContextResponse;
import com.golajugaenyang.payment.adapter.out.external.order.dto.OrderPaymentItemResponse;
import com.golajugaenyang.payment.application.payment.port.out.OrderLookupPort;
import com.golajugaenyang.payment.application.payment.port.out.dto.OrderPaymentContext;
import com.golajugaenyang.payment.application.payment.port.out.dto.OrderPaymentContext.OrderItemSubject;
import com.golajugaenyang.payment.error.PaymentErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;


@Component
@RequiredArgsConstructor
public class OrderLookupAdapter implements OrderLookupPort {

    private final OrderInternalApiClient orderInternalApiClient;

    @Override
    public OrderPaymentContext lookup(Long orderId) {
        OrderPaymentContextResponse response;
        try {
            response = orderInternalApiClient.getPaymentContext(orderId);
        } catch (HttpClientErrorException.NotFound e) {
            throw new AppException(PaymentErrorCode.ORDER_NOT_FOUND);
        } catch (RestClientException e) {
            throw new AppException(PaymentErrorCode.ORDER_SERVICE_UNAVAILABLE);
        }

        List<OrderItemSubject> items = response.items().stream()
            .map(this::toItemSubject)
            .toList();
        return new OrderPaymentContext(
            response.orderId(), response.orderNumber(), response.memberId(),
            response.orderStatus(), response.totalAmount(),
            response.orderName(), items);
    }

    private OrderPaymentContext.OrderItemSubject toItemSubject(
        OrderPaymentItemResponse r
    ) {
        return new OrderPaymentContext.OrderItemSubject(
            r.orderItemId(), r.subjectType(), r.subjectId(), r.quantity());
    }
}
