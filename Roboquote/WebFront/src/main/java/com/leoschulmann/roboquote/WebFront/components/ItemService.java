package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.itemservice.entities.Item;

import java.util.List;

public interface ItemService {
    List<Item> findAll();

    void saveItem(Item item);
}
