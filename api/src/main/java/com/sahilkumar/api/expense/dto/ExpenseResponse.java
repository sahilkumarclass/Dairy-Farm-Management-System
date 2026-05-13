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
        LocalDate expenseDate,
        UUID cowId,
        String cowTagNo
) {
    public static ExpenseResponse from(Expense e) {
        UUID cowId = e.getCow() == null ? null : e.getCow().getId();
        String cowTag = e.getCow() == null ? null : e.getCow().getTagNo();
        return new ExpenseResponse(
                e.getId(), e.getCategory(), e.getAmount(),
                e.getNotes(), e.getExpenseDate(), cowId, cowTag);
    }
}
