package com.pauldaniv.promotion.yellowtaxi.totals;

import com.pauldaniv.promotion.yellowtaxi.totals.service.TotalsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class TotalsApplication implements CommandLineRunner {
    private final TotalsService totalsService;
    public static void main(String[] args) {
        SpringApplication.run(TotalsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info(totalsService.run());
    }
}
