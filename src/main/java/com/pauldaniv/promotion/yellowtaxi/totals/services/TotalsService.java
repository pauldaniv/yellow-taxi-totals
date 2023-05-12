package com.pauldaniv.promotion.yellowtaxi.totals.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class TotalsService {

    @Cacheable("itemCache")
    public String run() {
        return String.valueOf(System.currentTimeMillis());
    }
}
