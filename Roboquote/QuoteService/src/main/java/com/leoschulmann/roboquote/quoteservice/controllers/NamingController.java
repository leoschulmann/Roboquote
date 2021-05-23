package com.leoschulmann.roboquote.quoteservice.controllers;

import com.leoschulmann.roboquote.quoteservice.services.NameGeneratingService;
import com.leoschulmann.roboquote.quoteservice.services.QuoteService;
import com.leoschulmann.roboquote.quoteservice.validation.ExistingQuote;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/name")
@RequiredArgsConstructor
@Validated
public class NamingController {

    private final NameGeneratingService generatingService;
    private final QuoteService quoteService;

    @GetMapping
    public ResponseEntity<String> generateName() {
        return new ResponseEntity<>(generatingService.generate(), HttpStatus.OK);
    }

    @GetMapping("/{serial}")
    public ResponseEntity<Integer> generateVersion(@PathVariable String serial) {
        return new ResponseEntity<>(generatingService.generateVer(String.valueOf(serial)), HttpStatus.OK);
    }

    @GetMapping("/forid/{id}")
    public ResponseEntity<String> getFullNameForQuote(@PathVariable @ExistingQuote int id) {
        return new ResponseEntity<>(quoteService.getQuoteFullName(id), HttpStatus.OK);
    }
}
