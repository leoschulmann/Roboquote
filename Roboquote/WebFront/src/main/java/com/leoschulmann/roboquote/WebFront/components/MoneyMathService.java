package com.leoschulmann.roboquote.WebFront.components;

import javax.money.MonetaryAmount;
import java.util.List;

public interface MoneyMathService {
    MonetaryAmount calculateDiscountedPrice(MonetaryAmount ma, Integer discount);

    MonetaryAmount calculateIncludedTax(MonetaryAmount ma, Integer tax);

    MonetaryAmount getSum(List<MonetaryAmount> collect);
}
