package com.sahilkumar.api.portal.dto;

import com.sahilkumar.api.billing.BillStatus;
import com.sahilkumar.api.customer.dto.CustomerResponse;
import java.math.BigDecimal;

public record CustomerSelfDashboard(
        CustomerResponse customer,
        BigDecimal monthLiters,
        BigDecimal monthAmount,
        BigDecimal outstandingTotal,
        BillStatus currentMonthStatus
) {}
