package com.sahilkumar.api.dashboard;

import com.sahilkumar.api.billing.BillRepository;
import com.sahilkumar.api.customer.CustomerRepository;
import com.sahilkumar.api.customer.CustomerStatus;
import com.sahilkumar.api.dashboard.dto.DashboardSummary;
import com.sahilkumar.api.expense.ExpenseRepository;
import com.sahilkumar.api.milk.MilkEntryRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final MilkEntryRepository milkEntryRepository;
    private final ExpenseRepository expenseRepository;
    private final BillRepository billRepository;
    private final CustomerRepository customerRepository;

    public DashboardSummary summary() {
        LocalDate today = LocalDate.now();
        YearMonth thisMonth = YearMonth.from(today);
        LocalDate monthStart = thisMonth.atDay(1);
        LocalDate monthEnd = thisMonth.atEndOfMonth();

        BigDecimal todayLiters = milkEntryRepository.sumLiters(today, today);
        BigDecimal todaySales = milkEntryRepository.sumAmount(today, today);
        BigDecimal monthLiters = milkEntryRepository.sumLiters(monthStart, monthEnd);
        BigDecimal monthSales = milkEntryRepository.sumAmount(monthStart, monthEnd);
        BigDecimal monthExpenses = expenseRepository.sumAmount(monthStart, monthEnd);
        BigDecimal monthProfit = monthSales.subtract(monthExpenses);
        BigDecimal outstanding = billRepository.sumOutstanding();
        long activeCustomers = customerRepository.countByStatus(CustomerStatus.ACTIVE);

        return new DashboardSummary(
                today, todayLiters, todaySales,
                monthLiters, monthSales, monthExpenses, monthProfit,
                outstanding, activeCustomers);
    }
}
