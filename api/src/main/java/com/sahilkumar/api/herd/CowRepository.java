package com.sahilkumar.api.herd;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CowRepository extends JpaRepository<Cow, UUID>, JpaSpecificationExecutor<Cow> {

    Optional<Cow> findByTagNo(String tagNo);

    boolean existsByTagNo(String tagNo);

    long countByHealthStatus(HealthStatus status);
}
