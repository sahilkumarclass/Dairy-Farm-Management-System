package com.sahilkumar.api.expense.dto;

import com.sahilkumar.api.expense.Expense;
import com.sahilkumar.api.expense.ExpenseCategory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseResponse(
        UUID id,
        ExpenseCategory category,
        BigDecimal amount,
        String notes,
        LocalDate expenseDate
) {
    public static ExpenseResponse from(Expense e) {
        return new ExpenseResponse(e.getId(), e.getCategory(), e.getAmount(), e.getNotes(), e.getExpenseDate());
    }
}
