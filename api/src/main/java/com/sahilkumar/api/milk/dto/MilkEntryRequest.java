package com.sahilkumar.api.milk.dto;

import com.sahilkumar.api.milk.MilkSession;
import com.sahilkumar.api.milk.MilkType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record MilkEntryRequest(
        @NotNull UUID customerId,
        @NotNull MilkType milkType,
        @NotNull @DecimalMin(value = "0.001") BigDecimal quantityLiters,
        @DecimalMin(value = "0.0") BigDecimal ratePerLiter,
        @NotNull MilkSession session,
        @NotNull LocalDate entryDate,
        @Size(max = 255) String notes
) {}
