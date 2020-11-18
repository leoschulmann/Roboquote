package com.leoschulmann.roboquote.WebFront.components;

import javax.money.MonetaryAmount;

public interface CurrencyFormatService {
    String formatMoney(MonetaryAmount money);
}
