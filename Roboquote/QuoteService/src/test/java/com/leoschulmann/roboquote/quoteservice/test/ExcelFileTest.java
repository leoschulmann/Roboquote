package com.leoschulmann.roboquote.quoteservice.test;

import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.services.ExcelService;
import com.leoschulmann.roboquote.quoteservice.services.NameGeneratingService;
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
