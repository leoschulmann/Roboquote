package com.leoschulmann.roboquote.quoteservice.services;

import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class ExcelServiceImplVariantRus implements ExcelService {
    @Override
    public void generateFile(Quote quote, String fileLocation) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(quote.getNumber());
        sheet.setColumnWidth(0, 19400); //75
        sheet.setColumnWidth(1, 1907);  //6.67
        sheet.setColumnWidth(2, 4252);  //15,83
        sheet.setColumnWidth(3, 3572);  //13.17
        sheet.setColumnWidth(4, 4852);  //18.17

        try {
            InputStream fileStream = new FileInputStream(ExcelServiceImplVariantRus.class.getClassLoader().getResource("pholder.png").getFile());
            byte[] bytes = IOUtils.toByteArray(fileStream);
            int pictureId = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
            fileStream.close();
            Drawing<?> drawing = sheet.createDrawingPatriarch();
            XSSFClientAnchor anchor = new XSSFClientAnchor();
            anchor.setCol1(0);
            anchor.setRow1(0);
            Picture picture = drawing.createPicture(anchor, pictureId);
            picture.resize();

        } catch (IOException e) {
            e.printStackTrace();
        }


        XSSFFont titleFont = (XSSFFont) workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeight(11);
        titleFont.setFontName("Calibri");

        CellStyle topTitleStyle = workbook.createCellStyle();
        topTitleStyle.setFont(titleFont);
        topTitleStyle.setBorderBottom(BorderStyle.THIN);

        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setFont(titleFont);

        for (int i = 0; i < 15; i++) {
            Row r = sheet.createRow(i);
            for (int j = 0; j < 5; j++) {
                Cell c = r.createCell(j);
                if (i == 8 && j < 2) c.setCellStyle(topTitleStyle);
                else if (i > 8 && j < 1) c.setCellStyle(titleStyle);
            }
        }

        List<String> qDetails = List.of(
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


        IntStream.range(0, qDetails.size())
                .forEach(i -> sheet.getRow(8 + i)
                        .getCell(0)
                        .setCellValue(qDetails.get(i)));


        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(fileLocation);
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
