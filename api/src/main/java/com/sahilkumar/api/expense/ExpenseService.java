package com.sahilkumar.api.expense;

import com.sahilkumar.api.common.exception.ResourceNotFoundException;
import com.sahilkumar.api.expense.dto.ExpenseRequest;
import com.sahilkumar.api.expense.dto.ExpenseResponse;
import com.sahilkumar.api.herd.Cow;
import com.sahilkumar.api.herd.CowRepository;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CowRepository cowRepository;

    public Page<ExpenseResponse> list(ExpenseCategory category, LocalDate from, LocalDate to, Pageable pageable) {
        return expenseRepository.search(category, from, to, pageable).map(ExpenseResponse::from);
    }

    @Transactional
    public ExpenseResponse create(ExpenseRequest req) {
        Expense e = Expense.builder()
                .category(req.category())
                .amount(req.amount())
                .notes(req.notes())
                .expenseDate(req.expenseDate())
                .cow(resolveCow(req.cowId()))
                .build();
        return ExpenseResponse.from(expenseRepository.save(e));
    }

    @Transactional
    public ExpenseResponse update(UUID id, ExpenseRequest req) {
        Expense e = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", id));
        e.setCategory(req.category());
        e.setAmount(req.amount());
        e.setNotes(req.notes());
        e.setExpenseDate(req.expenseDate());
        e.setCow(resolveCow(req.cowId()));
        return ExpenseResponse.from(e);
    }

    @Transactional
    public void delete(UUID id) {
        if (!expenseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Expense", id);
        }
        expenseRepository.deleteById(id);
    }

    private Cow resolveCow(UUID cowId) {
        if (cowId == null) return null;
        return cowRepository.findById(cowId)
                .orElseThrow(() -> new ResourceNotFoundException("Cow", cowId));
    }
}
