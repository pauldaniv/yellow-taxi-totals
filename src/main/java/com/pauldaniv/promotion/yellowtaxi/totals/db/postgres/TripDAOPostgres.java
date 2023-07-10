package com.pauldaniv.promotion.yellowtaxi.totals.db.postgres;

import com.pauldaniv.promotion.yellowtaxi.jooq.Tables;
import com.pauldaniv.promotion.yellowtaxi.model.TaxiTrip;
import com.pauldaniv.promotion.yellowtaxi.totals.db.TripDAO;
import com.pauldaniv.promotion.yellowtaxi.totals.model.TaxiTripAmountStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

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
    public void processAll(Consumer<TaxiTripAmountStats> forEach) {
        try (final Stream<TaxiTripAmountStats> cursor = db.select(
                        Tables.TAXI_TRIPS.DROP_OFF_YEAR,
                        Tables.TAXI_TRIPS.DROP_OFF_MONTH,
                        Tables.TAXI_TRIPS.DROP_OFF_DAY,
                        Tables.TAXI_TRIPS.TOTAL_AMOUNT)
                .from(Tables.TAXI_TRIPS)
                .fetchStreamInto(TaxiTripAmountStats.class)) {
            cursor.forEach(forEach);
        }
    }
}
