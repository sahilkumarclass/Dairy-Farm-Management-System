package com.sahilkumar.api.expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    @Query("""
            SELECT e FROM Expense e
            WHERE (:category IS NULL OR e.category = :category)
              AND (:from IS NULL OR e.expenseDate >= :from)
              AND (:to   IS NULL OR e.expenseDate <= :to)
            ORDER BY e.expenseDate DESC
            """)
    Page<Expense> search(ExpenseCategory category, LocalDate from, LocalDate to, Pageable pageable);

    @Query("""
            SELECT COALESCE(SUM(e.amount), 0) FROM Expense e
            WHERE e.expenseDate BETWEEN :from AND :to
            """)
    BigDecimal sumAmount(LocalDate from, LocalDate to);

    @Query("""
            SELECT COALESCE(SUM(e.amount), 0) FROM Expense e
            WHERE e.cow.id = :cowId
              AND e.expenseDate BETWEEN :from AND :to
            """)
    BigDecimal sumAmountForCow(UUID cowId, LocalDate from, LocalDate to);

    @Query("""
            SELECT COALESCE(SUM(e.amount), 0) FROM Expense e
            WHERE e.cow IS NOT NULL
              AND e.expenseDate BETWEEN :from AND :to
            """)
    BigDecimal sumAmountForAnyCow(LocalDate from, LocalDate to);
}
