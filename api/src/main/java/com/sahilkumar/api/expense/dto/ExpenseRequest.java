package com.sahilkumar.api.expense.dto;

import com.sahilkumar.api.expense.ExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseRequest(
        @NotNull ExpenseCategory category,
        @NotNull @DecimalMin(value = "0.0") BigDecimal amount,
        @Size(max = 500) String notes,
        @NotNull LocalDate expenseDate,
        UUID cowId
) {}
