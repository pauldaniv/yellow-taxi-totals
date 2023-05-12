package com.pauldaniv.promotion.yellowtaxi.totals.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TotalsService {
    public void run(List<String> commands) {
       log.info("msg=running commands={}", commands);
    }
}
