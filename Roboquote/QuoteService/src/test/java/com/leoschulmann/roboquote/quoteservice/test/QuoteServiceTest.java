package com.leoschulmann.roboquote.quoteservice.test;

import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import com.leoschulmann.roboquote.quoteservice.services.QuoteService;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@PropertySource("classpath:application.properties")
class QuoteServiceTest {
    Quote quote;


    @Autowired
    QuoteService quoteService;

    @BeforeEach
    public void initQuote() {
        quote = new Quote("000", LocalDate.now().plus(3, ChronoUnit.MONTHS), "test customer",
                "test dealer", "", "");
        QuoteSection qs1 = new QuoteSection("Test section 1");
        ItemPosition ip1 = new ItemPosition("Test item 1", "000", Money.of(100, "USD"),
                1, -1);
        qs1.addItemPositions(ip1);
        quote.addSections(qs1);
    }

    @Test
    public void simpleQuoteSaveFind() {
        Integer id = quoteService.saveQuote(quote).getId();
        Quote fromDB = quoteService.getQuote(id);
        assertEquals("000", fromDB.getNumber());
    }

    @Test
    public void testQuoteHasItem() {
        Integer id = quoteService.saveQuote(quote).getId();
        Quote fromDB = quoteService.getQuote(id);

        assertEquals("Test item 1", fromDB.getSections().get(0).getPositions().get(0).getName());
    }

}