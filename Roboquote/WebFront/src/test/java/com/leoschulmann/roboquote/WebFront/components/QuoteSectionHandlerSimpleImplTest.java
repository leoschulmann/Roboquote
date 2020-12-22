package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
class QuoteSectionHandlerSimpleImplTest {

    @Autowired
    QuoteSectionHandler handler;


    @Test
    void putToSection() {
        QuoteSection quoteSection = new QuoteSection("");
        ItemPosition ip = new ItemPosition("", "", Money.of(1, "USD"), 1, 1);

        handler.putToSection(quoteSection, ip);
        handler.putToSection(quoteSection, ip);

        assertEquals(2, quoteSection.getPositions().get(0).getQty());
    }

    @Test
    void updateSubtotalToCurrency() {
        QuoteSection quoteSection = new QuoteSection("");
        ItemPosition dollar = new ItemPosition("", "", Money.of(1, "USD"), 1, 1);
        ItemPosition euro = new ItemPosition("", "", Money.of(1, "EUR"), 1, 1);
        ItemPosition hundredYen = new ItemPosition("", "", Money.of(1, "JPY"), 100, 1);
        quoteSection.addItemPositions(dollar, euro, hundredYen);
        handler.updateSubtotalToCurrency(quoteSection, "RUB", new BigDecimal(100),
                new BigDecimal(100), BigDecimal.ONE, 5.);

        assertEquals("RUB", quoteSection.getTotal().getCurrency().getCurrencyCode());
        assertEquals(315, quoteSection.getTotal().getNumber().intValueExact());
    }
}