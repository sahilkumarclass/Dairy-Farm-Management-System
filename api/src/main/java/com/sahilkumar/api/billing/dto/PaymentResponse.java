package com.sahilkumar.api.billing.dto;

import com.sahilkumar.api.billing.Payment;
import com.sahilkumar.api.billing.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID billId,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        LocalDate paymentDate,
        String reference,
        String notes
) {
    public static PaymentResponse from(Payment p) {
        return new PaymentResponse(
                p.getId(),
                p.getBill().getId(),
                p.getAmount(),
                p.getPaymentMethod(),
                p.getPaymentDate(),
                p.getReference(),
                p.getNotes());
    }
}
