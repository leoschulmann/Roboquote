package com.leoschulmann.roboquote.services;

import com.leoschulmann.roboquote.entities.Item;

import java.util.List;

public interface ItemService {
    Item getItemById(int id);

    Item getItemByName(String name);

    List<Item> getAll();

    void addNewItem(Item item);
}
