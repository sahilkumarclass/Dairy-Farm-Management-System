package com.sahilkumar.api.customer.dto;

import com.sahilkumar.api.customer.Customer;
import com.sahilkumar.api.customer.CustomerStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String name,
        String phone,
        String address,
        BigDecimal customMilkRate,
        CustomerStatus status,
        Instant createdAt
) {
    public static CustomerResponse from(Customer c) {
        return new CustomerResponse(
                c.getId(), c.getName(), c.getPhone(), c.getAddress(),
                c.getCustomMilkRate(), c.getStatus(), c.getCreatedAt());
    }
}
