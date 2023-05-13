package com.pauldaniv.promotion.yellowtaxi.totals.db.postgres;

import com.pauldaniv.promotion.yellowtaxi.totals.db.TripDAO;
import com.pauldaniv.promotion.yellowtaxi.totals.model.TaxiTrip;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.util.List;

@RequiredArgsConstructor
public class TripDAOPostgres implements TripDAO {
    private final DSLContext db;

    @Override
    public List<TaxiTrip> getAll() {
        return null;
    }
}
