package com.leoschulmann.roboquote.quoteservice.services;

import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.exceptions.NoInventoryItemFound;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ItemPositionServiceImpl implements ItemPositionService {

    @Value("${itemservice.url}")
    String url;

    @Override
    public ItemPosition getNewItemPosition(Integer inventoryId, Integer qty) throws NoInventoryItemFound {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Item> responseEntity = restTemplate.getForEntity(
                url + inventoryId, Item.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            ItemPosition i = new ItemPosition();
            Item item = responseEntity.getBody(); //todo check
            i.setItemId(item.getId());
            i.setSellingPrice(item.getSellingPrice());
            i.setName(item.getNameRus());  //todo do not forget to make changeable
            i.setQty(qty);
            return i;
        } else throw new NoInventoryItemFound(inventoryId);
    }
}
