package com.sahilkumar.api.herd.dto;

import com.sahilkumar.api.herd.Cow;
import com.sahilkumar.api.herd.Gender;
import com.sahilkumar.api.herd.HealthStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CowResponse(
        UUID id,
        String tagNo,
        String name,
        String breed,
        Gender gender,
        Integer ageMonths,
        HealthStatus healthStatus,
        BigDecimal dailyYieldEstimateLiters,
        LocalDate dateAcquired,
        String notes
) {
    public static CowResponse from(Cow c) {
        return new CowResponse(
                c.getId(), c.getTagNo(), c.getName(), c.getBreed(),
                c.getGender(), c.getAgeMonths(), c.getHealthStatus(),
                c.getDailyYieldEstimateLiters(), c.getDateAcquired(), c.getNotes());
    }
}
