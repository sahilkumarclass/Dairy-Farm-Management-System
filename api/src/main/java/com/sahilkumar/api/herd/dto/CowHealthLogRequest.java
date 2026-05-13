package com.sahilkumar.api.herd.dto;

import com.sahilkumar.api.herd.HealthEventType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CowHealthLogRequest(
        @NotNull UUID cowId,
        @NotNull HealthEventType eventType,
        @NotNull LocalDate eventDate,
        LocalDate nextDueDate,
        @Size(max = 120) String vetName,
        @DecimalMin(value = "0.0") BigDecimal cost,
        @Size(max = 500) String notes
) {}
