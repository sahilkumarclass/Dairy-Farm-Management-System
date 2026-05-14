package com.sahilkumar.api.customer;

import com.sahilkumar.api.customer.dto.CreateCustomerLoginRequest;
import com.sahilkumar.api.customer.dto.CustomerRequest;
import com.sahilkumar.api.customer.dto.CustomerResponse;
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
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('OWNER','STAFF')")
@Tag(name = "Customers")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public Page<CustomerResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) CustomerStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return customerService.list(q, status, pageable);
    }

    @GetMapping("/{id}")
    public CustomerResponse get(@PathVariable UUID id) {
        return customerService.get(id);
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest req) {
        return ResponseEntity.status(201).body(customerService.create(req));
    }

    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable UUID id, @Valid @RequestBody CustomerRequest req) {
        return customerService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/login")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<CustomerResponse> createLogin(
            @PathVariable UUID id,
            @Valid @RequestBody CreateCustomerLoginRequest req) {
        return ResponseEntity.status(201).body(customerService.createLogin(id, req));
    }

    @PostMapping("/{id}/reactivate")
    @PreAuthorize("hasRole('OWNER')")
    public CustomerResponse reactivate(@PathVariable UUID id) {
        return customerService.reactivate(id);
    }

    @DeleteMapping("/{id}/permanent")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> hardDelete(@PathVariable UUID id) {
        customerService.hardDelete(id);
        return ResponseEntity.noContent().build();
    }
}
