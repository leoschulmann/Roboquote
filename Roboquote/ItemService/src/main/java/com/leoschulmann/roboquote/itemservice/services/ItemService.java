package com.leoschulmann.roboquote.itemservice.services;

import com.leoschulmann.roboquote.itemservice.entities.Item;

import java.util.List;

@Deprecated
public interface ItemService {
    Item getItemById(int id);

    List<Item> getAll();

    List<Item> searchBy(String str);

    void deleteItemById(int id);

    Item saveItem(Item item);
}
