package com.leoschulmann.roboquote.WebFront.components;

import lombok.RequiredArgsConstructor;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class StringFormattingService {
    private final MoneyMathService moneyMathService;
    private final CurrencyFormatService currencyFormatService;

    public String getCombined(MonetaryAmount am) {
        return "TOTAL: " + currencyFormatService.formatMoney(am);
    }

    public String getCombinedWithDiscountOrMarkup(MonetaryAmount am, BigDecimal discount) {
        if (discount.equals(BigDecimal.ZERO)) return "";
        if (discount.compareTo(BigDecimal.ZERO) > 0) {
            return "TOTAL (incl. discount " + discount.setScale(0, RoundingMode.HALF_UP) + "%): "
                    + currencyFormatService.formatMoney(moneyMathService.calculateDiscountedPrice(am, discount));
        }
        return "TOTAL (incl. markup " + discount.abs().setScale(0, RoundingMode.HALF_UP) + "%): "
                + currencyFormatService.formatMoney(moneyMathService.calculateDiscountedPrice(am, discount));
    }

    public String getVat(MonetaryAmount am, BigDecimal discount, Integer vat) {
        if (vat == 0) return "";
        return ("incl. VAT " + vat + "%: " + currencyFormatService.formatMoney(moneyMathService.calculateIncludedTax(
                moneyMathService.calculateDiscountedPrice(am, discount), vat)));
    }

    public String getSubtotal(String name, Money total) {
        return "Subtotal " + name + ": " + currencyFormatService.formatMoney(total);
    }

    public String getSubtotalDisc(String name, Money subtotal, BigDecimal discount) {
        if (discount.equals(BigDecimal.ZERO)) return "";
        if (discount.compareTo(BigDecimal.ZERO) > 0) {
            return "Subtotal " + name + " (incl. discount " + discount.stripTrailingZeros().toPlainString() + "%): "
                    + currencyFormatService.formatMoney(moneyMathService.calculateDiscountedPrice(subtotal, discount));
        }
        return "Subtotal " + name + " (incl. markup " + discount.abs().stripTrailingZeros().toPlainString() + "%): "
                + currencyFormatService.formatMoney(moneyMathService.calculateDiscountedPrice(subtotal, discount));
    }
}