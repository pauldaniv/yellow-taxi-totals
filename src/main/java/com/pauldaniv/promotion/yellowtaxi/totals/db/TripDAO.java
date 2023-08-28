package com.pauldaniv.promotion.yellowtaxi.totals.db;

import com.pauldaniv.promotion.yellowtaxi.totals.model.TaxiTripAmountStats;

import java.util.function.Consumer;

public interface TripDAO {
    void processAll(Consumer<TaxiTripAmountStats> forEach);
}
