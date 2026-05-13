package com.sahilkumar.api.herd;

import com.sahilkumar.api.common.exception.ResourceNotFoundException;
import com.sahilkumar.api.herd.dto.CowMilkProductionRequest;
import com.sahilkumar.api.herd.dto.CowMilkProductionResponse;
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
public class CowMilkProductionService {

    private final CowMilkProductionRepository productionRepository;
    private final CowRepository cowRepository;

    public Page<CowMilkProductionResponse> listForCow(UUID cowId, LocalDate from, LocalDate to, Pageable pageable) {
        LocalDate f = from != null ? from : LocalDate.now().minusMonths(1);
        LocalDate t = to != null ? to : LocalDate.now();
        return productionRepository.findForCowInRange(cowId, f, t, pageable)
                .map(CowMilkProductionResponse::from);
    }

    @Transactional
    public CowMilkProductionResponse create(CowMilkProductionRequest req) {
        Cow cow = cowRepository.findById(req.cowId())
                .orElseThrow(() -> new ResourceNotFoundException("Cow", req.cowId()));
        CowMilkProduction entry = CowMilkProduction.builder()
                .cow(cow)
                .productionDate(req.productionDate())
                .session(req.session())
                .liters(req.liters())
                .notes(req.notes())
                .build();
        return CowMilkProductionResponse.from(productionRepository.save(entry));
    }

    @Transactional
    public void delete(UUID id) {
        if (!productionRepository.existsById(id)) {
            throw new ResourceNotFoundException("CowMilkProduction", id);
        }
        productionRepository.deleteById(id);
    }
}
