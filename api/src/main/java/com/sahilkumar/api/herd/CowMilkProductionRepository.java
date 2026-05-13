package com.sahilkumar.api.herd;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CowMilkProductionRepository extends JpaRepository<CowMilkProduction, UUID> {

    @Query("""
            SELECT p FROM CowMilkProduction p
            WHERE p.cow.id = :cowId
              AND p.productionDate BETWEEN :from AND :to
            ORDER BY p.productionDate DESC, p.session DESC
            """)
    Page<CowMilkProduction> findForCowInRange(UUID cowId, LocalDate from, LocalDate to, Pageable pageable);

    @Query("""
            SELECT COALESCE(SUM(p.liters), 0) FROM CowMilkProduction p
            WHERE p.cow.id = :cowId
              AND p.productionDate BETWEEN :from AND :to
            """)
    BigDecimal sumLitersForCow(UUID cowId, LocalDate from, LocalDate to);

    @Query("""
            SELECT COALESCE(SUM(p.liters), 0) FROM CowMilkProduction p
            WHERE p.cow.id = :cowId
            """)
    BigDecimal sumLitersForCowAllTime(UUID cowId);

    @Query("""
            SELECT COALESCE(SUM(p.liters), 0) FROM CowMilkProduction p
            WHERE p.productionDate BETWEEN :from AND :to
            """)
    BigDecimal sumLiters(LocalDate from, LocalDate to);
}
