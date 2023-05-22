package com.pauldaniv.promotion.yellowtaxi.totals.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TotalsService {

    @Cacheable("itemCache")
    public String run() {
        try {
            Thread.sleep(120_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String returningValue = String.valueOf(System.currentTimeMillis());
        log.info("msg=returning_value value={}", returningValue);
        return returningValue;
    }
}
