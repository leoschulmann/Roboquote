package com.leoschulmann.roboquote.quoteservice.test;

import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import com.leoschulmann.roboquote.quoteservice.repositories.QuoteRepo;
import com.leoschulmann.roboquote.quoteservice.services.NameGeneratingService;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import(NameGeneratingService.class)
public class QuoteJpaTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    QuoteRepo quoteRepo;

    @Autowired
    NameGeneratingService generatingService;


    @Test
    public void saveFullStack() {
        Quote q = new Quote(generatingService.generate(),LocalDate.now().plus(3, ChronoUnit.MONTHS ),
                "randomCust", null, null, null);

        QuoteSection sect1 = new QuoteSection("testname");
        ItemPosition pos = new ItemPosition("item1", "ppp", Money.of(100, "EUR"), 10, 1);
        ItemPosition pos2 = new ItemPosition("item2", "xxx", Money.of(5, "USD"), 2, 42);
        sect1.addItemPositions(pos, pos2);

        QuoteSection sect2 = new QuoteSection("anysection");
        ItemPosition pos3 = new ItemPosition("item3", "sddd", Money.of(112300, "EUR"), 10, 1);
        ItemPosition pos4 = new ItemPosition("item4", "444", Money.of(43, "USD"), 2, 42);
        sect2.addItemPositions(pos3, pos4);

        q.addSections(sect1, sect2);

        int id = (int) entityManager.persistAndGetId(q);
        Quote qFromDB = entityManager.find(Quote.class, id);

        assertEquals("item4", qFromDB.getSections().get(1).getPositions().get(1).getName());
    }

    @Test
    public void testRepo() {
        Quote q = new Quote(generatingService.generate(),LocalDate.now().plus(3, ChronoUnit.MONTHS ),
                "randomCust", null, null, null);

        ItemPosition i = new ItemPosition("sdfsfd", "123", Money.of(1, "USD"), 2, 10);
        QuoteSection qs = new QuoteSection("some section");
        qs.addItemPositions(i);
        q.addSections(qs);


        quoteRepo.save(q);

        Quote found = quoteRepo.findAll().stream().findAny().orElseThrow(() -> new RuntimeException("something went wrong"));
        assertEquals("123", found.getSections().get(0).getPositions().get(0).getPartNo());
    }



    @Test
    public void testDuplicates() {
        Quote q = new Quote(generatingService.generate(),LocalDate.now().plus(3, ChronoUnit.MONTHS ),
                "randomCust", null, null, null);

        QuoteSection sect1 = new QuoteSection("testname");
        ItemPosition pos = new ItemPosition("item1", "ppp", Money.of(100, "EUR"), 10, 1);
        ItemPosition pos2 = new ItemPosition("item2", "xxx", Money.of(5, "USD"), 2, 42);
        sect1.addItemPositions(pos, pos2);

        QuoteSection sect2 = new QuoteSection("anysection");
        ItemPosition pos3 = new ItemPosition("item3", "sddd", Money.of(112300, "EUR"), 10, 1);
        ItemPosition pos4 = new ItemPosition("item4", "444", Money.of(43, "USD"), 2, 42);
        sect2.addItemPositions(pos3, pos4);

        q.addSections(sect1, sect2);

        int id = (int) entityManager.persistAndGetId(q);
        Quote qFromDB = entityManager.find(Quote.class, id);

        assertEquals(2, qFromDB.getSections().size());
    }

}
