package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;

public interface InventoryItemHelper {
    QuoteSection append(QuoteSection content, Item inventoryItem);
}
