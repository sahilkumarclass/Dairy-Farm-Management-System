package com.sahilkumar.api.portal;

import com.sahilkumar.api.auth.User;
import com.sahilkumar.api.billing.Bill;
import com.sahilkumar.api.billing.BillRepository;
import com.sahilkumar.api.billing.BillStatus;
import com.sahilkumar.api.billing.dto.BillResponse;
import com.sahilkumar.api.billing.dto.PaymentResponse;
import com.sahilkumar.api.billing.PaymentRepository;
import com.sahilkumar.api.common.exception.ResourceNotFoundException;
import com.sahilkumar.api.customer.Customer;
import com.sahilkumar.api.customer.CustomerRepository;
import com.sahilkumar.api.customer.dto.CustomerResponse;
import com.sahilkumar.api.milk.MilkEntryRepository;
import com.sahilkumar.api.milk.dto.MilkEntryResponse;
import com.sahilkumar.api.portal.dto.CustomerSelfDashboard;
import com.sahilkumar.api.security.CurrentUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
@Tag(name = "Customer Portal")
public class CustomerSelfController {

    private final CurrentUser currentUser;
    private final CustomerRepository customerRepository;
    private final MilkEntryRepository milkEntryRepository;
    private final BillRepository billRepository;
    private final PaymentRepository paymentRepository;

    @GetMapping("/profile")
    public CustomerResponse profile() {
        return CustomerResponse.from(loadCustomer());
    }

    @GetMapping("/dashboard")
    public CustomerSelfDashboard dashboard() {
        Customer customer = loadCustomer();
        YearMonth ym = YearMonth.now();
        LocalDate from = ym.atDay(1);
        LocalDate to = ym.atEndOfMonth();

        BigDecimal monthLiters = milkEntryRepository.sumLitersForCustomer(customer.getId(), from, to);
        BigDecimal monthAmount = milkEntryRepository.sumAmountForCustomer(customer.getId(), from, to);

        BigDecimal outstanding = billRepository.findByCustomerIdOrderByPeriodYearDescPeriodMonthDesc(customer.getId())
                .stream()
                .filter(b -> b.getStatus() != BillStatus.PAID)
                .map(Bill::getRemainingAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BillStatus currentStatus = billRepository
                .findByCustomerIdAndPeriodYearAndPeriodMonth(customer.getId(), ym.getYear(), ym.getMonthValue())
                .map(Bill::getStatus)
                .orElse(null);

        return new CustomerSelfDashboard(
                CustomerResponse.from(customer),
                nullToZero(monthLiters),
                nullToZero(monthAmount),
                outstanding,
                currentStatus);
    }

    @GetMapping("/milk-entries")
    public Page<MilkEntryResponse> milkEntries(
            @PageableDefault(size = 50, sort = "entryDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Customer customer = loadCustomer();
        return milkEntryRepository.search(customer.getId(), null, null, pageable)
                .map(MilkEntryResponse::from);
    }

    @GetMapping("/bills")
    public List<BillResponse> bills() {
        Customer customer = loadCustomer();
        return billRepository.findByCustomerIdOrderByPeriodYearDescPeriodMonthDesc(customer.getId())
                .stream()
                .map(BillResponse::from)
                .toList();
    }

    @GetMapping("/bills/{id}/payments")
    public List<PaymentResponse> billPayments(@PathVariable UUID id) {
        Customer customer = loadCustomer();
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", id));
        if (!bill.getCustomer().getId().equals(customer.getId())) {
            throw new ResourceNotFoundException("Bill", id);
        }
        return paymentRepository.findByBillIdOrderByPaymentDateDesc(id).stream()
                .map(PaymentResponse::from)
                .toList();
    }

    private Customer loadCustomer() {
        User user = currentUser.require();
        return customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No customer is linked to this account. Ask the dairy owner to link it."));
    }

    private static BigDecimal nullToZero(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
