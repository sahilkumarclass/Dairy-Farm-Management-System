package com.sahilkumar.api.billing.dto;

import com.sahilkumar.api.billing.Bill;
import com.sahilkumar.api.billing.BillStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BillResponse(
        UUID id,
        UUID customerId,
        String customerName,
        int periodMonth,
        int periodYear,
        BigDecimal totalLiters,
        BigDecimal totalAmount,
        BigDecimal paidAmount,
        BigDecimal remainingAmount,
        BillStatus status,
        OffsetDateTime generatedAt
) {
    public static BillResponse from(Bill b) {
        return new BillResponse(
                b.getId(),
                b.getCustomer().getId(),
                b.getCustomer().getName(),
                b.getPeriodMonth(),
                b.getPeriodYear(),
                b.getTotalLiters(),
                b.getTotalAmount(),
                b.getPaidAmount(),
                b.getRemainingAmount(),
                b.getStatus(),
                b.getGeneratedAt());
    }
}
