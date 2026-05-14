package com.sahilkumar.api.milk;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MilkEntryRepository extends JpaRepository<MilkEntry, UUID> {

    boolean existsByCustomerId(UUID customerId);

    @Query("""
            SELECT m FROM MilkEntry m
            WHERE (:customerId IS NULL OR m.customer.id = :customerId)
              AND (:from IS NULL OR m.entryDate >= :from)
              AND (:to   IS NULL OR m.entryDate <= :to)
            ORDER BY m.entryDate DESC, m.session DESC
            """)
    Page<MilkEntry> search(UUID customerId, LocalDate from, LocalDate to, Pageable pageable);

    List<MilkEntry> findByCustomerIdAndEntryDateBetween(UUID customerId, LocalDate from, LocalDate to);

    @Query("""
            SELECT COALESCE(SUM(m.quantityLiters), 0) FROM MilkEntry m
            WHERE m.entryDate BETWEEN :from AND :to
            """)
    BigDecimal sumLiters(LocalDate from, LocalDate to);

    @Query("""
            SELECT COALESCE(SUM(m.totalAmount), 0) FROM MilkEntry m
            WHERE m.entryDate BETWEEN :from AND :to
            """)
    BigDecimal sumAmount(LocalDate from, LocalDate to);

    @Query("""
            SELECT COALESCE(SUM(m.quantityLiters), 0) FROM MilkEntry m
            WHERE m.customer.id = :customerId
              AND m.entryDate BETWEEN :from AND :to
            """)
    BigDecimal sumLitersForCustomer(UUID customerId, LocalDate from, LocalDate to);

    @Query("""
            SELECT COALESCE(SUM(m.totalAmount), 0) FROM MilkEntry m
            WHERE m.customer.id = :customerId
              AND m.entryDate BETWEEN :from AND :to
            """)
    BigDecimal sumAmountForCustomer(UUID customerId, LocalDate from, LocalDate to);
}
