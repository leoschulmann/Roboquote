package com.leoschulmann.roboquote.WebFront.components;

import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;

public interface StringFormattingService {
    String getCombined(MonetaryAmount am);

    String getCombinedWithDiscountOrMarkup(MonetaryAmount am, Integer discount);

    String getVat(MonetaryAmount am, Integer discount, Integer vat);

    String getSubtotal(String name, Money total);

    String getSubtotalDisc(String name, Money total, int disc);
}
