package com.pauldaniv.promotion.yellowtaxi.totals.db;

import com.pauldaniv.promotion.yellowtaxi.totals.model.TaxiTrip;

import java.util.List;

public interface TripDAO {
    List<TaxiTrip> getAll();
}
