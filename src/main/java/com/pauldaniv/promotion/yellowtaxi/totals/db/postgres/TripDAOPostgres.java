package com.pauldaniv.promotion.yellowtaxi.totals.db.postgres;

import com.pauldaniv.promotion.yellowtaxi.jooq.Tables;
import com.pauldaniv.promotion.yellowtaxi.model.TaxiTrip;
import com.pauldaniv.promotion.yellowtaxi.totals.db.TripDAO;
import com.pauldaniv.promotion.yellowtaxi.totals.model.TaxiTripAmountStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TripDAOPostgres implements TripDAO {
    private final DSLContext db;

    @Override
    public List<TaxiTrip> getAll() {
        return db.selectFrom(Tables.TAXI_TRIPS)
                .fetchInto(TaxiTrip.class);
    }

    @Override
    public void processAll(Consumer<TaxiTripAmountStats> forEach, int batchSize) {
        try (Cursor<Record3<Integer, Integer, BigDecimal>> cursor = db.select(
                        Tables.TAXI_TRIPS.DROP_OFF_MONTH,
                        Tables.TAXI_TRIPS.DROP_OFF_DAY,
                        Tables.TAXI_TRIPS.TOTAL_AMOUNT)
                .from(Tables.TAXI_TRIPS)
                .fetchLazy()) {
            int batch = 1;
            while (cursor.hasNext()) {
                log.info("processing_bach batch={}", batch);
                cursor.fetchNext(batchSize)
                        .into(TaxiTripAmountStats.class)
                        .forEach(forEach);
                batch++;
            }
        }
    }
}
