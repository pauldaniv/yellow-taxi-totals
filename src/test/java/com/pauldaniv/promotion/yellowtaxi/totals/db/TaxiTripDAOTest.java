package com.pauldaniv.promotion.yellowtaxi.totals.db;

import com.pauldaniv.promotion.yellowtaxi.jooq.tables.records.TaxiTripsRecord;
import com.pauldaniv.promotion.yellowtaxi.model.TaxiTrip;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;

import static com.pauldaniv.promotion.yellowtaxi.jooq.Tables.TAXI_TRIPS;

@Slf4j
@JooqTest
@ContextConfiguration(classes = DbConfiguration.class)
public class TaxiTripDAOTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private TripDAO taxiTripDAO;
    @Autowired
    private DSLContext db;

    // disable tests for now, so when running on CI/CD there is no migration applied to DB
    // the question is should I propagate the migrations from API service, or just omit the
    // tests entirely...
    // need to give it a thought
    @Test(enabled = false)
    public void getsRecordsSuccessfully() {
        final TaxiTripsRecord taxiTripsRecord = db.newRecord(TAXI_TRIPS);
        taxiTripsRecord.from(TaxiTrip.builder()
                .build());
        db.insertInto(TAXI_TRIPS)
                .set(taxiTripsRecord)
                .returning()
                .fetchOneInto(TaxiTrip.class);
        log.info("Records={}", taxiTripDAO.getAll());
    }
}
