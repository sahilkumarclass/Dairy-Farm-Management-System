package com.sahilkumar.api.herd;

import com.sahilkumar.api.herd.dto.CowMilkProductionRequest;
import com.sahilkumar.api.herd.dto.CowMilkProductionResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cow-production")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('OWNER','STAFF')")
@Tag(name = "Cow Milk Production")
public class CowMilkProductionController {

    private final CowMilkProductionService service;

    @GetMapping
    public Page<CowMilkProductionResponse> list(
            @RequestParam UUID cowId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @PageableDefault(size = 60) Pageable pageable) {
        return service.listForCow(cowId, from, to, pageable);
    }

    @PostMapping
    public ResponseEntity<CowMilkProductionResponse> create(@Valid @RequestBody CowMilkProductionRequest req) {
        return ResponseEntity.status(201).body(service.create(req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
