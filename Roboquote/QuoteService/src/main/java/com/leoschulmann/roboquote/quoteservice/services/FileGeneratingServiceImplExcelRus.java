package com.leoschulmann.roboquote.quoteservice.services;

import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import com.leoschulmann.roboquote.quoteservice.exceptions.CreatingXlsxFileException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;

import javax.money.MonetaryAmount;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service
public class FileGeneratingServiceImplExcelRus implements FileGeneratingService {
    private Sheet sheet;
    private XSSFWorkbook workbook;
    private CellStyle subheaderStyle;
    private CellStyle subTotalStyle;
    private CellStyle regularStyle;
    private CellStyle regularStyleCentered;
    private CellStyle regularStyleUnderlined;
    private CellStyle regularStyleCenteredUnderlined;
    private CellStyle totalStyle;
    private CellStyle titleStyle;
    private CellStyle titleStyleUnderlined;
    private Map<String, CellStyle> currencyStyleMap;
    private Map<String, CellStyle> currencyUnderlinedStyleMap;
    private Map<String, CellStyle> currencySubtotalStyleMap;
    private Map<String, CellStyle> currencyTotalStyleMap;
    private List<String> summarizingCellsAddress;

    @Override
    public byte[] generateFile(Quote quote) {

        workbook = new XSSFWorkbook();
        workbook.getProperties().getExtendedProperties().setApplication("");
        sheet = workbook.createSheet(quote.getNumber());

        currencyStyleMap = new HashMap<>();
        currencyUnderlinedStyleMap = new HashMap<>();
        currencySubtotalStyleMap = new HashMap<>();
        currencyTotalStyleMap = new HashMap<>();
        summarizingCellsAddress = new ArrayList<>();

        titleStyle = getTitleStyle();
        titleStyleUnderlined = getTitleUnderlinedStyle();
        subheaderStyle = getSubheaderStyle();
        subTotalStyle = getSubtotalStyle();
        regularStyle = getRegularStyle();
        regularStyleCentered = getRegularStyleCentered();
        regularStyleUnderlined = getRegularStyleUnderlined();
        regularStyleCenteredUnderlined = getRegularStyleCenteredUnderlined();
        totalStyle = getTotalStyle();

        sheet.setColumnWidth(0, 19400); //75
        sheet.setColumnWidth(1, 1907);  //6.67
        sheet.setColumnWidth(2, 4252);  //15,83
        sheet.setColumnWidth(3, 3572);  //13.17
        sheet.setColumnWidth(4, 4852);  //18.17

        addPicture(FileGeneratingServiceImplExcelRus.class.getClassLoader().getResource("pholder.png").getFile(), 0, 0);

        for (int i = 0; i < 15; i++) {
            Row r = sheet.createRow(i);
            for (int j = 0; j < 5; j++) {
                Cell c = r.createCell(j);
                c.setCellStyle(titleStyle);
                if (i == 8 && j < 2) c.setCellStyle(titleStyleUnderlined);
            }
        }

        List<String> qDetails = getListOfDetails(quote);

        for (int i = 0; i < qDetails.size(); i++) {
            sheet.getRow(8 + i)
                    .getCell(0)
                    .setCellValue(qDetails.get(i));
        }

        drawColumnHeaders();

        for (QuoteSection section : quote.getSections()) {
            writeSection(section);
        }

        drawTotals(quote);

        return writeFileToByteArray();
    }

    @Override
    public String getExtension() {
        return ".xlsx";
    }

    private void drawColumnHeaders() {
        createBlankRows(1);
        Row r = sheet.getRow(sheet.getLastRowNum());
        createBlankCells(r);
        IntStream.range(0, 5).forEach(i -> r.getCell(i).setCellStyle(regularStyleCentered));
        r.getCell(1).setCellValue("Кол-во");
        r.getCell(2).setCellValue("Арт.");
        r.getCell(3).setCellValue("Цена");
        r.getCell(4).setCellValue("Сумма");
    }

