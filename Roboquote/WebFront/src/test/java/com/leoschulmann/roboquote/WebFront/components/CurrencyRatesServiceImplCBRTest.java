package com.leoschulmann.roboquote.WebFront.components;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class CurrencyRatesServiceImplCBRTest {

    @Autowired
    CurrencyRatesServiceImplCBR service;

    @Test
    public void testSimpleFetchRates() {
        service.setTimestamp(null);
        assertTrue(service.getRubEurRate().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(service.getRubUSDRate().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(service.getRubJPYRate().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    public void settingTimeStamp() {
        service.setTimestamp(null);
        service.getRubEurRate();
        assertTrue(service.getTimestamp().isAfter(LocalDateTime.now().minus(1, ChronoUnit.MINUTES)));
    }

    @Test
    public void reducingCbrRuLoad() {
        service.setTimestamp(LocalDateTime.now());
        service.setRubEurRate(new BigDecimal(42));  //my psychological euro rate :-/
        assertEquals(42, service.getRubEurRate().intValue());
    }
}