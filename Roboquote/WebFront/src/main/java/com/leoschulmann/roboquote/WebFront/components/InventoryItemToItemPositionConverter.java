package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;

public interface InventoryItemToItemPositionConverter {
    ItemPosition convert(Item inventoryItem);
}
