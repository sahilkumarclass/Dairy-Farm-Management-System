package com.sahilkumar.api.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DashboardSummary(
        LocalDate today,
        BigDecimal todayLiters,
        BigDecimal todaySales,
        BigDecimal monthLiters,
        BigDecimal monthSales,
        BigDecimal monthExpenses,
        BigDecimal monthProfit,
        BigDecimal outstandingDues,
        long activeCustomers
) {}
