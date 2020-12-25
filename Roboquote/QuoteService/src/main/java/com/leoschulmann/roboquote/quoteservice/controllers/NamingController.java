package com.leoschulmann.roboquote.quoteservice.controllers;

import com.leoschulmann.roboquote.quoteservice.services.NameGeneratingService;
import com.leoschulmann.roboquote.quoteservice.services.QuoteService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/name")
public class NamingController {

    final NameGeneratingService generatingService;

    final QuoteService quoteService;

    public NamingController(@Qualifier(value = "nameGeneratingServiceImpl") NameGeneratingService generatingService, QuoteService quoteService) {
        this.generatingService = generatingService;
        this.quoteService = quoteService;
    }

    @GetMapping
    public ResponseEntity<String> generateName() {
        return new ResponseEntity<>(generatingService.generate(), HttpStatus.OK);
    }

    @GetMapping("/{serial}")
    public ResponseEntity<Integer> generateVersion(@PathVariable Integer serial) {
        return new ResponseEntity<>(generatingService.generateVer(String.valueOf(serial)), HttpStatus.OK);
    }

    @GetMapping("/forid/{id}")
    private ResponseEntity<String> getFullNameForQuote(@PathVariable int id) {
        return new ResponseEntity<>(quoteService.getQuoteFullName(id), HttpStatus.OK);
    }
}
