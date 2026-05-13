package com.sahilkumar.api.billing;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BillRepository extends JpaRepository<Bill, UUID> {

    Optional<Bill> findByCustomerIdAndPeriodYearAndPeriodMonth(UUID customerId, int year, int month);

    @Query("""
            SELECT b FROM Bill b
            WHERE (:customerId IS NULL OR b.customer.id = :customerId)
              AND (:status IS NULL OR b.status = :status)
              AND (:year IS NULL OR b.periodYear = :year)
              AND (:month IS NULL OR b.periodMonth = :month)
            ORDER BY b.periodYear DESC, b.periodMonth DESC
            """)
    Page<Bill> search(UUID customerId, BillStatus status, Integer year, Integer month, Pageable pageable);

    List<Bill> findByCustomerIdOrderByPeriodYearDescPeriodMonthDesc(UUID customerId);

    @Query("""
            SELECT COALESCE(SUM(b.remainingAmount), 0) FROM Bill b
            WHERE b.status <> com.sahilkumar.api.billing.BillStatus.PAID
            """)
    BigDecimal sumOutstanding();
}
