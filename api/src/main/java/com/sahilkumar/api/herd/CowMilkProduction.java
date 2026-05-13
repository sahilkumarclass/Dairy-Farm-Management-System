package com.sahilkumar.api.herd;

import com.sahilkumar.api.common.BaseEntity;
import com.sahilkumar.api.milk.MilkSession;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cow_milk_production", uniqueConstraints = @UniqueConstraint(
        name = "uq_cow_production",
        columnNames = {"cow_id", "production_date", "session"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CowMilkProduction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cow_id", nullable = false)
    private Cow cow;

    @Column(name = "production_date", nullable = false)
    private LocalDate productionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MilkSession session;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal liters;

    @Column(length = 255)
    private String notes;
}
