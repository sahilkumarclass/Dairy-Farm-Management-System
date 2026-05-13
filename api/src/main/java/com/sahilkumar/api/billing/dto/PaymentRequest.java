package com.sahilkumar.api.billing.dto;

import com.sahilkumar.api.billing.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentRequest(
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        @NotNull PaymentMethod paymentMethod,
        @NotNull LocalDate paymentDate,
        @Size(max = 120) String reference,
        @Size(max = 255) String notes
) {}
