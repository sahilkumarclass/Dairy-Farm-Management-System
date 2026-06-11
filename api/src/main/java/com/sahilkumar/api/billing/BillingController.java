package com.sahilkumar.api.billing;

import com.sahilkumar.api.billing.dto.BillResponse;
import com.sahilkumar.api.billing.dto.GenerateBillsRequest;
import com.sahilkumar.api.billing.dto.PaymentRequest;
import com.sahilkumar.api.billing.dto.PaymentResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
@PreAuthorize("hasRole('OWNER')")
@Tag(name = "Billing")
public class BillingController {

    private final BillingService billingService;

    @GetMapping
    public Page<BillResponse> list(
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) BillStatus status,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @PageableDefault(size = 50) Pageable pageable) {
        return billingService.list(customerId, status, year, month, pageable);
    }

    @GetMapping("/{id}")
    public BillResponse get(@PathVariable UUID id) {
        return billingService.get(id);
    }

    @GetMapping("/{id}/payments")
    public List<PaymentResponse> payments(@PathVariable UUID id) {
        return billingService.payments(id);
    }

    @PostMapping("/{id}/payments")
    public ResponseEntity<PaymentResponse> recordPayment(@PathVariable UUID id,
                                                         @Valid @RequestBody PaymentRequest req) {
        return ResponseEntity.status(201).body(billingService.recordPayment(id, req));
    }

    @PostMapping("/generate")
    @PreAuthorize("hasRole('OWNER')")
    public List<BillResponse> generate(@Valid @RequestBody GenerateBillsRequest req) {
        return billingService.generateForMonth(req.month(), req.year());
    }
}
