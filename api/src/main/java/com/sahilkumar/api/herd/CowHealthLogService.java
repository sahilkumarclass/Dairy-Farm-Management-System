package com.sahilkumar.api.herd;

import com.sahilkumar.api.common.exception.ResourceNotFoundException;
import com.sahilkumar.api.herd.dto.CowHealthLogRequest;
import com.sahilkumar.api.herd.dto.CowHealthLogResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CowHealthLogService {

    private final CowHealthLogRepository healthLogRepository;
    private final CowRepository cowRepository;

    public Page<CowHealthLogResponse> listForCow(UUID cowId, Pageable pageable) {
        return healthLogRepository.findByCowIdOrderByEventDateDesc(cowId, pageable)
                .map(CowHealthLogResponse::from);
    }

    public List<CowHealthLogResponse> upcomingDue(int daysAhead) {
        LocalDate from = LocalDate.now();
        LocalDate to = from.plusDays(Math.max(daysAhead, 1));
        return healthLogRepository.findUpcomingDueBetween(from, to).stream()
                .map(CowHealthLogResponse::from)
                .toList();
    }

    @Transactional
    public CowHealthLogResponse create(CowHealthLogRequest req) {
        Cow cow = cowRepository.findById(req.cowId())
                .orElseThrow(() -> new ResourceNotFoundException("Cow", req.cowId()));
        CowHealthLog log = CowHealthLog.builder()
                .cow(cow)
                .eventType(req.eventType())
                .eventDate(req.eventDate())
                .nextDueDate(req.nextDueDate())
                .vetName(req.vetName())
                .cost(req.cost())
                .notes(req.notes())
                .build();
        return CowHealthLogResponse.from(healthLogRepository.save(log));
    }

    @Transactional
    public void delete(UUID id) {
        if (!healthLogRepository.existsById(id)) {
            throw new ResourceNotFoundException("CowHealthLog", id);
        }
        healthLogRepository.deleteById(id);
    }
}
