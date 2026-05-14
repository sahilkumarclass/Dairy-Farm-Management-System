package com.sahilkumar.api.customer;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CustomerRepository
        extends JpaRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {

    long countByStatus(CustomerStatus status);

    Optional<Customer> findByUserId(UUID userId);

    Optional<Customer> findByPhone(String phone);

    boolean existsByPhoneAndIdNot(String phone, UUID id);
}
