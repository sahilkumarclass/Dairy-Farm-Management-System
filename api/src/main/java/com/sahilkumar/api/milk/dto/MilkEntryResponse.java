package com.sahilkumar.api.milk.dto;

import com.sahilkumar.api.milk.MilkEntry;
import com.sahilkumar.api.milk.MilkSession;
import com.sahilkumar.api.milk.MilkType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record MilkEntryResponse(
        UUID id,
        UUID customerId,
        String customerName,
        MilkType milkType,
        BigDecimal quantityLiters,
        BigDecimal ratePerLiter,
        BigDecimal totalAmount,
        MilkSession session,
        LocalDate entryDate,
        String notes
) {
    public static MilkEntryResponse from(MilkEntry m) {
        return new MilkEntryResponse(
                m.getId(),
                m.getCustomer().getId(),
                m.getCustomer().getName(),
                m.getMilkType(),
                m.getQuantityLiters(),
                m.getRatePerLiter(),
                m.getTotalAmount(),
                m.getSession(),
                m.getEntryDate(),
                m.getNotes());
    }
}
