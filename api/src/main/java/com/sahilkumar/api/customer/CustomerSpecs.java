package com.sahilkumar.api.customer;

import org.springframework.data.jpa.domain.Specification;

public final class CustomerSpecs {

    private CustomerSpecs() {}

    public static Specification<Customer> hasStatus(CustomerStatus status) {
        return (root, cq, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<Customer> nameOrPhoneContains(String q) {
        if (q == null || q.isBlank()) {
            return (root, cq, cb) -> cb.conjunction();
        }
        String lowered = "%" + q.toLowerCase() + "%";
        String raw = "%" + q + "%";
        return (root, cq, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), lowered),
                cb.like(root.get("phone"), raw));
    }
}
