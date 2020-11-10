package com.leoschulmann.roboquote.WebFront.components;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.format.CurrencyStyle;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.util.Locale;

@Service
public class FrenchCurrencyFormatter implements CurrencyFormatService {
    private MonetaryAmountFormat formatter;

    @PostConstruct
    private void init() {
        formatter = MonetaryFormats.getAmountFormat(
                AmountFormatQueryBuilder
                        .of(Locale.FRANCE)
                        .set(CurrencyStyle.SYMBOL)
                        .build());
    }

    @Override
    public String formatMoney(Money money) {
        return formatter.format(money);
    }
}