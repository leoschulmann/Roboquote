package com.leoschulmann.roboquote.quoteservice.controllers;

import com.leoschulmann.roboquote.quoteservice.dto.DistinctTermsDto;
import com.leoschulmann.roboquote.quoteservice.dto.QuoteDto;
import com.leoschulmann.roboquote.quoteservice.services.QuoteService;
import com.leoschulmann.roboquote.quoteservice.validation.ExistingQuote;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class QuoteController {
    private final QuoteService quoteService;

    @PostMapping
    public ResponseEntity<Object> saveQuote(@RequestBody @Valid QuoteDto quoteDto) {
        Integer id = quoteService.saveQuote(quoteDto);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @GetMapping //returning only basic info, without sections or positions
    public ResponseEntity<List<QuoteDto>> findAll() {
        List<QuoteDto> quotes = quoteService.findAll();
        return new ResponseEntity<>(quotes, HttpStatus.OK);
    }

    @GetMapping("/uncancelled")
//returning only basic info, without sections or positions (returns projs with canceled=false only)
    public ResponseEntity<List<QuoteDto>> findAllUncacelled() {
        List<QuoteDto> quotes = quoteService.findAllUncancelled();
        return new ResponseEntity<>(quotes, HttpStatus.OK);
    }

    @GetMapping("/{id}") // returning all info on quote
    public ResponseEntity<QuoteDto> getQuote(@PathVariable @ExistingQuote int id) {
        QuoteDto dto = quoteService.getById(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping("/comment/{id}")
    public ResponseEntity<Object> addComment(@PathVariable @ExistingQuote int id,
                                             @RequestBody @Size(max = 255) String comment) {
        quoteService.addComment(id, comment);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<Object> cancelQuote(@PathVariable @ExistingQuote int id, @RequestBody boolean action) {
        quoteService.setQuoteCancelled(id, action);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/terms")
    public ResponseEntity<DistinctTermsDto> getDistinctTerms() {
        return new ResponseEntity<>(quoteService.getDistinctQuoteTerms(), HttpStatus.OK);
    }

    @GetMapping("/foritem/{id}")
    public ResponseEntity<List<QuoteDto>> findAllForItemId(@PathVariable int id) {
        List<QuoteDto> quotes = quoteService.findAllForItemId(id);
        return new ResponseEntity<>(quotes, HttpStatus.OK);
    }
}
