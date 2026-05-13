package com.sahilkumar.api.milk;

import com.sahilkumar.api.milk.dto.MilkEntryRequest;
import com.sahilkumar.api.milk.dto.MilkEntryResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/api/milk-entries")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('OWNER','STAFF')")
@Tag(name = "Milk Entries")
public class MilkEntryController {

    private final MilkEntryService milkEntryService;

    @GetMapping
    public Page<MilkEntryResponse> list(
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @PageableDefault(size = 50, sort = "entryDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return milkEntryService.list(customerId, from, to, pageable);
    }

    @PostMapping
    public ResponseEntity<MilkEntryResponse> create(@Valid @RequestBody MilkEntryRequest req) {
        return ResponseEntity.status(201).body(milkEntryService.create(req));
    }

    @PutMapping("/{id}")
    public MilkEntryResponse update(@PathVariable UUID id, @Valid @RequestBody MilkEntryRequest req) {
        return milkEntryService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        milkEntryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
