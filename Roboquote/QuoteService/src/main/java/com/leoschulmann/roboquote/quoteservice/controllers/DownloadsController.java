package com.leoschulmann.roboquote.quoteservice.controllers;

import com.leoschulmann.roboquote.quoteservice.dto.XlsxDataObject;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.services.FileGeneratingService;
import com.leoschulmann.roboquote.quoteservice.services.QuoteService;
import com.leoschulmann.roboquote.quoteservice.validation.ExistingQuote;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/download/")
@RequiredArgsConstructor
public class DownloadsController {
    private final FileGeneratingService fileGeneratingService;
    private final QuoteService quoteService;

    @GetMapping("/xlsx/{id}")
    ResponseEntity<XlsxDataObject> generateXlsxQuote(@PathVariable @ExistingQuote Integer id) {
        Quote q = quoteService.getQuote(id);
        XlsxDataObject data = new XlsxDataObject();

        data.setFileName(quoteService.getQuoteFullName(id) + fileGeneratingService.getExtension());
        data.setData(fileGeneratingService.generateFile(q));

        return new ResponseEntity<>(data, HttpStatus.CREATED);
    }
}
