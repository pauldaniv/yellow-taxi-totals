package com.pauldaniv.promotion.yellowtaxi.totals.db;


import com.pauldaniv.promotion.yellowtaxi.model.TaxiTrip;
import com.pauldaniv.promotion.yellowtaxi.totals.model.TaxiTripAmountStats;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface TripDAO {
    List<TaxiTrip> getAll();
    void processAll(Consumer<TaxiTripAmountStats> forEach, int batchSize);
}
