package com.sahilkumar.api.herd.dto;

import com.sahilkumar.api.herd.CowMilkProduction;
import com.sahilkumar.api.milk.MilkSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CowMilkProductionResponse(
        UUID id,
        UUID cowId,
        String cowTagNo,
        LocalDate productionDate,
        MilkSession session,
        BigDecimal liters,
        String notes
) {
    public static CowMilkProductionResponse from(CowMilkProduction p) {
        return new CowMilkProductionResponse(
                p.getId(), p.getCow().getId(), p.getCow().getTagNo(),
                p.getProductionDate(), p.getSession(), p.getLiters(), p.getNotes());
    }
}
