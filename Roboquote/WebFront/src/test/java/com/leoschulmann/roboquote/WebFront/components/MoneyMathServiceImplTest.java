package com.leoschulmann.roboquote.WebFront.components;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class MoneyMathServiceImplTest {

    @Autowired
    MoneyMathService moneyMathService;


    @Test
    void calculateDiscountedPrice() {
        MonetaryAmount ma = Money.of(100, "USD");
        MonetaryAmount discounted = moneyMathService.calculateDiscountedPrice(ma, BigDecimal.valueOf(10));
        assertEquals(90, discounted.getNumber().intValueExact());
    }

    @Test
    void calculateIncludedTax() {
        MonetaryAmount amountWithTax = Money.of(120, "USD");
        MonetaryAmount tax = moneyMathService.calculateIncludedTax(amountWithTax, 20);
        assertEquals(20., tax.getNumber().doubleValueExact());
    }

    @Test
    void getSum() {
     MonetaryAmount ma =  moneyMathService.getSum(List.of(Money.of(100, "USD"),
                Money.of(100, "USD"), Money.of(100, "USD")));

        assertEquals(300, ma.getNumber().intValueExact());
    }
}