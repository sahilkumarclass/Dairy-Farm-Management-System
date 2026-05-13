package com.sahilkumar.api.herd.dto;

import com.sahilkumar.api.milk.MilkSession;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CowMilkProductionRequest(
        @NotNull UUID cowId,
        @NotNull LocalDate productionDate,
        @NotNull MilkSession session,
        @NotNull @DecimalMin(value = "0.0") BigDecimal liters,
        @Size(max = 255) String notes
) {}
