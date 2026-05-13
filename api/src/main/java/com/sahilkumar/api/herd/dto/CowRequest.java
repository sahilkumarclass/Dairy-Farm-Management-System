package com.sahilkumar.api.herd.dto;

import com.sahilkumar.api.herd.Gender;
import com.sahilkumar.api.herd.HealthStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CowRequest(
        @NotBlank @Size(max = 40) String tagNo,
        @Size(max = 80) String name,
        @Size(max = 60) String breed,
        @NotNull Gender gender,
        @Min(0) Integer ageMonths,
        @NotNull HealthStatus healthStatus,
        @DecimalMin(value = "0.0") BigDecimal dailyYieldEstimateLiters,
        LocalDate dateAcquired,
        @Size(max = 500) String notes
) {}
