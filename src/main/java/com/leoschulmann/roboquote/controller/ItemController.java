package com.leoschulmann.roboquote.controller;

import com.leoschulmann.roboquote.entities.Item;
import com.leoschulmann.roboquote.services.ItemServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    ItemServiceImpl itemServiceImpl;

    @GetMapping("/")
    List<Item> findAll() {
        return itemServiceImpl.getAll();
    }

    @GetMapping("/{id}")
    Item findById(@PathVariable int id) {
        return itemServiceImpl.getItemById(id);
    }

    @PostMapping("/")
    void addNewItem(@RequestBody Item item) {
        itemServiceImpl.addNewItem(item);
    }
}
