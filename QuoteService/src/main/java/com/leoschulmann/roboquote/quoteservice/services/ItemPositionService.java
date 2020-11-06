package com.leoschulmann.roboquote.quoteservice.services;

import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;

public interface ItemPositionService {
    ItemPosition getNewItemPosition(Integer inventoryId, Integer qty);
}
