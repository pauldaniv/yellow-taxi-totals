package com.pauldaniv.promotion.yellowtaxi.totals;

import com.pauldaniv.promotion.yellowtaxi.totals.services.TotalsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
public class TotalsApplication implements CommandLineRunner {
    private final TotalsService totalsService;
    public static void main(String[] args) {
        SpringApplication.run(TotalsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        totalsService.run();
    }
}
