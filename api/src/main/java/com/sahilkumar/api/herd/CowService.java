package com.sahilkumar.api.herd;

import com.sahilkumar.api.common.exception.BusinessException;
import com.sahilkumar.api.common.exception.ResourceNotFoundException;
import com.sahilkumar.api.expense.ExpenseRepository;
import com.sahilkumar.api.herd.dto.CowDetailResponse;
import com.sahilkumar.api.herd.dto.CowRequest;
import com.sahilkumar.api.herd.dto.CowResponse;
import com.sahilkumar.api.herd.dto.CowSummary;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CowService {

    private final CowRepository cowRepository;
    private final CowMilkProductionRepository productionRepository;
    private final CowHealthLogRepository healthLogRepository;
    private final ExpenseRepository expenseRepository;

    public Page<CowResponse> list(String q, HealthStatus status, Pageable pageable) {
        Specification<Cow> spec = Specification
                .where(CowSpecs.tagOrNameContains(q))
                .and(CowSpecs.hasHealthStatus(status));
        return cowRepository.findAll(spec, pageable).map(CowResponse::from);
    }

    public CowResponse get(UUID id) {
        return CowResponse.from(load(id));
    }

    public CowDetailResponse detail(UUID id) {
        Cow cow = load(id);
        YearMonth ym = YearMonth.now();
        LocalDate from = ym.atDay(1);
        LocalDate to = ym.atEndOfMonth();

        BigDecimal monthLiters = productionRepository.sumLitersForCow(id, from, to);
        BigDecimal lifetimeLiters = productionRepository.sumLitersForCowAllTime(id);
        BigDecimal monthHealthCost = healthLogRepository.sumCostForCow(id, from, to);
        BigDecimal monthExpenseTotal = expenseRepository.sumAmountForCow(id, from, to);

        return new CowDetailResponse(
                CowResponse.from(cow),
                nullToZero(monthLiters),
                nullToZero(lifetimeLiters),
                nullToZero(monthHealthCost),
                nullToZero(monthExpenseTotal));
    }

    public CowSummary summary() {
        YearMonth ym = YearMonth.now();
        LocalDate from = ym.atDay(1);
        LocalDate to = ym.atEndOfMonth();

        return new CowSummary(
                cowRepository.count(),
                cowRepository.countByHealthStatus(HealthStatus.HEALTHY),
                cowRepository.countByHealthStatus(HealthStatus.UNDER_TREATMENT),
                cowRepository.countByHealthStatus(HealthStatus.DRY),
                nullToZero(productionRepository.sumLiters(from, to)),
                nullToZero(healthLogRepository.sumCost(from, to)),
                nullToZero(expenseRepository.sumAmountForAnyCow(from, to)));
    }

    @Transactional
    public CowResponse create(CowRequest req) {
        if (cowRepository.existsByTagNo(req.tagNo())) {
            throw new BusinessException("Tag number already exists: " + req.tagNo(), HttpStatus.CONFLICT);
        }
        Cow cow = Cow.builder()
                .tagNo(req.tagNo())
                .name(req.name())
                .breed(req.breed())
                .gender(req.gender())
                .ageMonths(req.ageMonths())
                .healthStatus(req.healthStatus())
                .dailyYieldEstimateLiters(req.dailyYieldEstimateLiters())
                .dateAcquired(req.dateAcquired())
                .notes(req.notes())
                .build();
        return CowResponse.from(cowRepository.save(cow));
    }

    @Transactional
    public CowResponse update(UUID id, CowRequest req) {
        Cow cow = load(id);
        if (!cow.getTagNo().equals(req.tagNo()) && cowRepository.existsByTagNo(req.tagNo())) {
            throw new BusinessException("Tag number already exists: " + req.tagNo(), HttpStatus.CONFLICT);
        }
        cow.setTagNo(req.tagNo());
        cow.setName(req.name());
        cow.setBreed(req.breed());
        cow.setGender(req.gender());
        cow.setAgeMonths(req.ageMonths());
        cow.setHealthStatus(req.healthStatus());
        cow.setDailyYieldEstimateLiters(req.dailyYieldEstimateLiters());
        cow.setDateAcquired(req.dateAcquired());
        cow.setNotes(req.notes());
        return CowResponse.from(cow);
    }

    @Transactional
    public void delete(UUID id) {
        if (!cowRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cow", id);
        }
        cowRepository.deleteById(id);
    }

    Cow load(UUID id) {
        return cowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cow", id));
    }

    private static BigDecimal nullToZero(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
