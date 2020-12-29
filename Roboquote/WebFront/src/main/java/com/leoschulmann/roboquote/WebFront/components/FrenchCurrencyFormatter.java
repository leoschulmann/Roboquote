package com.leoschulmann.roboquote.WebFront.components;

import org.javamoney.moneta.format.AmountFormatParams;
import org.javamoney.moneta.format.CurrencyStyle;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.money.MonetaryAmount;
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
                        .set(AmountFormatParams.PATTERN, "#,###,###.## ¤")
                        .build());
    }

    @Override
    public String formatMoney(MonetaryAmount money) {
        return formatter.format(money);
    }
}