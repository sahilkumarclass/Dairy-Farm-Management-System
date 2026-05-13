package com.sahilkumar.api.herd;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CowHealthLogRepository extends JpaRepository<CowHealthLog, UUID> {

    Page<CowHealthLog> findByCowIdOrderByEventDateDesc(UUID cowId, Pageable pageable);

    @Query("""
            SELECT h FROM CowHealthLog h
            WHERE h.nextDueDate IS NOT NULL
              AND h.nextDueDate BETWEEN :from AND :to
            ORDER BY h.nextDueDate ASC
            """)
    List<CowHealthLog> findUpcomingDueBetween(LocalDate from, LocalDate to);

    @Query("""
            SELECT COALESCE(SUM(h.cost), 0) FROM CowHealthLog h
            WHERE h.cow.id = :cowId
              AND h.eventDate BETWEEN :from AND :to
            """)
    BigDecimal sumCostForCow(UUID cowId, LocalDate from, LocalDate to);

    @Query("""
            SELECT COALESCE(SUM(h.cost), 0) FROM CowHealthLog h
            WHERE h.eventDate BETWEEN :from AND :to
            """)
    BigDecimal sumCost(LocalDate from, LocalDate to);
}
