package com.sahilkumar.api.herd.dto;

import java.math.BigDecimal;

public record CowDetailResponse(
        CowResponse cow,
        BigDecimal monthLiters,
        BigDecimal lifetimeLiters,
        BigDecimal monthHealthCost,
        BigDecimal monthExpenseTotal
) {}
