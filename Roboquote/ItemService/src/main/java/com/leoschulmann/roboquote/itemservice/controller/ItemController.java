package com.leoschulmann.roboquote.itemservice.controller;

import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.itemservice.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    ItemService itemService;

    @GetMapping("/")
    List<Item> findAll() {
        return itemService.getAll();
    }

    @GetMapping("/{id}")
    Item findById(@PathVariable int id) {
        return itemService.getItemById(id);
    }

    @PostMapping("/")
    void addNewItem(@RequestBody Item item) {
        itemService.addNewItem(item);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Item> deleteItem(@PathVariable int id) {
        itemService.deleteItemById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/")
    ResponseEntity<Item> updateItem(@RequestBody Item item) {
        Item anItem = itemService.updateItem(item);
        return new ResponseEntity<>(anItem, HttpStatus.OK);
    }

    @GetMapping("/search/{str}")
    List<Item> search(@PathVariable String str) {

        return itemService.searchBy(str);
    }
}
