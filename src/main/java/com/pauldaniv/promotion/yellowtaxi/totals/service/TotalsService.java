package com.pauldaniv.promotion.yellowtaxi.totals.service;

import com.pauldaniv.promotion.yellowtaxi.totals.db.TripDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPooled;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TotalsService {

    private final TripDAO tripDAO;
    private final JedisPooled jedis;

    public void run() {
        final Map<Integer, BigDecimal> monthTotals = new HashMap<>();
        final Map<String, BigDecimal> dayTotals = new HashMap<>();

        tripDAO.processAll(it -> {
            monthTotals.putIfAbsent(it.getDropOffMonth(), BigDecimal.ZERO);
            monthTotals.computeIfPresent(it.getDropOffMonth(), (key, val) -> val.add(it.getTotalAmount()));
            final String monthAndDay = String.format("%s/%s", it.getDropOffMonth(), it.getDropOffDay());
            dayTotals.putIfAbsent(monthAndDay, BigDecimal.ZERO);
            dayTotals.computeIfPresent(monthAndDay,
                    (key, val) -> val.add(it.getTotalAmount()));
        }, 10_000);

        monthTotals.forEach((key, value) -> jedis.set(String.valueOf(key), String.valueOf(value)));
        dayTotals.forEach((key, value) -> jedis.set(key, String.valueOf(value)));
        log.info("Job complete");
    }
}
