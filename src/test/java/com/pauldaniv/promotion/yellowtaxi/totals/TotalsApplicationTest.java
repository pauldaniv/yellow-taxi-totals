package com.pauldaniv.promotion.yellowtaxi.totals;

import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@Slf4j
public class TotalsApplicationTest extends AbstractTestNGSpringContextTests {
    @Test
    public void loadContext() {
        log.info("msg=loads_contexts");
    }
}
