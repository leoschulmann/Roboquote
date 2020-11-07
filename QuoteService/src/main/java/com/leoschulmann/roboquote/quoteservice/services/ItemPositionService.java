package com.leoschulmann.roboquote.quoteservice.services;

import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.exceptions.NoInventoryItemFound;

public interface ItemPositionService {
    ItemPosition getNewItemPosition(Integer inventoryId, Integer qty) throws NoInventoryItemFound;
}
