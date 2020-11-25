package com.leoschulmann.roboquote.quoteservice.controllers;

import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.services.ExcelService;
import com.leoschulmann.roboquote.quoteservice.services.QuoteService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/download/")
public class DownloadsController {
    private final ExcelService excelService;
    private final QuoteService quoteService;

    public DownloadsController(ExcelService excelService, QuoteService quoteService) {
        this.excelService = excelService;
        this.quoteService = quoteService;
    }

    @GetMapping("/xlsx/{id}")
    ResponseEntity<byte[]> generateXlsxQuote(@PathVariable Integer id) {
        Quote q = quoteService.getQuote(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(excelService.generateFile(q),
                    headers, HttpStatus.OK);
    }
}
