package com.sahilkumar.api.herd.dto;

import java.math.BigDecimal;

public record CowSummary(
        long totalCows,
        long healthyCount,
        long underTreatmentCount,
        long dryCount,
        BigDecimal monthLiters,
        BigDecimal monthHealthCost,
        BigDecimal monthFeedAndOtherCost
) {}
