package com.sahilkumar.api.herd;

import com.sahilkumar.api.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cow extends BaseEntity {

    @Column(name = "tag_no", nullable = false, unique = true, length = 40)
    private String tagNo;

    @Column(length = 80)
    private String name;

    @Column(length = 60)
    private String breed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;

    @Column(name = "age_months")
    private Integer ageMonths;

    @Enumerated(EnumType.STRING)
    @Column(name = "health_status", nullable = false, length = 20)
    private HealthStatus healthStatus;

    @Column(name = "daily_yield_estimate_liters", precision = 10, scale = 3)
    private BigDecimal dailyYieldEstimateLiters;

    @Column(name = "date_acquired")
    private LocalDate dateAcquired;

    @Column(length = 500)
    private String notes;
}
