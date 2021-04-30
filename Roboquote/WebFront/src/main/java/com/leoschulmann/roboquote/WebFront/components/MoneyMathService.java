package com.leoschulmann.roboquote.WebFront.components;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MonetaryFunctions;
import org.springframework.stereotype.Service;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class MoneyMathService {
    public MonetaryAmount calculateDiscountedPrice(MonetaryAmount ma, BigDecimal discount) {
        return ma.multiply((BigDecimal.valueOf(100).subtract(discount)).divide(BigDecimal.valueOf(100),
                8, RoundingMode.HALF_UP));
    }

    public MonetaryAmount calculateIncludedTax(MonetaryAmount ma, Integer tax) {
        return ma.multiply(tax / 100.).divide((tax + 100) / 100.);
    }

    public MonetaryAmount getSum(List<MonetaryAmount> monies) {
        return monies.stream().reduce(MonetaryFunctions.sum())
                .orElseGet(() -> Money.of(BigDecimal.ZERO, "EUR"));
    }
}
