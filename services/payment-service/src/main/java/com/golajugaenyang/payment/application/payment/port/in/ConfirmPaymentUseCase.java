package com.golajugaenyang.payment.application.payment.port.in;

import com.golajugaenyang.payment.application.payment.port.in.dto.ConfirmPaymentCommand;
import com.golajugaenyang.payment.application.payment.port.in.dto.ConfirmPaymentResult;

public interface ConfirmPaymentUseCase {

    ConfirmPaymentResult confirmPayment(ConfirmPaymentCommand command);
}
