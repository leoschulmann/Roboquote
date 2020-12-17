package com.leoschulmann.roboquote.WebFront.components;

import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.money.MonetaryAmount;

@Service
public class StringFormattingServiceImpl implements StringFormattingService {

    @Autowired
    MoneyMathService moneyMathService;

    @Autowired
    CurrencyFormatService currencyFormatService;

    @Override
    public String getCombined(MonetaryAmount am) {
        return "TOTAL: " + currencyFormatService.formatMoney(am);
    }

    @Override
    public String getCombinedWithDiscountOrMarkup(MonetaryAmount am, Integer discount) {
        if (discount == 0) return "";
        if (discount > 0) {
            return "TOTAL (incl. discount " + discount + "%): "
                    + currencyFormatService.formatMoney(moneyMathService.calculateDiscountedPrice(am, discount));
        }
        return "TOTAL (incl. markup " + Math.abs(discount) + "%): "
                + currencyFormatService.formatMoney(moneyMathService.calculateDiscountedPrice(am, discount));
    }

    @Override
    public String getVat(MonetaryAmount am, Integer discount, Integer vat) {
        if (vat == 0) return "";
        return ("incl. VAT " + vat + "%: " + currencyFormatService.formatMoney(moneyMathService.calculateIncludedTax(
                moneyMathService.calculateDiscountedPrice(am, discount), vat)));
    }

    @Override
    public String getSubtotal(String name, Money total) {
        return "Subtotal " + name + ": " + currencyFormatService.formatMoney(total);
    }

    @Override
    public String getSubtotalDisc(String name, Money subtotal, int discount) {
        if (discount == 0) return "";
        if (discount > 0) {
            return "Subtotal " + name + " (incl. discount " + discount + "%): "
                    + currencyFormatService.formatMoney(moneyMathService.calculateDiscountedPrice(subtotal, discount));
        }
        return "Subtotal " + name + " (incl. markup " + Math.abs(discount) + "%): "
                + currencyFormatService.formatMoney(moneyMathService.calculateDiscountedPrice(subtotal, discount));
    }
}