package com.leoschulmann.roboquote.WebFront.components;

import org.javamoney.moneta.Money;

public interface CurrencyFormatService {
    String formatMoney(Money money);
}
