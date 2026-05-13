package com.sahilkumar.api.herd;

import org.springframework.data.jpa.domain.Specification;

public final class CowSpecs {

    private CowSpecs() {}

    public static Specification<Cow> hasHealthStatus(HealthStatus status) {
        return (root, cq, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("healthStatus"), status);
    }

    public static Specification<Cow> tagOrNameContains(String q) {
        if (q == null || q.isBlank()) {
            return (root, cq, cb) -> cb.conjunction();
        }
        String like = "%" + q.toLowerCase() + "%";
        return (root, cq, cb) -> cb.or(
                cb.like(cb.lower(root.get("tagNo")), like),
                cb.like(cb.lower(cb.coalesce(root.get("name"), "")), like));
    }
}
