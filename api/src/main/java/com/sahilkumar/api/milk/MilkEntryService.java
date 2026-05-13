package com.sahilkumar.api.milk;

import com.sahilkumar.api.common.exception.BusinessException;
import com.sahilkumar.api.common.exception.ResourceNotFoundException;
import com.sahilkumar.api.customer.Customer;
import com.sahilkumar.api.customer.CustomerRepository;
import com.sahilkumar.api.milk.dto.MilkEntryRequest;
import com.sahilkumar.api.milk.dto.MilkEntryResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MilkEntryService {

    private final MilkEntryRepository milkEntryRepository;
    private final CustomerRepository customerRepository;

    public Page<MilkEntryResponse> list(UUID customerId, LocalDate from, LocalDate to, Pageable pageable) {
        return milkEntryRepository.search(customerId, from, to, pageable).map(MilkEntryResponse::from);
    }

    @Transactional
    public MilkEntryResponse create(MilkEntryRequest req) {
        Customer customer = customerRepository.findById(req.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", req.customerId()));

        BigDecimal rate = effectiveRate(req.ratePerLiter(), customer);
        BigDecimal total = req.quantityLiters().multiply(rate).setScale(2, RoundingMode.HALF_UP);

        MilkEntry entry = MilkEntry.builder()
                .customer(customer)
                .milkType(req.milkType())
                .quantityLiters(req.quantityLiters())
                .ratePerLiter(rate)
                .totalAmount(total)
                .session(req.session())
                .entryDate(req.entryDate())
                .notes(req.notes())
                .build();
        return MilkEntryResponse.from(milkEntryRepository.save(entry));
    }

    @Transactional
    public MilkEntryResponse update(UUID id, MilkEntryRequest req) {
        MilkEntry entry = milkEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MilkEntry", id));
        Customer customer = entry.getCustomer().getId().equals(req.customerId())
                ? entry.getCustomer()
                : customerRepository.findById(req.customerId())
                        .orElseThrow(() -> new ResourceNotFoundException("Customer", req.customerId()));

        BigDecimal rate = effectiveRate(req.ratePerLiter(), customer);
        BigDecimal total = req.quantityLiters().multiply(rate).setScale(2, RoundingMode.HALF_UP);

        entry.setCustomer(customer);
        entry.setMilkType(req.milkType());
        entry.setQuantityLiters(req.quantityLiters());
        entry.setRatePerLiter(rate);
        entry.setTotalAmount(total);
        entry.setSession(req.session());
        entry.setEntryDate(req.entryDate());
        entry.setNotes(req.notes());
        return MilkEntryResponse.from(entry);
    }

    @Transactional
    public void delete(UUID id) {
        if (!milkEntryRepository.existsById(id)) {
            throw new ResourceNotFoundException("MilkEntry", id);
        }
        milkEntryRepository.deleteById(id);
    }

    private BigDecimal effectiveRate(BigDecimal requestRate, Customer customer) {
        if (requestRate != null && requestRate.signum() > 0) {
            return requestRate;
        }
        if (customer.getCustomMilkRate() != null && customer.getCustomMilkRate().signum() > 0) {
            return customer.getCustomMilkRate();
        }
        throw new BusinessException("No rate provided and customer has no default rate");
    }
}
