package com.sahilkumar.api.milk;

import com.sahilkumar.api.common.BaseEntity;
import com.sahilkumar.api.customer.Customer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "milk_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MilkEntry extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "milk_type", nullable = false, length = 20)
    private MilkType milkType;

    @Column(name = "quantity_liters", nullable = false, precision = 10, scale = 3)
    private BigDecimal quantityLiters;

    @Column(name = "rate_per_liter", nullable = false, precision = 10, scale = 2)
    private BigDecimal ratePerLiter;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MilkSession session;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Column(length = 255)
    private String notes;
}
