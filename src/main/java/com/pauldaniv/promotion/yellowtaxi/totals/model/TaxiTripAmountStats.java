package com.pauldaniv.promotion.yellowtaxi.totals.model;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxiTripAmountStats {
    @Column(name = "drop_off_year")
    private Integer dropOffYear;
    @Column(name = "drop_off_month")
    private Integer dropOffMonth;
    @Column(name = "drop_off_day")
    private Integer dropOffDay;
    @Column(name = "total_amount")
    private BigDecimal totalAmount;
}
