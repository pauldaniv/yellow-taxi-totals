package com.pauldaniv.promotion.yellowtaxi.totals.db.postgres;

import com.pauldaniv.promotion.yellowtaxi.jooq.Tables;
import com.pauldaniv.promotion.yellowtaxi.model.TaxiTrip;
import com.pauldaniv.promotion.yellowtaxi.totals.db.TripDAO;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TripDAOPostgres implements TripDAO {
    private final DSLContext db;

    @Override
    public List<TaxiTrip> getAll() {
        return db.selectFrom(Tables.TAXI_TRIPS)
                .fetchInto(TaxiTrip.class);
    }
}
