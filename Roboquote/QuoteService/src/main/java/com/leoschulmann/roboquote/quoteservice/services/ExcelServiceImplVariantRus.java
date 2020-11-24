package com.leoschulmann.roboquote.quoteservice.services;

import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MonetaryFunctions;
import org.springframework.stereotype.Service;

import javax.money.MonetaryAmount;
import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class ExcelServiceImplVariantRus implements ExcelService {
    private Sheet sheet;
    private Workbook workbook;
    private CellStyle subheaderStyle;
    private CellStyle subTotalStyle;
    private CellStyle regularStyle;
    private CellStyle regularStyleCentered;
    private CellStyle currencyStyle;
    private CellStyle regularStyleUnderlined;
    private CellStyle regularStyleCenteredUnderlined;
    private CellStyle currencyStyleUnderlined;
    private CellStyle currencySubtotalStyle;
    private CellStyle totalStyle;
    private CellStyle currencyTotalStyle;

    @Override
    public void generateFile(Quote quote, String fileLocation) {

        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet(quote.getNumber());
        sheet.setColumnWidth(0, 19400); //75
        sheet.setColumnWidth(1, 1907);  //6.67
        sheet.setColumnWidth(2, 4252);  //15,83
        sheet.setColumnWidth(3, 3572);  //13.17
        sheet.setColumnWidth(4, 4852);  //18.17

        addPicture(ExcelServiceImplVariantRus.class.getClassLoader().getResource("pholder.png").getFile(), 0, 0);

        for (int i = 0; i < 15; i++) {
            Row r = sheet.createRow(i);
            for (int j = 0; j < 5; j++) {
                Cell c = r.createCell(j);
                c.setCellStyle(getTitleStyle());
                if (i == 8 && j < 2) c.getCellStyle().setBorderBottom(BorderStyle.THIN);
            }
        }

        List<String> qDetails;
        qDetails = getListOfDetails(quote);

        IntStream.range(0, qDetails.size())
                .forEach(i -> sheet.getRow(8 + i)
                        .getCell(0)
                        .setCellValue(qDetails.get(i)));

        drawColumnHeaders();

        for (QuoteSection section : quote.getSections()) {
            writeSection(section);
        }

        drawTotals(quote);

        writeFile(fileLocation);
    }

    private void drawColumnHeaders() {
        createBlankRows(1);
        Row r = sheet.getRow(sheet.getLastRowNum());
        createBlankCells(r);
        IntStream.range(0, 5).forEach(i -> r.getCell(i).setCellStyle(getRegularStyleCentered()));
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
                        .append(quote.getVersion())
                        .append(", действительно до ")
                        .append(quote.getValidThru().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))).toString(),

                new StringBuilder("Заказчик: ")
                        .append(quote.getDealer().isBlank() ? quote.getCustomer() : quote.getDealer()).toString(),

                //todo impl shipping terms (ddp, etc)

                new StringBuilder("Условия оплаты: ").append(quote.getPaymentTerms()).toString(),

                new StringBuilder("Гарантия: ").append(quote.getWarranty()).toString(),

                new StringBuilder("Срок поставки: ").append(quote.getShippingTerms()).toString()
        );
        return qDetails;
    }

    private void addPicture(String file, int col1, int row1) {
        try {
            InputStream fileStream = new FileInputStream(file);
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
            e.printStackTrace();
        }
    }

    private void writeSection(QuoteSection section) {
        drawSubheader(section);
        List<ItemPosition> positions = section.getPositions();
        for (int i = 0; i < positions.size(); i++) {
            writePosition(positions.get(i), i == positions.size() - 1);
        }
        drawWideLine();
        drawSubtotals(0, section.getName(), section.getTotal());

        if (section.getDiscount() != null && section.getDiscount() > 0) {
            drawSubtotals(section.getDiscount(), section.getName(), section.getTotalDiscounted());
        }
        createBlankRows(3);
    }

    private void drawSubheader(QuoteSection section) {
        int subheaderRowNum = sheet.getLastRowNum() + 1;
        Row subheaderRow = sheet.createRow(subheaderRowNum);
        createBlankCells(subheaderRow);
        CellRangeAddress region = new CellRangeAddress(subheaderRowNum, subheaderRowNum, 0, 4);
        sheet.addMergedRegion(region);
        subheaderRow.getCell(0).setCellValue(section.getName());
        subheaderRow.getCell(0).setCellStyle(getSubheaderStyle());
        RegionUtil.setBorderTop(BorderStyle.MEDIUM, region, sheet);
    }

    private void drawSubtotals(int discount, String name, MonetaryAmount money) {
        int idx = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(idx);
        createBlankCells(row);
        sheet.addMergedRegion(new CellRangeAddress(idx, idx, 0, 3));
        String content = (discount > 0 ?
                "ВСЕГО " + name + " (со скидкой " + discount + "%)" :
                "ВСЕГО " + name);
        row.getCell(0).setCellValue(content);
        row.getCell(4).setCellValue(money.getNumber().doubleValue());
        row.getCell(0).setCellStyle(getSubtotalStyle());
        row.getCell(4).setCellStyle(getCurrencySubtotalStyle(money.getCurrency().getCurrencyCode().toLowerCase()));
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

        row.getCell(0).setCellStyle(isLast ? getRegularStyleUnderlined() : getRegularStyle());
        row.getCell(1).setCellStyle(isLast ? getRegularStyleCenteredUnderlined() : getRegularStyleCentered());
        row.getCell(2).setCellStyle(isLast ? getRegularStyleCenteredUnderlined() : getRegularStyleCentered());
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

        //todo refactor to FORMULA
        MonetaryAmount sum = quote.getSections().stream()
                .map(QuoteSection::getTotalDiscounted)
                .reduce(MonetaryFunctions.sum())
                .orElseGet(() -> Money.of(0, "EUR"));

        row.getCell(4).setCellValue(sum.getNumber().doubleValue());

        row.getCell(0).setCellStyle(getTotalStyle());
        row.getCell(4).setCellStyle(getTotalCurrencyStyle(sum.getCurrency().getCurrencyCode().toLowerCase()));

        if (quote.getDiscount() != null && quote.getDiscount() > 0) {
            idx++;
            Row discoRow = sheet.createRow(idx);
            createBlankCells(discoRow);
            CellRangeAddress discoRegion = new CellRangeAddress(idx, idx, 0, 3);
            sheet.addMergedRegion(discoRegion);
            discoRow.getCell(0).setCellValue("ИТОГО (со скидкой " + quote.getDiscount() + "%):");
            discoRow.getCell(4).setCellValue(sum.multiply((100 - quote.getDiscount()) / 100.).getNumber().doubleValue());
        discoRow.getCell(0).setCellStyle(getTotalStyle());
        discoRow.getCell(4).setCellStyle(getTotalCurrencyStyle(sum.getCurrency().getCurrencyCode().toLowerCase()));
        }

        idx++;
        Row taxRow = sheet.createRow(idx);
        createBlankCells(taxRow);
        CellRangeAddress taxRegion = new CellRangeAddress(idx, idx, 0, 3);
        sheet.addMergedRegion(taxRegion);
        taxRow.getCell(0).setCellValue("в т.ч. НДС 20%:"); //todo fix hardcoded tax
        taxRow.getCell(4).setCellValue(getTaxSum(sum , quote.getDiscount()));
        taxRow.getCell(0).setCellStyle(getTotalStyle());
        taxRow.getCell(4).setCellStyle(getTotalCurrencyStyle(sum.getCurrency().getCurrencyCode().toLowerCase()));
    }

    private double getTaxSum(MonetaryAmount sum, Integer discount) {
        //todo refactor to FORMULA
        MonetaryAmount source;
        if (discount != null && discount > 0) {
            source = sum.multiply((100 - discount) / 100.);
        } else source = sum;
        return source.multiply(0.2).divide(1.2).getNumber().doubleValueExact();
    }

    private void writeFile(String fileLocation) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(fileLocation);
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
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

    private CellStyle getRegularStyle() {
        if (regularStyle == null) {
            regularStyle = workbook.createCellStyle();
            XSSFFont arial10 = (XSSFFont) workbook.createFont();
            arial10.setBold(false);
            arial10.setFontHeight(10);
            arial10.setFontName("Arial");

            regularStyle.setFont(arial10);
            regularStyle.setAlignment(HorizontalAlignment.LEFT);
        }
        return regularStyle;
    }

    private CellStyle getSubheaderStyle() {
        if (subheaderStyle == null) {
            subheaderStyle = workbook.createCellStyle();
            XSSFFont boldArial10 = (XSSFFont) workbook.createFont();
            boldArial10.setBold(true);
            boldArial10.setFontHeight(10);
            boldArial10.setFontName("Arial");
            subheaderStyle.setFont(boldArial10);
            subheaderStyle.setAlignment(HorizontalAlignment.CENTER);
            subheaderStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            subheaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        return subheaderStyle;
    }

    private CellStyle getRegularStyleCentered() {
        if (regularStyleCentered == null) {
            regularStyleCentered = workbook.createCellStyle();
            XSSFFont arial10 = (XSSFFont) workbook.createFont();
            arial10.setBold(false);
            arial10.setFontHeight(10);
            arial10.setFontName("Arial");

            regularStyleCentered.setFont(arial10);
            regularStyleCentered.setAlignment(HorizontalAlignment.CENTER);
        }
        return regularStyleCentered;
    }

    private CellStyle getCurrencyStyle(String currency) {
        if (currencyStyle == null) {
            currencyStyle = workbook.createCellStyle();
            XSSFFont arial10 = (XSSFFont) workbook.createFont();
            arial10.setBold(false);
            arial10.setFontHeight(10);
            arial10.setFontName("Arial");

            currencyStyle.setFont(arial10);
            currencyStyle.setAlignment(HorizontalAlignment.RIGHT);
            DataFormat dataFormat = workbook.createDataFormat();

            format(currency, currencyStyle, dataFormat);
        }
        return currencyStyle;
    }

    private CellStyle getRegularStyleUnderlined() {
        if (regularStyleUnderlined == null) {
            regularStyleUnderlined = workbook.createCellStyle();
            XSSFFont arial10 = (XSSFFont) workbook.createFont();
            arial10.setBold(false);
            arial10.setFontHeight(10);
            arial10.setFontName("Arial");

            regularStyleUnderlined.setFont(arial10);
            regularStyleUnderlined.setAlignment(HorizontalAlignment.LEFT);
            regularStyleUnderlined.setBorderBottom(BorderStyle.MEDIUM);
        }
        return regularStyleUnderlined;
    }

    private CellStyle getRegularStyleCenteredUnderlined() {
        if (regularStyleCenteredUnderlined == null) {
            regularStyleCenteredUnderlined = workbook.createCellStyle();
            XSSFFont arial10 = (XSSFFont) workbook.createFont();
            arial10.setBold(false);
            arial10.setFontHeight(10);
            arial10.setFontName("Arial");

            regularStyleCenteredUnderlined.setFont(arial10);
            regularStyleCenteredUnderlined.setAlignment(HorizontalAlignment.CENTER);
            regularStyleCenteredUnderlined.setBorderBottom(BorderStyle.MEDIUM);

        }
        return regularStyleCenteredUnderlined;
    }

    private CellStyle getCurrencyStyleUnderlined(String currency) {
        if (currencyStyleUnderlined == null) {
            currencyStyleUnderlined = workbook.createCellStyle();
            XSSFFont arial10 = (XSSFFont) workbook.createFont();
            arial10.setBold(false);
            arial10.setFontHeight(10);
            arial10.setFontName("Arial");

            currencyStyleUnderlined.setFont(arial10);
            currencyStyleUnderlined.setAlignment(HorizontalAlignment.RIGHT);
            currencyStyleUnderlined.setBorderBottom(BorderStyle.MEDIUM);
            DataFormat dataFormat = workbook.createDataFormat();

            format(currency, currencyStyleUnderlined, dataFormat);
        }
        return currencyStyleUnderlined;
    }

    private CellStyle getSubtotalStyle() {
        if (subTotalStyle == null) {
            subTotalStyle = workbook.createCellStyle();
            XSSFFont xssfFont = (XSSFFont) workbook.createFont();
            xssfFont.setBold(true);
            xssfFont.setFontHeight(11);
            xssfFont.setFontName("Arial");
            xssfFont.setBold(true);
            subTotalStyle.setFont(xssfFont);
            subTotalStyle.setAlignment(HorizontalAlignment.RIGHT);
        }
        return subTotalStyle;
    }

    private CellStyle getCurrencySubtotalStyle(String currencyCode) {
        if (currencySubtotalStyle == null) {
            currencySubtotalStyle = workbook.createCellStyle();
            XSSFFont font = (XSSFFont) workbook.createFont();
            font.setFontHeight(11);
            font.setBold(true);
            font.setFontName("Arial");
            currencySubtotalStyle.setFont(font);
            currencySubtotalStyle.setAlignment(HorizontalAlignment.RIGHT);
            DataFormat dataFormat = workbook.createDataFormat();
            format(currencyCode, currencySubtotalStyle, dataFormat);

        }
        return currencySubtotalStyle;
    }

    private CellStyle getTotalStyle() {
                if (totalStyle == null) {
                    totalStyle = workbook.createCellStyle();
                    XSSFFont xssfFont = (XSSFFont) workbook.createFont();
                    xssfFont.setBold(true);
                    xssfFont.setFontHeight(12);
                    xssfFont.setFontName("Arial");
                    xssfFont.setBold(true);
                    totalStyle.setFont(xssfFont);
                    totalStyle.setAlignment(HorizontalAlignment.RIGHT);
                }
                return totalStyle;
    }

    private CellStyle getTotalCurrencyStyle(String currencyCode) {
                if (currencyTotalStyle == null) {
                    currencyTotalStyle = workbook.createCellStyle();
                    XSSFFont font = (XSSFFont) workbook.createFont();
                    font.setFontHeight(12);
                    font.setBold(true);
                    font.setFontName("Arial");
                    currencyTotalStyle.setFont(font);
                    currencyTotalStyle.setAlignment(HorizontalAlignment.RIGHT);
                    DataFormat dataFormat = workbook.createDataFormat();
                    format(currencyCode, currencyTotalStyle, dataFormat);
                }
                return currencyTotalStyle;
    }

    private void format(String currency, CellStyle currStyle, DataFormat dataFormat) {
        switch (currency) {
            case ("usd"):
                currStyle.setDataFormat(dataFormat.getFormat(// this alien crap is right outta MS Excel
                        "_-[$$-en-US]* # ##0.00_ ;_-[$$-en-US]* -# ##0.00\\ ;_-[$$-en-US]* \"-\"??_ ;_-@_ "));
                break;
            case ("eur"):
                currStyle.setDataFormat(dataFormat.getFormat(
                        "_-[$€-x-euro2] * # ##0.00_-;-[$€-x-euro2] * # ##0.00_-;_-[$€-x-euro2] * \"-\"??_-;_-@_-"));
                break;
            case ("jpy"):
                currStyle.setDataFormat(dataFormat.getFormat(
                        "_-* # ##0.00\\ [$JPY]_-;-* # ##0.00\\ [$JPY]_-;_-* \"-\"??\\ [$JPY]_-;_-@_-"));
                break;
            case ("rub"):
                currStyle.setDataFormat(dataFormat.getFormat(
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
