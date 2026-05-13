package com.sahilkumar.api.customer;

import com.sahilkumar.api.auth.Role;
import com.sahilkumar.api.auth.User;
import com.sahilkumar.api.auth.UserRepository;
import com.sahilkumar.api.common.exception.BusinessException;
import com.sahilkumar.api.common.exception.ResourceNotFoundException;
import com.sahilkumar.api.customer.dto.CreateCustomerLoginRequest;
import com.sahilkumar.api.customer.dto.CustomerRequest;
import com.sahilkumar.api.customer.dto.CustomerResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<CustomerResponse> list(String q, CustomerStatus status, Pageable pageable) {
        Specification<Customer> spec = Specification
                .where(CustomerSpecs.nameOrPhoneContains(q))
                .and(CustomerSpecs.hasStatus(status));
        return customerRepository.findAll(spec, pageable).map(CustomerResponse::from);
    }

    public CustomerResponse get(UUID id) {
        return CustomerResponse.from(load(id));
    }

    @Transactional
    public CustomerResponse create(CustomerRequest req) {
        Customer c = Customer.builder()
                .name(req.name())
                .phone(req.phone())
                .address(req.address())
                .customMilkRate(req.customMilkRate())
                .status(req.status() != null ? req.status() : CustomerStatus.ACTIVE)
                .build();
        return CustomerResponse.from(customerRepository.save(c));
    }

    @Transactional
    public CustomerResponse update(UUID id, CustomerRequest req) {
        Customer c = load(id);
        c.setName(req.name());
        c.setPhone(req.phone());
        c.setAddress(req.address());
        c.setCustomMilkRate(req.customMilkRate());
        if (req.status() != null) {
            c.setStatus(req.status());
        }
        return CustomerResponse.from(c);
    }

    @Transactional
    public void delete(UUID id) {
        Customer c = load(id);
        c.setStatus(CustomerStatus.INACTIVE);
    }

    @Transactional
    public CustomerResponse createLogin(UUID customerId, CreateCustomerLoginRequest req) {
        Customer customer = load(customerId);
        if (customer.getUser() != null) {
            throw new BusinessException("Customer already has a login: " + customer.getUser().getUsername(),
                    HttpStatus.CONFLICT);
        }
        if (userRepository.existsByUsername(req.username())) {
            throw new BusinessException("Username already taken", HttpStatus.CONFLICT);
        }
        User user = User.builder()
                .username(req.username())
                .passwordHash(passwordEncoder.encode(req.password()))
                .fullName(customer.getName())
                .phone(customer.getPhone())
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();
        userRepository.save(user);
        customer.setUser(user);
        return CustomerResponse.from(customer);
    }

    Customer load(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
    }
}
