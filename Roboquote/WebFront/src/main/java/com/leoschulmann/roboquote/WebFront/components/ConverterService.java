package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.itemservice.entities.BundledPosition;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import org.springframework.stereotype.Service;

@Service
public class ConverterService {
    public ItemPosition convertBundledPositionToItemPosition(BundledPosition bp) {
        return new ItemPosition(bp.getItem().getNameRus(), bp.getItem().getPartno(), bp.getItem().getSellingPrice(),
                bp.getQty(), bp.getItem().getId());
    }

    public BundledPosition convertToBundlePosition(Item item) {
        return new BundledPosition(1, item);
    }

    public ItemPosition convertItemToItemPosition(Item item) {
        return new ItemPosition(item.getNameRus(), item.getPartno(), item.getSellingPrice(), 1, item.getId());
    }
}
