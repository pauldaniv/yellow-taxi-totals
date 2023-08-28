package com.pauldaniv.promotion.yellowtaxi.totals.service;

import com.pauldaniv.promotion.yellowtaxi.totals.db.TripDAO;
import com.pauldaniv.promotion.yellowtaxi.totals.model.TaxiTripAmountStats;
import lombok.extern.slf4j.Slf4j;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import redis.clients.jedis.JedisPooled;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@Slf4j
public class TotalsServiceTest {

    @Mock
    private TripDAO tripDAO;
    @Mock
    private JedisPooled jedisPooled;

    private TotalsService totalsService;

    @BeforeTest
    public void setup() {
        MockitoAnnotations.openMocks(this);
        totalsService = new TotalsService(tripDAO, jedisPooled);
    }

    @SuppressWarnings("unchecked")
    @Test(dataProvider = "taxi_trip_records")
    public void runsJobSuccessfully(List<TaxiTripAmountStats> taxiTripRecords) {
        doAnswer(invocation -> {
            taxiTripRecords.forEach(it -> ((Consumer<TaxiTripAmountStats>) invocation.getArguments()[0]).accept(it));
            return null;
        }).when(tripDAO).processAll(any());
        totalsService.run();
        verify(jedisPooled).set("1", "369");
        verify(jedisPooled).set("2", "123");
        verify(jedisPooled).set("1/1", "123");
        verify(jedisPooled).set("1/2", "123");
        verify(jedisPooled).set("1/3", "123");
        verify(jedisPooled).set("2/3", "123");
    }

    @DataProvider(name = "taxi_trip_records")
    public Object[][] taxiTrips() {
        return new Object[][]{
                {List.of(TaxiTripAmountStats.builder()
                                .totalAmount(new BigDecimal("123"))
                                .dropOffDay(1)
                                .dropOffMonth(1)
                                .dropOffYear(2018).build(),
                        TaxiTripAmountStats.builder()
                                .totalAmount(new BigDecimal("123"))
                                .dropOffDay(2)
                                .dropOffMonth(1)
                                .dropOffYear(2018).build(),
                        TaxiTripAmountStats.builder()
                                .totalAmount(new BigDecimal("123"))
                                .dropOffDay(3)
                                .dropOffMonth(1)
                                .dropOffYear(2018).build(),
                        TaxiTripAmountStats.builder()
                                .totalAmount(new BigDecimal("123"))
                                .dropOffDay(3)
                                .dropOffMonth(2)
                                .dropOffYear(2018).build())
                }
        };
    }
}
