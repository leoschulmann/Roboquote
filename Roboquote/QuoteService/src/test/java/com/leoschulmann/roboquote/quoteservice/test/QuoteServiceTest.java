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
import java.util.UUID;

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


    @Test
    public void testBigQuoteForDuplicates() {
        Quote quote = new Quote("num", LocalDate.now().plus(10, ChronoUnit.DAYS), "Test customer", null, null, null);
        quote.setWarranty("test warranty");
        quote.setPaymentTerms("test payment");
        quote.setShippingTerms("test shipping");
        quote.setDiscount(10);

        QuoteSection qs1 = new QuoteSection("Section 1");
        QuoteSection qs2 = new QuoteSection("Section 2");
        QuoteSection qs3 = new QuoteSection("Section 3");

        ItemPosition ip1 = new ItemPosition(UUID.randomUUID().toString(), "0", Money.of(100, "EUR"), 10, -1);
        ItemPosition ip2 = new ItemPosition(UUID.randomUUID().toString(), "0", Money.of(200, "EUR"), 9, -1);
        ItemPosition ip3 = new ItemPosition(UUID.randomUUID().toString(), "0", Money.of(300, "EUR"), 8, -1);
        ItemPosition ip4 = new ItemPosition(UUID.randomUUID().toString(), "0", Money.of(400, "EUR"), 7, -1);
        ItemPosition ip5 = new ItemPosition(UUID.randomUUID().toString(), "0", Money.of(500, "EUR"), 6, -1);
        ItemPosition ip6 = new ItemPosition(UUID.randomUUID().toString(), "0", Money.of(600, "EUR"), 5, -1);
        ItemPosition ip7 = new ItemPosition(UUID.randomUUID().toString(), "0", Money.of(700, "EUR"), 4, -1);
        ItemPosition ip8 = new ItemPosition(UUID.randomUUID().toString(), "0", Money.of(800, "EUR"), 3, -1);
        ItemPosition ip9 = new ItemPosition(UUID.randomUUID().toString(), "0", Money.of(900, "EUR"), 2, -1);

        qs1.addItemPositions(ip1, ip2, ip3);
        qs1.setDiscount(5);

        qs2.addItemPositions(ip4, ip5, ip6);
        qs2.setDiscount(10);

        qs3.addItemPositions(ip7, ip8, ip9);
        qs3.setDiscount(15);

        quote.addSections(qs1, qs2, qs3);

        Integer id = quoteService.saveQuote(quote).getId();
        Quote fromDB = quoteService.getQuote(id);

        assertEquals(3, fromDB.getSections().size());

    }

}