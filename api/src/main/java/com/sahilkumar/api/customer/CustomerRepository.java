package com.sahilkumar.api.customer;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    @Query("""
            SELECT c FROM Customer c
            WHERE (:q IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%'))
                              OR c.phone LIKE CONCAT('%', :q, '%'))
              AND (:status IS NULL OR c.status = :status)
            """)
    Page<Customer> search(String q, CustomerStatus status, Pageable pageable);

    long countByStatus(CustomerStatus status);
}
