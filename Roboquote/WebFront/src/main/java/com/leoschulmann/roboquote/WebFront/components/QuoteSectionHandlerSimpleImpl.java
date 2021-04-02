package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MonetaryFunctions;
import org.springframework.stereotype.Service;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class QuoteSectionHandlerSimpleImpl implements QuoteSectionHandler {
    @Override
    public void putToSection(QuoteSection section, ItemPosition ip) {
        Optional<ItemPosition> optional = section.getPositions().stream()
                .filter(pos -> pos.getItemId().equals(ip.getItemId()))
                .findAny();

        if (optional.isPresent()) {
            optional.get().incrementQty();
        } else section.getPositions().add(ip);
    }

    @Override
    public void deletePosition(QuoteSection quoteSection, ItemPosition itemPosition) {
        getOptionalItemPosition(quoteSection, itemPosition).ifPresent(p -> quoteSection.getPositions().remove(p));
    }

    @Override
    public void setQty(QuoteSection quoteSection, ItemPosition itemPosition, Integer value) {
        getOptionalItemPosition(quoteSection, itemPosition).ifPresent(p -> {
            p.setQty(value);
            p.setSellingSum(p.getSellingPrice().multiply(value));
        });
    }

    @Override
    public void setSectionDiscount(QuoteSection quoteSection, Integer discount) {
        quoteSection.setDiscount(discount);
    }

    @Override
    public void setSectionName(QuoteSection quoteSection, String value) {
        quoteSection.setName(value);
    }

    @Override
    public String getSectionName(QuoteSection quoteSection) {
        return quoteSection.getName();
    }

    @Override
    public void updateSubtotalToCurrency(QuoteSection qs, String currency,
                                         BigDecimal euroRate, BigDecimal dollarRate, BigDecimal yenRate, Double conv) {

        MonetaryAmount euros = calcSumByCurrency(qs.getPositions(), "EUR");;
        MonetaryAmount dollars = calcSumByCurrency(qs.getPositions(), "USD");;
        MonetaryAmount yens = calcSumByCurrency(qs.getPositions(), "JPY");;
        MonetaryAmount roubles = calcSumByCurrency(qs.getPositions(), "RUB");
        double charge = conv / 100 + 1.;

        switch (currency) {
            case "RUB": //todo investigate cast
                qs.setTotal((Money) roubles.add(convertToRouble(euros, euroRate, charge))
                        .add(convertToRouble(dollars, dollarRate, charge))
                        .add(convertToRouble(yens, yenRate, charge)));
                break;
            case "EUR":
                qs.setTotal((Money) euros.add(convertFromRouble(roubles, "EUR", euroRate, charge))
                        .add(crossExchange(dollars, "EUR", dollarRate, euroRate, charge))
                        .add(crossExchange(yens, "EUR", yenRate, euroRate, charge)));
                break;
            case "USD":
                qs.setTotal((Money) dollars.add(convertFromRouble(roubles, "USD", dollarRate, charge))
                        .add(crossExchange(euros, "USD", euroRate, dollarRate, charge))
                        .add(crossExchange(yens, "USD", yenRate, dollarRate, charge)));
                break;
            case "JPY":
                qs.setTotal((Money) yens.add(convertFromRouble(roubles, "JPY", yenRate, charge))
                        .add(crossExchange(euros, "JPY", euroRate, yenRate, charge))
                        .add(crossExchange(dollars, "JPY", dollarRate, yenRate, charge)));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + currency);
        }
    }

    private static MonetaryAmount calcSumByCurrency(List<ItemPosition> positions, String currency) {
        return positions.stream()
                .map(ipos -> (MonetaryAmount) ipos.getSellingSum())
                .filter(mon -> mon.getCurrency().getCurrencyCode().equals(currency))
                .reduce(MonetaryFunctions.sum())
                .orElseGet(() -> Money.of(BigDecimal.ZERO, currency));
    }

    private MonetaryAmount convertToRouble(MonetaryAmount monetaryAmount, BigDecimal rate, double charge) {
        if (monetaryAmount.isZero()) return Money.of(BigDecimal.ZERO, "RUB");
        if (monetaryAmount.getCurrency().getCurrencyCode().equals("RUB")) return monetaryAmount;
        return Money.of(monetaryAmount.multiply(rate).multiply(charge).getNumber(), "RUB");
    }

    private MonetaryAmount convertFromRouble(MonetaryAmount rubles, String targetCurrency, BigDecimal rate, double charge) {
        if (rubles.isZero()) return Money.of(BigDecimal.ZERO, targetCurrency);
        if (targetCurrency.equals("RUB")) return rubles;
        return Money.of(rubles.divide(rate).multiply(charge).getNumber(), targetCurrency);
    }

    private MonetaryAmount crossExchange(MonetaryAmount source, String target, BigDecimal sourceRate,
                                         BigDecimal targetRate, double charge) {
        if (source.isZero()) return Money.of(BigDecimal.ZERO, target);
        if (source.getCurrency().getCurrencyCode().equals(target)) return source;
        MonetaryAmount rubs = convertToRouble(source, sourceRate, 1.);
        return convertFromRouble(rubs, target, targetRate, charge);
    }

    private Optional<ItemPosition> getOptionalItemPosition(QuoteSection quoteSection, ItemPosition itemPosition) {
        return quoteSection.getPositions().stream().filter(ip -> ip == itemPosition).findFirst();
    }
}
