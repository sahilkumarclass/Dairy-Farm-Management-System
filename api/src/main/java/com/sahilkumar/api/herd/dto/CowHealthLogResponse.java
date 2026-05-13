package com.sahilkumar.api.herd.dto;

import com.sahilkumar.api.herd.CowHealthLog;
import com.sahilkumar.api.herd.HealthEventType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CowHealthLogResponse(
        UUID id,
        UUID cowId,
        String cowTagNo,
        HealthEventType eventType,
        LocalDate eventDate,
        LocalDate nextDueDate,
        String vetName,
        BigDecimal cost,
        String notes
) {
    public static CowHealthLogResponse from(CowHealthLog h) {
        return new CowHealthLogResponse(
                h.getId(), h.getCow().getId(), h.getCow().getTagNo(),
                h.getEventType(), h.getEventDate(), h.getNextDueDate(),
                h.getVetName(), h.getCost(), h.getNotes());
    }
}
