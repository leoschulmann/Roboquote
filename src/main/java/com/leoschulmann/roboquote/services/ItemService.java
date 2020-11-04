package com.leoschulmann.roboquote.services;

import com.leoschulmann.roboquote.entities.Item;

import java.util.List;

public interface ItemService {
    Item getItemById(int id);

    List<Item> getAll();

    void addNewItem(Item item);

    List<Item> searchBy(String str);
}
