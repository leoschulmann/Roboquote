package com.leoschulmann.roboquote.quoteservice.controllers;

import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.services.ItemPositionService;
import com.leoschulmann.roboquote.quoteservice.services.QuoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class QuoteController {

    private final QuoteService quoteService;

    private final ItemPositionService itemPositionService;

    public QuoteController(QuoteService quoteService, ItemPositionService itemPositionService) {
        this.quoteService = quoteService;
        this.itemPositionService = itemPositionService;
    }

    @PostMapping("/save")
    public ResponseEntity<Quote> saveQuote(@RequestBody Quote quote) {
        quote.getItemPositions().forEach(itemPosition -> itemPosition.setQuote(quote));
        Quote q = quoteService.saveQuote(quote);
        return new ResponseEntity<>(q, HttpStatus.CREATED);
    }

    @GetMapping("/add/{inventoryId}/{qty}")
    public ResponseEntity<ItemPosition> getNewItemPosition(@PathVariable Integer inventoryId, @PathVariable Integer qty) {
        ItemPosition i = itemPositionService.getNewItemPosition(inventoryId, qty);
        return new ResponseEntity<>(i, HttpStatus.OK);
    }
}
