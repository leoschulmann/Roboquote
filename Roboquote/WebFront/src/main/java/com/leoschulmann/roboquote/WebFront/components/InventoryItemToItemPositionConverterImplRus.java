package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import org.springframework.stereotype.Service;

@Service
public class InventoryItemToItemPositionConverterImplRus implements InventoryItemToItemPositionConverter {
    @Override
    public ItemPosition convert(Item item) {
        return new ItemPosition(item.getNameRus(),
                item.getPartno(),
                item.getSellingPrice(),
                1,
                item.getId());
    }
}
