package com.sahilkumar.api.billing;

import com.sahilkumar.api.billing.dto.BillResponse;
import com.sahilkumar.api.billing.dto.PaymentRequest;
import com.sahilkumar.api.billing.dto.PaymentResponse;
import com.sahilkumar.api.common.exception.BusinessException;
import com.sahilkumar.api.common.exception.ResourceNotFoundException;
import com.sahilkumar.api.customer.Customer;
import com.sahilkumar.api.customer.CustomerRepository;
import com.sahilkumar.api.customer.CustomerStatus;
import com.sahilkumar.api.milk.MilkEntry;
import com.sahilkumar.api.milk.MilkEntryRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BillingService {

    private final BillRepository billRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final MilkEntryRepository milkEntryRepository;

    public Page<BillResponse> list(UUID customerId, BillStatus status, Integer year, Integer month, Pageable pageable) {
        return billRepository.search(customerId, status, year, month, pageable).map(BillResponse::from);
    }

    public BillResponse get(UUID id) {
        return BillResponse.from(loadBill(id));
    }

    public List<PaymentResponse> payments(UUID billId) {
        return paymentRepository.findByBillIdOrderByPaymentDateDesc(billId).stream()
                .map(PaymentResponse::from)
                .toList();
    }

    @Transactional
    public List<BillResponse> generateForMonth(int month, int year) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate from = ym.atDay(1);
        LocalDate to = ym.atEndOfMonth();

        List<Customer> activeCustomers = customerRepository.findAll().stream()
                .filter(c -> c.getStatus() == CustomerStatus.ACTIVE)
                .toList();

        List<BillResponse> generated = new ArrayList<>();
        for (Customer c : activeCustomers) {
            List<MilkEntry> entries = milkEntryRepository
                    .findByCustomerIdAndEntryDateBetween(c.getId(), from, to);
            if (entries.isEmpty()) {
                continue;
            }
            BigDecimal totalLiters = entries.stream()
                    .map(MilkEntry::getQuantityLiters)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalAmount = entries.stream()
                    .map(MilkEntry::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);

            Bill bill = billRepository
                    .findByCustomerIdAndPeriodYearAndPeriodMonth(c.getId(), year, month)
                    .orElseGet(() -> Bill.builder()
                            .customer(c)
                            .periodYear(year)
                            .periodMonth(month)
                            .paidAmount(BigDecimal.ZERO)
                            .generatedAt(OffsetDateTime.now())
                            .build());

            bill.setTotalLiters(totalLiters);
            bill.setTotalAmount(totalAmount);
            bill.setRemainingAmount(totalAmount.subtract(
                    bill.getPaidAmount() == null ? BigDecimal.ZERO : bill.getPaidAmount()));
            bill.setStatus(deriveStatus(bill.getPaidAmount(), totalAmount));
            generated.add(BillResponse.from(billRepository.save(bill)));
        }
        log.info("Generated/refreshed {} bills for {}-{}", generated.size(), year, month);
        return generated;
    }

    @Transactional
    public PaymentResponse recordPayment(UUID billId, PaymentRequest req) {
        Bill bill = loadBill(billId);
        BigDecimal newPaid = bill.getPaidAmount().add(req.amount());
        if (newPaid.compareTo(bill.getTotalAmount()) > 0) {
            throw new BusinessException("Payment exceeds bill total");
        }
        Payment p = Payment.builder()
                .bill(bill)
                .amount(req.amount())
                .paymentMethod(req.paymentMethod())
                .paymentDate(req.paymentDate())
                .reference(req.reference())
                .notes(req.notes())
                .build();
        paymentRepository.save(p);

        bill.setPaidAmount(newPaid);
        bill.setRemainingAmount(bill.getTotalAmount().subtract(newPaid));
        bill.setStatus(deriveStatus(newPaid, bill.getTotalAmount()));
        return PaymentResponse.from(p);
    }

    // Monthly generation: 03:00 on the 1st of every month for the previous month.
    @Scheduled(cron = "0 0 3 1 * *")
    @Transactional
    public void scheduledMonthlyGeneration() {
        YearMonth previous = YearMonth.now().minusMonths(1);
        log.info("Scheduled bill generation for {}", previous);
        generateForMonth(previous.getMonthValue(), previous.getYear());
    }

    private static BillStatus deriveStatus(BigDecimal paid, BigDecimal total) {
        if (paid == null || paid.signum() == 0) return BillStatus.UNPAID;
        if (paid.compareTo(total) >= 0) return BillStatus.PAID;
        return BillStatus.PARTIAL;
    }

    private Bill loadBill(UUID id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", id));
    }
}
