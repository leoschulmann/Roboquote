package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InventoryItemToItemPositionConverterImplRus implements InventoryItemToItemPositionConverter {
    @Override
    public QuoteSection append(QuoteSection content, Item item) {
        Optional<ItemPosition> opt = content.getPositions()
                .stream()
                .filter(i -> i.getItemId() == item.getId())
                .findFirst();

        if (opt.isPresent()) opt.get().incrementQty();
        else {
            ItemPosition ip = new ItemPosition(item.getNameRus(),
                    item.getPartno(), item.getSellingPrice(), 1, item.getId());
            content.addItemPositions(ip);
        }
        return content;
    }
}
