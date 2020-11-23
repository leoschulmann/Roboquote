package com.leoschulmann.roboquote.quoteservice.test;

import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import com.leoschulmann.roboquote.quoteservice.services.ExcelService;
import com.leoschulmann.roboquote.quoteservice.services.NameGeneratingService;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
 class ExcelFileTest {  // removing 'public' prevents junit vintage to start and complain

    Quote quote;
    @Autowired
    public NameGeneratingService nameGenerator;

    @Autowired
    public ExcelService excelService;

    @BeforeEach
    public void prepareQuote() {
        quote = new Quote(nameGenerator.generate(),
                LocalDate.now().plus(1, ChronoUnit.WEEKS),
                "Test customer",
                "Test dealer",
                "", "");
        QuoteSection qs1 = new QuoteSection("Test section 1");
        ItemPosition ip1 = new ItemPosition("Test item 1", "000", Money.of(100, "USD"),
                1, -1);
        ItemPosition ip2 = new ItemPosition("Test item 2", "001", Money.of(10, "USD"),
                6, -1);

        QuoteSection qs2 = new QuoteSection("Test section 2");
        ItemPosition ip3 = new ItemPosition("Test item 3", "010", Money.of(15, "USD"),
                10, -1);
        ItemPosition ip4 = new ItemPosition("Test item 4", "011", Money.of(85, "USD"),
                2, -1);

        qs1.addItemPositions(ip1, ip2);
        qs2.addItemPositions(ip3, ip4);
        qs2.setDiscount(10);
        quote.addSections(qs1, qs2);
    }

    @Test
    public void nameGeneratorIsWorking() {
        assertNotNull(quote.getNumber());
    }

    @Test
    public void correctDate() {
        assertEquals(LocalDate.now(), quote.getCreated());
    }


    @Test
    public void writeExcelDocument() {
        excelService.generateFile(quote, "./test.xlsx");
        assertTrue(Files.exists(Path.of("./test.xlsx")));
    }

}
