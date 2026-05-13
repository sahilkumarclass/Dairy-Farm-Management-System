package com.sahilkumar.api.customer.dto;

import com.sahilkumar.api.customer.CustomerStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CustomerRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 20) String phone,
        @Size(max = 255) String address,
        @DecimalMin(value = "0.0", inclusive = true) BigDecimal customMilkRate,
        CustomerStatus status
) {}
