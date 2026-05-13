package com.sahilkumar.api.herd;

import com.sahilkumar.api.herd.dto.CowHealthLogRequest;
import com.sahilkumar.api.herd.dto.CowHealthLogResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cow-health")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('OWNER','STAFF')")
@Tag(name = "Cow Health")
public class CowHealthLogController {

    private final CowHealthLogService service;

    @GetMapping
    public Page<CowHealthLogResponse> list(
            @RequestParam UUID cowId,
            @PageableDefault(size = 50) Pageable pageable) {
        return service.listForCow(cowId, pageable);
    }

    @GetMapping("/upcoming")
    public List<CowHealthLogResponse> upcoming(@RequestParam(defaultValue = "14") int daysAhead) {
        return service.upcomingDue(daysAhead);
    }

    @PostMapping
    public ResponseEntity<CowHealthLogResponse> create(@Valid @RequestBody CowHealthLogRequest req) {
        return ResponseEntity.status(201).body(service.create(req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
