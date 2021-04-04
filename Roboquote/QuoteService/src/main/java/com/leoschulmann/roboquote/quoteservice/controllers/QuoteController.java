package com.leoschulmann.roboquote.quoteservice.controllers;

import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.services.QuoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Deprecated
public class QuoteController {

    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @PostMapping("/save")
    public ResponseEntity<Quote> saveQuote(@RequestBody Quote quote) {
        Quote q = quoteService.saveQuote(quote);
        return new ResponseEntity<>(q, HttpStatus.CREATED);
    }

    @GetMapping("/quote")
    public List<Quote> findAll() {
        return quoteService.findAll();
    }}
