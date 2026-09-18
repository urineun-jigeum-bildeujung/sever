package com.golajugaenyang.payment.application.payment.port.in;

import com.golajugaenyang.payment.application.payment.port.in.dto.RequestPaymentCommand;
import com.golajugaenyang.payment.application.payment.port.in.dto.RequestPaymentResult;

public interface RequestPaymentUseCase {

    RequestPaymentResult requestPayment(RequestPaymentCommand command);
}
