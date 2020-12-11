package com.leoschulmann.roboquote.WebFront.components;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MonetaryFunctions;
import org.springframework.stereotype.Service;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.List;

@Service
public class MoneyMathServiceImpl implements MoneyMathService {
    @Override
    public MonetaryAmount calculateDiscountedPrice(MonetaryAmount ma, Integer discount) {
        return ma.multiply((100.0 - discount) / 100);
    }

    @Override
    public MonetaryAmount calculateIncludedTax(MonetaryAmount ma, Integer tax) {
        return ma.multiply(tax / 100.).divide((tax + 100) / 100.);
    }

    @Override
    public MonetaryAmount getSum(List<MonetaryAmount> monies) {
        return monies.stream().reduce(MonetaryFunctions.sum())
                .orElseGet(() -> Money.of(BigDecimal.ZERO, "EUR"));
    }
}
