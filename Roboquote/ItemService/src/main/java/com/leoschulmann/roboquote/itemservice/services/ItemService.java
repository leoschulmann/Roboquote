package com.leoschulmann.roboquote.itemservice.services;

import com.leoschulmann.roboquote.itemservice.entities.Item;

import java.util.List;

public interface ItemService {
    Item getItemById(int id);

    List<Item> getAll();

    void addNewItem(Item item);

    List<Item> searchBy(String str);
}
