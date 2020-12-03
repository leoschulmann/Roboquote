package com.leoschulmann.roboquote.WebFront.components;

import java.math.BigDecimal;

public interface CurrencyRatesService {

    BigDecimal getRubEurRate();

    BigDecimal getRubUSDRate();

    BigDecimal getRubJPYRate();
}
