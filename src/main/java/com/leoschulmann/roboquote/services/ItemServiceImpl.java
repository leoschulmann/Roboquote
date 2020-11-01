package com.leoschulmann.roboquote.services;

import com.leoschulmann.roboquote.entities.Item;
import com.leoschulmann.roboquote.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    ItemRepository itemRepository;

    @Override
    public Item getItemById(int id) {
        return itemRepository.findById(id).orElse(null);
    }

    @Override
    public Item getItemByName(String name) {
        return itemRepository.findByNameRus(name);
    }

    @Override
    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    @Override
    public void addNewItem(Item item) {
        itemRepository.save(item);
    }
}
