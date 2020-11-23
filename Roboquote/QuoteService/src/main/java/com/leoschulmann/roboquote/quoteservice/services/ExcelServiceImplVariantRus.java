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
import org.javamoney.moneta.format.CurrencyStyle;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.money.MonetaryAmount;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

@Service
public class ExcelServiceImplVariantRus implements ExcelService {
    private Sheet sheet;
    private Workbook workbook;
    private MonetaryAmountFormat formatter;

    @PostConstruct
    private void init() {
        formatter = MonetaryFormats.getAmountFormat(
                AmountFormatQueryBuilder
                        .of(Locale.FRANCE)
                        .set(CurrencyStyle.SYMBOL)
                        .build());
    }

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

        CellStyle topTitleStyle = getTopCellStyle(getXssfFont());
        CellStyle titleStyle = getCellStyle(getXssfFont());

        for (int i = 0; i < 15; i++) {
            Row r = sheet.createRow(i);
            for (int j = 0; j < 5; j++) {
                Cell c = r.createCell(j);
                if (i == 8 && j < 2) c.setCellStyle(topTitleStyle);
                else if (i > 8 && j < 1) c.setCellStyle(titleStyle);
            }
        }

        List<String> qDetails;
        qDetails = getListOfDetails(quote);

        IntStream.range(0, qDetails.size())
                .forEach(i -> sheet.getRow(8 + i)
                        .getCell(0)
                        .setCellValue(qDetails.get(i)));


        for (QuoteSection section : quote.getSections()) {
            writeSection(section);
        }

        writeFile(fileLocation);
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
        createBlankRows(3);
        int subheaderRowNum = sheet.getLastRowNum() + 1;
        Row subheaderRow = sheet.createRow(subheaderRowNum);
        createBlankCells(subheaderRow, 5);
        CellRangeAddress region = new CellRangeAddress(subheaderRowNum, subheaderRowNum, 0, 4);
        sheet.addMergedRegion(region);
        subheaderRow.getCell(0).setCellValue(section.getName());
        RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderTop(BorderStyle.MEDIUM, region, sheet);
        subheaderRow.getCell(0).setCellStyle(getSubheaderStyle(getXssfFont()));
        section.getPositions().forEach(this::writePosition);
        drawWideLine();
        addSubtotals(0, section.getName(), section.getTotal());
        if (section.getDiscount() != null && section.getDiscount() > 0) {
            addSubtotals(section.getDiscount(), section.getName(), section.getTotalDiscounted());
        }
    }

    private void addSubtotals(int discount, String name, MonetaryAmount money) {
        int idx = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(idx);
        createBlankCells(row, 5);
        sheet.addMergedRegion(new CellRangeAddress(idx, idx, 0, 3));
        String content = (discount > 0 ?
                "ВСЕГО " + name + " (со скидкой " + discount + "%)" :
                "ВСЕГО " + name);
        row.getCell(0).setCellValue(content);
        row.getCell(0).getCellStyle().setAlignment(HorizontalAlignment.RIGHT);
        row.getCell(4).setCellValue(formatter.format(money));
    }


    private void drawWideLine() {
        Row r = sheet.getRow(sheet.getLastRowNum());
        r.cellIterator().forEachRemaining(cell -> cell.setCellStyle(getLinedStyle()));
    }

    private void writePosition(ItemPosition pos) {
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        createBlankCells(row, 5);
        row.getCell(0).setCellValue(pos.getName());
        row.getCell(1).setCellValue(pos.getQty());
        row.getCell(2).setCellValue(pos.getPartNo());
        row.getCell(3).setCellValue(formatter.format(pos.getSellingPrice()));
        row.getCell(4).setCellValue(formatter.format(pos.getSellingSum()));
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

    private CellStyle getCellStyle(XSSFFont titleFont) {
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setFont(titleFont);
        return titleStyle;
    }

    private CellStyle getTopCellStyle(XSSFFont titleFont) {
        CellStyle topTitleStyle = workbook.createCellStyle();
        topTitleStyle.setFont(titleFont);
        topTitleStyle.setBorderBottom(BorderStyle.THIN);
        return topTitleStyle;
    }

    private CellStyle getSubheaderStyle(XSSFFont xssfFont) {
        CellStyle subheaderStyle = workbook.createCellStyle();
        subheaderStyle.setFont(xssfFont);
        subheaderStyle.setAlignment(HorizontalAlignment.CENTER);
        subheaderStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        subheaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return subheaderStyle;
    }

    private CellStyle getLinedStyle() {
        CellStyle underline = workbook.createCellStyle();
        underline.setBorderBottom(BorderStyle.MEDIUM);
        return underline;
    }

    private XSSFFont getXssfFont() {
        XSSFFont titleFont;
        titleFont = (XSSFFont) workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeight(11);
        titleFont.setFontName("Calibri");
        return titleFont;
    }

    private void createBlankRows(int rows) {
        IntStream.range(0, rows).forEach((i) -> sheet.createRow(sheet.getLastRowNum() + i));
    }

    private void createBlankCells(Row row, int cells) {
        IntStream.range(0, cells).forEach(row::createCell);
    }
}
