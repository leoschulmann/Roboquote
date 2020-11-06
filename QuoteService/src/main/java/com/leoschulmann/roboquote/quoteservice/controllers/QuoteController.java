package com.leoschulmann.roboquote.quoteservice.controllers;

import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.services.ItemPositionService;
import com.leoschulmann.roboquote.quoteservice.services.QuoteService;
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
    public void saveQuote(@RequestBody Quote quote) {
        quote.getItemPositions().forEach(itemPosition -> itemPosition.setQuote(quote));
        quoteService.saveQuote(quote);
    }

    @GetMapping("/add/{inventoryId}/{qty}")
    public ItemPosition getNewItemPosition(@PathVariable Integer inventoryId, @PathVariable Integer qty) {
        return itemPositionService.getNewItemPosition(inventoryId, qty);
    }
}
