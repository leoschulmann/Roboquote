package com.leoschulmann.roboquote.quoteservice.test;

import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import com.leoschulmann.roboquote.quoteservice.services.FileGeneratingService;
import com.leoschulmann.roboquote.quoteservice.services.NameGeneratingService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MonetaryFunctions;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.money.MonetaryAmount;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExcelFileTest {  // removing 'public' prevents junit vintage to start and complain

    Quote quote;
    @Autowired
    public NameGeneratingService nameGenerator;

    @Autowired
    public FileGeneratingService fileGeneratingService;

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
        qs1.setDiscount(10);
        qs2.setDiscount(95);
        quote.addSections(qs2, qs1);
        quote.setDiscount(15);

        qs1.setTotal(Money.of(160, "USD"));
        qs2.setTotal(Money.of(320, "USD"));
        quote.setVat(20);
        MonetaryAmount total = List.of(qs1, qs2).stream().map(QuoteSection::getTotalDiscounted).reduce(MonetaryFunctions.sum()).get();
        quote.setFinalPrice((Money) (total.multiply((100.0 - quote.getDiscount()) / 100)));
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
    @Order(1)
    public void deserializeByteArrAndWrite() throws IOException {
        Files.deleteIfExists(Path.of("./test.xlsx"));
        try (FileOutputStream fos = new FileOutputStream("./test.xlsx")) {
            byte[] arr = fileGeneratingService.generateFile(quote);
            fos.write(arr);
        }
        assertTrue(Files.exists(Path.of("./test.xlsx")));
    }

    @Test
    @Order(2)
    public void excelContents() throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream("./test.xlsx"))) {
            Sheet sheet = workbook.getSheetAt(0);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            Cell testingCell = sheet.getRow(sheet.getLastRowNum() - 1).getCell(4);
            assertEquals(136., evaluator.evaluate(testingCell).getNumberValue());
        }
    }

}
