package com.pauldaniv.promotion.yellowtaxi.totals.db;


import com.pauldaniv.promotion.yellowtaxi.model.TaxiTrip;
import com.pauldaniv.promotion.yellowtaxi.totals.model.TaxiTripAmountStats;

import java.util.List;

public interface TripDAO {
    List<TaxiTrip> getAll();
    List<TaxiTripAmountStats> getAmountStats();
}
