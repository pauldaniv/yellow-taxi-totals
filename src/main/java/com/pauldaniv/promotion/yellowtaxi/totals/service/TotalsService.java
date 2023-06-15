package com.pauldaniv.promotion.yellowtaxi.totals.service;

import com.pauldaniv.promotion.yellowtaxi.totals.db.TripDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TotalsService {
    private final TripDAO tripDAO;

    @Cacheable("itemCache")
    public String run() {
        tripDAO.getAll().forEach(it -> log.info("Taxi Trip: {}", it));
        String returningValue = String.valueOf(System.currentTimeMillis());
        log.info("msg=returning_value value={}", returningValue);
        return returningValue;
    }
}
