package com.sahilkumar.api.herd;

import com.sahilkumar.api.herd.dto.CowDetailResponse;
import com.sahilkumar.api.herd.dto.CowRequest;
import com.sahilkumar.api.herd.dto.CowResponse;
import com.sahilkumar.api.herd.dto.CowSummary;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cows")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('OWNER','STAFF')")
@Tag(name = "Herd")
public class CowController {

    private final CowService cowService;

    @GetMapping
    public Page<CowResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) HealthStatus status,
            @PageableDefault(size = 50, sort = "tagNo") Pageable pageable) {
        return cowService.list(q, status, pageable);
    }

    @GetMapping("/summary")
    public CowSummary summary() {
        return cowService.summary();
    }

    @GetMapping("/{id}")
    public CowResponse get(@PathVariable UUID id) {
        return cowService.get(id);
    }

    @GetMapping("/{id}/detail")
    public CowDetailResponse detail(@PathVariable UUID id) {
        return cowService.detail(id);
    }

    @PostMapping
    public ResponseEntity<CowResponse> create(@Valid @RequestBody CowRequest req) {
        return ResponseEntity.status(201).body(cowService.create(req));
    }

    @PutMapping("/{id}")
    public CowResponse update(@PathVariable UUID id, @Valid @RequestBody CowRequest req) {
        return cowService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        cowService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