    private List<String> getListOfDetails(Quote quote) {
        List<String> qDetails;
        qDetails = List.of(
                new StringBuilder("Коммерческое предложение №")
                        .append(quote.getNumber())
                        .append("-")
                        .append(quote.getVersion())
                        .append(", действительно до ")
                        .append(quote.getValidThru().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))).toString(),

                new StringBuilder("Заказчик: ")
                        .append(quote.getDealer().isBlank() ? quote.getCustomer() : quote.getDealer()).toString(),

                new StringBuilder("Условия поставки: ").append(quote.getInstallation()).toString(),

                new StringBuilder("Условия оплаты: ").append(quote.getPaymentTerms()).toString(),

                new StringBuilder("Гарантия: ").append(quote.getWarranty()).toString(),

                new StringBuilder("Срок поставки: ").append(quote.getShippingTerms()).toString()
        );
        return qDetails;
    }

    private void addPicture(String file, int col1, int row1) {
        try (InputStream fileStream = new FileInputStream(file)) {
            byte[] bytes = IOUtils.toByteArray(fileStream);
            int pictureId = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
            fileStream.close();
            Drawing<?> drawing = sheet.createDrawingPatriarch();
            XSSFClientAnchor anchor = new XSSFClientAnchor();
            anchor.setCol1(col1);
            anchor.setRow1(row1);
            Picture picture = drawing.createPicture(anchor, pictureId);
            picture.resize();
        } catch (IOException e) {
            throw new CreatingXlsxFileException(file);
        }
    }

    private void writeSection(QuoteSection section) {
        drawSubheader(section);
        List<ItemPosition> positions = section.getPositions();
        for (int i = 0; i < positions.size(); i++) {
            writePosition(positions.get(i), i == positions.size() - 1);
        }
        drawWideLine();
        Cell subtotals;
        Cell subtotalsWithDiscount = null;

        //if rounded discount == 0 ...
        if (section.getDiscount().setScale(0, RoundingMode.HALF_UP).equals(BigDecimal.ZERO)) {
            //write section without discount or section without discount but with minor rounding
            subtotals = drawSubtotals(section.getDiscount(), section.getName(), section.getTotalDiscounted());

        } else {
            //write section before discount,
            //write section after discount
            subtotals = drawSubtotals(BigDecimal.ZERO, section.getName(), section.getTotal());
            subtotalsWithDiscount = drawSubtotals(section.getDiscount(), section.getName(), section.getTotalDiscounted());
        }

        summarizingCellsAddress.add(subtotalsWithDiscount == null ? subtotals.getAddress().formatAsString()
                : subtotalsWithDiscount.getAddress().formatAsString());
        createBlankRows(3);
    }

    private void drawSubheader(QuoteSection section) {
        int subheaderRowNum = sheet.getLastRowNum() + 1;
        Row subheaderRow = sheet.createRow(subheaderRowNum);
        createBlankCells(subheaderRow);
        CellRangeAddress region = new CellRangeAddress(subheaderRowNum, subheaderRowNum, 0, 4);
        sheet.addMergedRegion(region);
        subheaderRow.getCell(0).setCellValue(section.getName());
        subheaderRow.getCell(0).setCellStyle(subheaderStyle);
        RegionUtil.setBorderTop(BorderStyle.MEDIUM, region, sheet);
    }

    private Cell drawSubtotals(BigDecimal discount, String name, MonetaryAmount money) {
        int idx = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(idx);
        createBlankCells(row);
        sheet.addMergedRegion(new CellRangeAddress(idx, idx, 0, 3));
        String content;

        if (discount.setScale(0, RoundingMode.HALF_UP).equals(BigDecimal.ZERO)) content = "ВСЕГО " + name;
        else if (discount.compareTo(BigDecimal.ZERO) > 0) {
            content = "ВСЕГО " + name + " (со скидкой " + discount.setScale(0, RoundingMode.HALF_UP) + "%)";
        } else content = "ВСЕГО " + name + " (с наценкой " + discount.abs().setScale(0, RoundingMode.HALF_UP) + "%)";

        double value = ((Money) money).getNumberStripped().setScale(2, RoundingMode.HALF_UP).doubleValue();
        row.getCell(0).setCellValue(content);
        row.getCell(4).setCellValue(value);
        row.getCell(0).setCellStyle(subTotalStyle);
        row.getCell(4).setCellStyle(getCurrencySubtotalStyle(money.getCurrency().getCurrencyCode().toLowerCase()));
        return row.getCell(4);
    }

    private void drawWideLine() {
        Row r = sheet.getRow(sheet.getLastRowNum());
        r.cellIterator().forEachRemaining(cell -> {
            CellStyle cs = cell.getCellStyle();
            cs.setBorderBottom(BorderStyle.MEDIUM);
        });
    }

    private void writePosition(ItemPosition pos, boolean isLast) {
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        createBlankCells(row);
        row.getCell(0).setCellValue(pos.getName());
        row.getCell(1).setCellValue(pos.getQty());
        row.getCell(2).setCellValue(pos.getPartNo());
        row.getCell(3).setCellValue(pos.getSellingPrice().getNumberStripped().doubleValue());
        row.getCell(4).setCellValue(pos.getSellingSum().getNumberStripped().doubleValue());

        row.getCell(0).setCellStyle(isLast ? regularStyleUnderlined : regularStyle);
        row.getCell(1).setCellStyle(isLast ? regularStyleCenteredUnderlined : regularStyleCentered);
        row.getCell(2).setCellStyle(isLast ? regularStyleCenteredUnderlined : regularStyleCentered);
        row.getCell(3).setCellStyle(isLast ? getCurrencyStyleUnderlined(
                pos.getSellingPrice().getCurrency().getCurrencyCode().toLowerCase()) :
                getCurrencyStyle(pos.getSellingPrice().getCurrency().getCurrencyCode().toLowerCase()));
        row.getCell(4).setCellStyle(isLast ? getCurrencyStyleUnderlined(
                pos.getSellingSum().getCurrency().getCurrencyCode().toLowerCase()) :
                getCurrencyStyle(pos.getSellingSum().getCurrency().getCurrencyCode().toLowerCase()));
    }

    private void drawTotals(Quote quote) {
        int idx = sheet.getLastRowNum();
        Row row = sheet.getRow(idx);
        createBlankCells(row);
        CellRangeAddress region = new CellRangeAddress(idx, idx, 0, 3);
        sheet.addMergedRegion(region);
        row.getCell(0).setCellValue("ИТОГО:");

        String currency = quote.getFinalPrice().getCurrency().getCurrencyCode();

        Cell totalCell = row.getCell(4);
        totalCell.setCellFormula(String.join("+", summarizingCellsAddress));

        row.getCell(0).setCellStyle(totalStyle);
        totalCell.setCellStyle(getTotalCurrencyStyle(currency.toLowerCase()));

        if (quote.getDiscount() != 0) {
            idx++;
            Row discoRow = sheet.createRow(idx);
            createBlankCells(discoRow);
            CellRangeAddress discoRegion = new CellRangeAddress(idx, idx, 0, 3);
            sheet.addMergedRegion(discoRegion);
            discoRow.getCell(0).setCellValue(quote.getDiscount() > 0 ?
                    "ИТОГО (со скидкой " + quote.getDiscount() + "%):"
                    : "ИТОГО (со наценкой " + Math.abs(quote.getDiscount()) + "%):"
            );
            String formula = totalCell.getAddress().formatAsString() + "* (100 - " + quote.getDiscount() + ")/100";

            discoRow.getCell(4).setCellFormula(formula);
            totalCell = discoRow.getCell(4);

            discoRow.getCell(0).setCellStyle(totalStyle);
            discoRow.getCell(4).setCellStyle(getTotalCurrencyStyle(currency.toLowerCase()));
        }

        if (quote.getVat() != 0) {
            idx++;
            Row taxRow = sheet.createRow(idx);
            createBlankCells(taxRow);
            CellRangeAddress taxRegion = new CellRangeAddress(idx, idx, 0, 3);
            sheet.addMergedRegion(taxRegion);
            taxRow.getCell(0).setCellValue("(в т.ч. НДС " + quote.getVat() + "%):");

            String formula = totalCell.getAddress().formatAsString()
                    + " * (" + quote.getVat() + "/100) / ((" + quote.getVat() + "/100) +1 )";
            taxRow.getCell(4).setCellFormula(formula);
            taxRow.getCell(0).setCellStyle(totalStyle);
            taxRow.getCell(4).setCellStyle(getTotalCurrencyStyle(currency.toLowerCase()));
        }
    }

    private byte[] writeFileToByteArray() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            workbook.close();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new CreatingXlsxFileException();
        }
    }

    private CellStyle getTitleStyle() {
        CellStyle titleStyle = workbook.createCellStyle();
        XSSFFont titleFont = (XSSFFont) workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeight(11);
        titleFont.setFontName("Calibri");
        titleStyle.setFont(titleFont);
        return titleStyle;
    }

    private CellStyle getTitleUnderlinedStyle() {
        CellStyle cs = workbook.createCellStyle();
        XSSFFont titleFont = (XSSFFont) workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeight(11);
        titleFont.setFontName("Calibri");
        cs.setFont(titleFont);
        cs.setBorderBottom(BorderStyle.THIN);
        return cs;
    }

    private CellStyle getRegularStyle() {
        CellStyle style = workbook.createCellStyle();
        XSSFFont arial10 = (XSSFFont) workbook.createFont();
        arial10.setBold(false);
        arial10.setFontHeight(10);
        arial10.setFontName("Arial");
        style.setFont(arial10);
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.LEFT);
        return style;
    }

    private CellStyle getSubheaderStyle() {
        CellStyle style = workbook.createCellStyle();
        XSSFFont boldArial10 = (XSSFFont) workbook.createFont();
        boldArial10.setBold(true);
        boldArial10.setFontHeight(10);
        boldArial10.setFontName("Arial");
        style.setFont(boldArial10);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle getRegularStyleCentered() {
        CellStyle regularStyleCentered = workbook.createCellStyle();
        XSSFFont arial10 = (XSSFFont) workbook.createFont();
        arial10.setBold(false);
        arial10.setFontHeight(10);
        arial10.setFontName("Arial");
        regularStyleCentered.setFont(arial10);
        regularStyleCentered.setAlignment(HorizontalAlignment.CENTER);
        return regularStyleCentered;
    }

    private CellStyle getCurrencyStyle(String currency) {
        return currencyStyleMap.computeIfAbsent(currency, s -> {
            CellStyle cs = workbook.createCellStyle();
            XSSFFont arial10 = (XSSFFont) workbook.createFont();
            arial10.setBold(false);
            arial10.setFontHeight(10);
            arial10.setFontName("Arial");
            cs.setFont(arial10);
            cs.setAlignment(HorizontalAlignment.RIGHT);
            applyFormat(currency, cs);
            return cs;
        });
    }

    private CellStyle getRegularStyleUnderlined() {
        CellStyle cs = workbook.createCellStyle();
        XSSFFont arial10 = (XSSFFont) workbook.createFont();
        arial10.setBold(false);
        arial10.setFontHeight(10);
        arial10.setFontName("Arial");

        cs.setFont(arial10);
        cs.setWrapText(true);
        cs.setAlignment(HorizontalAlignment.LEFT);
        cs.setBorderBottom(BorderStyle.MEDIUM);
        return cs;
    }

    private CellStyle getRegularStyleCenteredUnderlined() {
        CellStyle regularStyleCenteredUnderlined = workbook.createCellStyle();
        XSSFFont arial10 = (XSSFFont) workbook.createFont();
        arial10.setBold(false);
        arial10.setFontHeight(10);
        arial10.setFontName("Arial");

        regularStyleCenteredUnderlined.setFont(arial10);
        regularStyleCenteredUnderlined.setAlignment(HorizontalAlignment.CENTER);
        regularStyleCenteredUnderlined.setBorderBottom(BorderStyle.MEDIUM);
        return regularStyleCenteredUnderlined;
    }

    private CellStyle getCurrencyStyleUnderlined(String currency) {
        return currencyUnderlinedStyleMap.computeIfAbsent(currency, s -> {
            CellStyle cs = workbook.createCellStyle();
            XSSFFont arial10 = (XSSFFont) workbook.createFont();
            arial10.setBold(false);
            arial10.setFontHeight(10);
            arial10.setFontName("Arial");
            cs.setFont(arial10);
            cs.setAlignment(HorizontalAlignment.RIGHT);
            cs.setBorderBottom(BorderStyle.MEDIUM);
            applyFormat(currency, cs);
            return cs;
        });
    }

    private CellStyle getSubtotalStyle() {
        CellStyle cs = workbook.createCellStyle();
        XSSFFont xssfFont = (XSSFFont) workbook.createFont();
        xssfFont.setBold(true);
        xssfFont.setFontHeight(11);
        xssfFont.setFontName("Arial");
        xssfFont.setBold(true);
        cs.setFont(xssfFont);
        cs.setAlignment(HorizontalAlignment.RIGHT);
        return cs;
    }

    private CellStyle getCurrencySubtotalStyle(String currencyCode) {
        return currencySubtotalStyleMap.computeIfAbsent(currencyCode, s -> {
            CellStyle cs = workbook.createCellStyle();
            XSSFFont font = (XSSFFont) workbook.createFont();
            font.setFontHeight(11);
            font.setBold(true);
            font.setFontName("Arial");
            cs.setFont(font);
            cs.setAlignment(HorizontalAlignment.RIGHT);
            DataFormat dataFormat = workbook.createDataFormat();
            applyFormat(currencyCode, cs);
            return cs;
        });
    }

    private CellStyle getTotalStyle() {
        CellStyle cs = workbook.createCellStyle();
        XSSFFont xssfFont = (XSSFFont) workbook.createFont();
        xssfFont.setBold(true);
        xssfFont.setFontHeight(12);
        xssfFont.setFontName("Arial");
        xssfFont.setBold(true);
        cs.setFont(xssfFont);
        cs.setAlignment(HorizontalAlignment.RIGHT);
        return cs;
    }

    private CellStyle getTotalCurrencyStyle(String currencyCode) {
        return currencyTotalStyleMap.computeIfAbsent(currencyCode, s -> {
            CellStyle cs = workbook.createCellStyle();
            XSSFFont font = (XSSFFont) workbook.createFont();
            font.setFontHeight(12);
            font.setBold(true);
            font.setFontName("Arial");
            cs.setFont(font);
            cs.setAlignment(HorizontalAlignment.RIGHT);
            DataFormat dataFormat = workbook.createDataFormat();
            applyFormat(currencyCode, cs);
            return cs;
        });
    }

    private void applyFormat(String currency, CellStyle currStyle) {
        switch (currency) {
            case ("usd"):
                currStyle.setDataFormat(workbook.createDataFormat().getFormat(// this alien crap is right outta MS Excel
                        "_-[$$-en-US]* # ##0.00_ ;_-[$$-en-US]* -# ##0.00\\ ;_-[$$-en-US]* \"-\"??_ ;_-@_ "));
                break;
            case ("eur"):
                currStyle.setDataFormat(workbook.createDataFormat().getFormat(
                        "_-[$€-x-euro2] * # ##0.00_-;-[$€-x-euro2] * # ##0.00_-;_-[$€-x-euro2] * \"-\"??_-;_-@_-"));
                break;
            case ("jpy"):
                currStyle.setDataFormat(workbook.createDataFormat().getFormat(
                        "_-* # ##0.00\\ [$JPY]_-;-* # ##0.00\\ [$JPY]_-;_-* \"-\"??\\ [$JPY]_-;_-@_-"));
                break;
            case ("rub"):
                currStyle.setDataFormat(workbook.createDataFormat().getFormat(
                        "_-* # ##0.00\\ [$₽-ru-RU]_-;-* # ##0.00\\ [$₽-ru-RU]_-;_-* \"-\"??\\ [$₽-ru-RU]_-;_-@_-"));
        }
    }

    private void createBlankRows(int rows) {
        int startIdx = sheet.getLastRowNum() + 1;
        IntStream.range(0, rows).forEach((i) -> sheet.createRow(startIdx + i));
    }

    private void createBlankCells(Row row) {
        IntStream.range(0, 5).forEach(row::createCell);
    }
}
